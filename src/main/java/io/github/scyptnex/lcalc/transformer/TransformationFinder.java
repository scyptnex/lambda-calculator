package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * finds potential transformations in the given term, dependant on the given map
 */
public class TransformationFinder {

    public static Optional<TransformationEvent> find(Term t, Map<String, Term> defs){
        return new TransformationFinder(t, defs).result();
    }

    private final Term base;
    private final Map<String, Term> definitions;

    private TransformationFinder(Term base, Map<String, Term> definitions) {
        this.base = base;
        this.definitions = definitions;
    }

    private Optional<TransformationEvent> result(){
        Optional<TransformationEvent> ret = Optional.empty();
        if(!ret.isPresent()) ret = new FunctionApplier().visit(null, base);
        if(!ret.isPresent()) ret = new VarSubstitutor().visit(Collections.emptySet(), base);
        return ret;
    }

    /**
     * For performing beta substitution when the lhs of an application is a lambda
     */
    private class FunctionApplier implements Visitor<Void, Optional<TransformationEvent>>{

        @Override
        public Optional<TransformationEvent> visitApp(Void aVoid, App t) {
            // first, is this application valid
            // TODO bail out when there are name conflicts
            if(t.getLhs() instanceof Fun){

                return Optional.of(TransformationEvent.makeBeta(base, (Fun)t.getLhs(), t.getRhs()));
            }
            Optional<TransformationEvent> left = visit(null, t.getLhs());
            if(left.isPresent()) return left;
            else return visit(null, t.getRhs());
        }

        @Override
        public Optional<TransformationEvent> visitFun(Void aVoid, Fun t) {
            return visit(null, t.getBody());
        }

        @Override
        public Optional<TransformationEvent> visitVar(Void aVoid, Var t) {
            return Optional.empty();
        }
    }

    /**
     * For performing alpha substitution when an identifier is defined in the map of variables
     */
    private class VarSubstitutor implements Visitor<Set<String>, Optional<TransformationEvent>>{

        @Override
        public Optional<TransformationEvent> visitApp(Set<String> avoid, App t) {
            if(t.getLhs() instanceof Var){
                String nm = ((Var) t.getLhs()).getBaseName();
                if(definitions.containsKey(nm)){
                    return Optional.of(TransformationEvent.makeAlpha(base, (Var)t.getLhs(), definitions.get(nm)));
                }
            }
            Optional<TransformationEvent> left = visit(avoid, t.getLhs());
            if(left.isPresent()) return left;
            return visit(avoid, t.getRhs());
        }

        @Override
        public Optional<TransformationEvent> visitFun(Set<String> avoid, Fun t) {
            // a variable bound locally to a function can be name-swapped for one defined elsewhere (i.e. only free vars can)
            return visit(Stream.concat(avoid.stream(), Stream.of(t.getHead().getBaseName())).collect(Collectors.toSet()), t.getBody());
        }

        @Override
        public Optional<TransformationEvent> visitVar(Set<String> avoid, Var t) {
            return Optional.empty();
        }
    }

}
