package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.*;
import io.github.scyptnex.lcalc.util.Bi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Discovers sigma transformations, and attempts to solve them
 *
 * A sigma transformation is a simplifying transformation which is NOT left-most
 * I.e. we handle the left-most simplification with normal evaluations, this is for simplifying other terms
 */
public class Simplifier {

    private final Map<String, Term> definitions;
    private final Map<String, String> known;

    public Simplifier(Map<String, Term> defs){
        definitions = defs;
        known = new HashMap<>();
    }

    /**
     * Finds a simplification transformation for the given term
     * @param t The input term
     * @return Optional.none() if there are no simplification opportunities, if an opportunity
     * exists return it, and if this opportunity had not been discovered before, include the
     * computation used to find it
     */

    public Optional<App> findCandidate(Term t){
        return new CandidateFinder().visit(true, t);
    }

    public boolean isAlphaEquivalent(Term a, Term b){
        return new AlphaEquivocator().visit(a, b);
    }

    /**
     * The descriptor used to identify an app that can be simplified
     */
    private String desc(String l, String r){
        return l + " " + r;
    }

    /**
     * Determines if two terms are alpha equivalent
     */
    private class AlphaEquivocator implements Visitor<Term, Boolean> {
        Map<Var, Var> l_r = new HashMap<>();
        Map<Var, Var> r_l = new HashMap<>();
        @Override
        public Boolean visitApp(Term term, App t) {
            return term instanceof App
                    && visit(((App) term).getLhs(), t.getLhs())
                    && visit(((App) term).getRhs(), t.getRhs());
        }

        @Override
        public Boolean visitFun(Term term, Fun t) {
            return term instanceof Fun
                    && visit(((Fun) term).getHead(), t.getHead())
                    && visit(((Fun) term).getBody(), t.getBody());
        }

        @Override
        public Boolean visitVar(Term term, Var r) {
            if(term instanceof Var){
                Var l = (Var) term;
                if(!l_r.containsKey(l) && !r_l.containsKey(r)){
                    if(definitions.containsKey(l.getBaseName()) || definitions.containsKey(r.getBaseName())){
                        return l.getBaseName().equals(r.getBaseName());
                    }
                    l_r.put(l, r);
                    r_l.put(r, l);
                    return true;
                } else if (l_r.containsKey(l) && r_l.containsKey(r)){
                    return l_r.get(l) == r && r_l.get(r) == l;
                } else return false;
            }
            return false;
        }
    }

    /**
     * Discovers potential simplification targets
     */
    private class CandidateFinder implements Visitor<Boolean, Optional<App>> {
        @Override
        public Optional<App> visitApp(Boolean mightBeLeftMost, App t) {
            if(!mightBeLeftMost && t.getLhs() instanceof Var && t.getRhs() instanceof Var){
                String lb = ((Var) t.getLhs()).getBaseName();
                String rb = ((Var) t.getRhs()).getBaseName();
                if(definitions.containsKey(lb) && definitions.containsKey(rb)){
                    String nm = desc(lb, rb);
                    return known.containsKey(nm) && known.get(nm) == null ? Optional.empty() : Optional.of(t);
                }
            }
            Optional<App> left = visit(mightBeLeftMost, t.getLhs());
            if(left.isPresent()) return left;
            else return visit(false, t.getRhs());
        }

        @Override
        public Optional<App> visitFun(Boolean mightBeLeftMost, Fun t) {
            return visit(false, t.getBody());
        }

        @Override
        public Optional<App> visitVar(Boolean mightBeLeftMost, Var t) {
            return Optional.empty();
        }
    }
}
