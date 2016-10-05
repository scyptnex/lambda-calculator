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
        return new VarSubstitutor().visit(Collections.emptySet(), base);
    }

    public class VarSubstitutor implements Visitor<Set<String>, Optional<TransformationEvent>>{

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
