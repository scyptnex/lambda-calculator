package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.*;
import io.github.scyptnex.lcalc.output.TextPrinter;
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

    public static final int SIMPLIFY_MAX_ITERS = 500;

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
     * exists return it (as a transformation event (sigma). If it is new, include the
     * computation used to find it
     */
    public Optional<Bi<TransformationEvent, Optional<Computer>>> findCandidate(Term t){
        return computeTrueCandidate(t).map(b -> new Bi<>(new TransformationEvent.Sigma(t, b.first, new Var(known.get(desc(b.first)))), b.second));
    }

    /**
     * Iteratively finds candidates and simplify-computes them, or seeks a new one if the simplification fails
     * @param t the term to simplify
     * @return the application that can be simplified and (if it is new) a computation to simplify it
     */
    private Optional<Bi<App, Optional<Computer>>> computeTrueCandidate(Term t){
        while(true){
            Optional<App> potential = new CandidateFinder().visit(true, t);
            if(!potential.isPresent()) return Optional.empty();
            else {
                App a = potential.get();
                String d = desc(a);
                if(known.containsKey(d)) return Optional.of(new Bi<>(a, Optional.empty()));
                Computer cmp = Computer.compute(a, SIMPLIFY_MAX_ITERS, definitions);
                for(String def : definitions.keySet()){
                    if ((cmp.result instanceof Var && ((Var) cmp.result).getBaseName().equals(def))
                            || isAlphaEquivalent(cmp.result, definitions.get(def))) {
                        known.put(d, def);
                        return Optional.of(new Bi<>(a, Optional.of(cmp)));
                    }
                }
                known.put(d, null); // null definition means no simplification.  TODO what if there are new definitions?
            }
        }
    }

    /**
     * Determines if two terms are alpha-equivalent (i.e. you can rename one to make the other)
     * @param a one of the terms
     * @param b the other term
     * @return true if a can have its variables renamed to create b
     */
    public boolean isAlphaEquivalent(Term a, Term b){
        return new AlphaEquivocator().visit(a, b);
    }

    /**
     * The descriptor used to identify an app that can be simplified
     */
    private String desc(String l, String r){
        return l + " " + r;
    }

    private String desc(App a){
        return desc(((Var)a.getLhs()).getBaseName(), ((Var)a.getRhs()).getBaseName());
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
     * Discovers potential simplification targets, if a target could work, it tries to compute it
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
