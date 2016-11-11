package io.github.scyptnex.lcalc.expression;

import java.util.HashSet;
import java.util.Set;

/**
 * Utilities for expressions
 */
public class Util {

    public static BoundFree getBoundFree(Term t){
        return new BoundFree(t);
    }

    public static class BoundFree implements Visitor<Void, Void>{
        public final Set<Var> bound;
        public final Set<Var> free;
        private BoundFree(Term t){
            bound = new HashSet<>();
            free = new HashSet<>();
            this.visit(null, t);
        }

        @Override
        public Void visitApp(Void aVoid, App t) {
            this.visit(null, t.getLhs());
            this.visit(null, t.getRhs());
            return null;
        }

        @Override
        public Void visitFun(Void aVoid, Fun t) {
            this.visit(null, t.getBody());
            free.remove(t.getHead());
            bound.add(t.getHead());
            return null;
        }

        @Override
        public Void visitVar(Void aVoid, Var t) {
            free.add(t);
            return null;
        }
    }

}
