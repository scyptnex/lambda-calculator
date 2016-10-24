package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.*;
import io.github.scyptnex.lcalc.util.Bi;

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
    private final Util.BoundFree bf;
    private final Map<String, Term> definitions;

    private TransformationFinder(Term base, Map<String, Term> definitions) {
        this.base = base;
        this.bf = Util.getBoundFree(base);
        this.definitions = definitions;
    }

    private Optional<TransformationEvent> result(){
        Optional<TransformationEvent> ret = Optional.empty();
        if(!ret.isPresent()) ret = new FunctionApplier().visit(null, base);
        if(!ret.isPresent()) ret = new VarSubstitutor().visit(null, base);
        return ret;
    }

    /**
     * For performing beta substitution when the lhs of an application is a lambda
     */
    private class FunctionApplier implements Visitor<Void, Optional<TransformationEvent>>{

        @Override
        public Optional<TransformationEvent> visitApp(Void aVoid, App t) {
            // first, is this application valid
            if(t.getLhs() instanceof Fun){
                Fun lhs = (Fun) t.getLhs();
                Util.BoundFree rbf = Util.getBoundFree(t.getRhs()), lbf = Util.getBoundFree(lhs.getBody());
                // i'm conservatively renaming anything that could conflict
                Set<String> namesInBody = Stream.concat(lbf.bound.stream(), lbf.free.stream())
                        .filter(v -> !v.equals(lhs.getHead())) // conflicts with the binding var (itself) don't matter
                        .map(Var::getBaseName)
                        .collect(Collectors.toSet());
                // at this point we know the optional exists
                return Optional.of(Stream.concat(rbf.bound.stream(), rbf.free.stream())
                        .map(v -> new Bi<>(v, v.getBaseName()))
                        .filter(b -> namesInBody.contains(b.second))
                        .findAny()                                        // if there is any name conflict
                        .filter(n -> !definitions.containsKey(n.second))  // which is not for a definition
                        .map(n -> chooseAlpha(n.first, namesInBody, rbf)) // map it to an alpha transform and return
                        .orElseGet(() -> TransformationEvent.makeBeta(base, (Fun)t.getLhs(), t.getRhs())));
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
     * @param conflict the conflicting variable
     * @param taken the set of taken names (i.e. bound names in the body, excluding the binding name
     * @param renamableTermVars the boundfree of the renaming set, so i don't have to find it again
     * @return an Alpha transform event
     */
    private TransformationEvent chooseAlpha(Var conflict, Set<String> taken, Util.BoundFree renamableTermVars){
        // you also cant conflict with your own names
        taken = Stream.concat(taken.stream(),
                    Stream.concat(
                        renamableTermVars.bound.stream(),
                        renamableTermVars.free.stream())
                        .map(Var::getBaseName))
                .collect(Collectors.toSet());

        // remove any suffix from the name
        String newName = conflict.getBaseName();
        if(newName.contains("'")){
            newName = newName.substring(0, newName.indexOf("'"));
        }

        //find the lowest name that does not currently exist
        for(int i=0; ; i++){
            String sfx = i>0 ? (i>1 ? "'" + (i-1) : "'") : "";
            if(!taken.contains(newName + sfx)){
                return TransformationEvent.makeAlpha(base, conflict, new Var(newName + sfx));
            }
        }
    }

    /**
     * For performing delta substitution when an identifier is defined in the map of variables
     */
    private class VarSubstitutor implements Visitor<Void, Optional<TransformationEvent>>{

        @Override
        public Optional<TransformationEvent> visitApp(Void v, App t) {
            if(t.getLhs() instanceof Var){
                String nm = ((Var) t.getLhs()).getBaseName();
                if(definitions.containsKey(nm) && bf.free.contains(t.getLhs())){
                    return Optional.of(TransformationEvent.makeDelta(base, (Var)t.getLhs(), definitions.get(nm)));
                }
            }
            Optional<TransformationEvent> left = visit(null, t.getLhs());
            if(left.isPresent()) return left;
            return visit(null, t.getRhs());
        }

        @Override
        public Optional<TransformationEvent> visitFun(Void v, Fun t) {
            return visit(null, t.getBody());
        }

        @Override
        public Optional<TransformationEvent> visitVar(Void v, Var t) {
            return Optional.empty();
        }
    }

}
