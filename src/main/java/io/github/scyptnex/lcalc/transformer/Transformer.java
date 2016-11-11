package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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
            default /*DELTA*/: return new DuplicateReplace((Var)tev.relevantSubTerm, tev.transformation, true).visit(null, tev.totalTerm);
        }
    }

    /**
     * Delegates copy-replacement functinality to this subclass, so as to keep the interface clean
     */
    private class DuplicateReplace implements Visitor<Void, Term>{

        final Optional<Var> replaceMe;
        final Optional<Fun> applyMe;
        final Term withMe;
        final boolean alsoDuplicateReplacement;
        final Map<Var, Var> varMap; // allows us to keep equal references equal


        private DuplicateReplace(Var repl, Term with, boolean duplicateReplace){
            this(repl, new HashMap<>(), with, duplicateReplace);
        }

        private DuplicateReplace(Var repl, Map<Var, Var> currentMappings, Term with, boolean duplicateReplace){
            replaceMe = with == null || repl == null ? Optional.empty() : Optional.of(repl);
            applyMe = Optional.empty();
            withMe = with;
            alsoDuplicateReplacement = duplicateReplace;
            varMap = currentMappings;
        }

        private DuplicateReplace(Fun apl, Term with, boolean duplicateReplace){
            applyMe = with == null || apl == null ? Optional.empty() : Optional.of(apl);
            replaceMe = Optional.empty();
            withMe = with;
            alsoDuplicateReplacement = duplicateReplace;
            varMap = new HashMap<>();
        }

        private DuplicateReplace(Map<Var, Var> m){
            this(null, m, null, false);
        }

        @Override
        public Term visitApp(Void nul, App t) {
            if(applyMe.map(f -> t.getLhs().equals(f)).orElse(false)){
                Fun tf = (Fun)t.getLhs();
                // beta substitutions must respect renaming conventions that we have already found
                // otherwise if i'm beta substituting and i see a bound var, i would give it a new name, which is a bug
                return new DuplicateReplace(tf.getHead(), varMap, withMe, true).visit(null, tf.getBody());
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
                // TODO "? MULT THREE TWO" - this replaces all instances of the definition, but i'd like to lazily replace only the shallow ones
                return alsoDuplicateReplacement ? new DuplicateReplace(varMap).visit(null, withMe) : withMe;
            } else {
                if(!varMap.containsKey(t)){
                    varMap.put(t, new Var(t.getBaseName()));
                }
                return varMap.get(t);
            }
        }
    }
}
