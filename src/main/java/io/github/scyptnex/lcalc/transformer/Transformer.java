package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class that actually does the transformation given some TransformationEvent
 * Implements the visitor for "copy with replacement" semantics
 */
public class Transformer implements Function<TransformationEvent, Term> {

    @Override
    public Term apply(TransformationEvent tev) {
        // so it seems alpha and beta are meaningless...
        switch(tev.type){
            case ALPHA : return new DuplicateReplace((Var)tev.relevantSubTerm, tev.transformation, false).visit(null, tev.totalTerm);
            case BETA: return new DuplicateReplace((Fun)tev.relevantSubTerm, tev.transformation, false).visit(null, tev.totalTerm);
            case SIGMA: return new DuplicateReplace((App)tev.relevantSubTerm, (Var)tev.transformation).visit(null, tev.totalTerm);
            default /*DELTA*/: return new DuplicateReplace((Var)tev.relevantSubTerm, tev.transformation, true).visit(null, tev.totalTerm);
        }
    }

    /**
     * Delegates copy-replacement functinality to this subclass, so as to keep the interface clean
     */
    private class DuplicateReplace implements Visitor<Void, Term>{

        final Optional<Var> replaceMe;
        final Optional<Fun> applyMe;
        final Optional<App> simplifyMe;
        final Term withMe;
        final boolean alsoDuplicateReplacement;
        final Map<Var, Var> varMap; // allows us to keep equal references equal


        private DuplicateReplace(Var repl, Term with, boolean duplicateReplace){
            this(repl, new HashMap<>(), with, duplicateReplace);
        }

        private DuplicateReplace(App apl, Var res){
            replaceMe = Optional.empty();
            applyMe = Optional.empty();
            simplifyMe = Optional.of(apl);
            withMe = res;
            alsoDuplicateReplacement = true;
            varMap = new HashMap<>();
        }

        private DuplicateReplace(Var repl, Map<Var, Var> currentMappings, Term with, boolean duplicateReplace){
            replaceMe = with == null || repl == null ? Optional.empty() : Optional.of(repl);
            applyMe = Optional.empty();
            simplifyMe = Optional.empty();
            withMe = with;
            alsoDuplicateReplacement = duplicateReplace;
            varMap = currentMappings;
        }

        private DuplicateReplace(Fun apl, Term with, boolean duplicateReplace){
            replaceMe = Optional.empty();
            applyMe = with == null || apl == null ? Optional.empty() : Optional.of(apl);
            simplifyMe = Optional.empty();
            withMe = with;
            alsoDuplicateReplacement = duplicateReplace;
            varMap = new HashMap<>();
        }

        private DuplicateReplace(Map<Var, Var> m){
            this(null, m, null, false);
        }

        @Override
        public Term visitApp(Void nul, App t) {
            if(applyMe.map(f -> t.getLhs().equals(f)).orElse(false)) {
                Fun tf = (Fun) t.getLhs();
                // beta substitutions must respect renaming conventions that we have already found
                // otherwise if i'm beta substituting and i see a bound var, i would give it a new name, which is a bug
                return new DuplicateReplace(tf.getHead(), varMap, withMe, true).visit(null, tf.getBody());
            } else if(simplifyMe.map(a -> a.equals(t)).orElse(false)){
                return new DuplicateReplace(varMap).visit(null, withMe);
            } else {
                return new App(this.visit(null, t.getLhs()), this.visit(null, t.getRhs()));
            }
        }

        @Override
        public Term visitFun(Void nul, Fun t) {
            // if we allowed bound variables to be def-replaced, this would
            // error here because visitVar would return a term
            return new Fun((Var)this.visitVar(null, t.getHead()), this.visit(null, t.getBody()));

        }

        @Override
        public Term visitVar(Void nul, Var t) {
            if(replaceMe.map(t::equals).orElse(false)){
                if(alsoDuplicateReplacement){
                    // in case a variable is replaced multiple times, new variables in one should not be accessible in the other
                    Map<Var, Var> copy = varMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, HashMap::new));
                    return new DuplicateReplace(copy).visit(null, withMe);
                } else {
                    return withMe;
                }
            } else {
                if(!varMap.containsKey(t)){
                    varMap.put(t, new Var(t.getBaseName()));
                }
                return varMap.get(t);
            }
        }
    }

    public static class DebugRenamer implements Visitor<Map<String, Term>, Term>{

        public Map<Var, Var> newNames = new HashMap<>();

        @Override
        public Term visitApp(Map<String, Term> m, App t) {
            return new App(visit(m, t.getLhs()), visit(m, t.getRhs()));
        }

        @Override
        public Term visitFun(Map<String, Term> m, Fun t) {
            return new Fun((Var)visit(m, t.getHead()), visit(m, t.getBody()));
        }

        @Override
        public Term visitVar(Map<String, Term> m, Var t) {
            if(m.containsKey(t.getBaseName())) return t;
            if(!newNames.containsKey(t)){
                newNames.put(t, new Var("v" + newNames.size()));
            }
            return newNames.get(t);
        }
    }
}
