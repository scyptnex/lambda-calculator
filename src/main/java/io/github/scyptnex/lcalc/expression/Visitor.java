package io.github.scyptnex.lcalc.expression;

import java.util.stream.Stream;

public interface Visitor<A, R> {

    R visitApp(A a, App t);

    R visitFun(A a, Fun t);

    R visitVar(A a, Var t);

    default R visit(A a, Term t){
        if (t instanceof App){
            return visitApp(a, (App)t);
        } else if (t instanceof Fun) {
            return visitFun(a, (Fun) t);
        } else {
            return visitVar(a, (Var) t);
        }
    }


    /**
     * Simple visitor which returns null by default
     */
    class Base<A, R> implements Visitor<A, R> {

        @Override
        public R visitApp(A a, App t) {
            return null;
        }

        @Override
        public R visitFun(A a, Fun t) {
            return null;
        }

        @Override
        public R visitVar(A a, Var t) {
            return null;
        }
    }

    abstract class Ordered<A, R> implements Visitor<A, Void> {

        protected final Visitor<A, R> sub;
        protected Stream.Builder<R> builder = Stream.builder();

        protected Ordered(Visitor<A, R> subord){
            this.sub = subord;
        }

        public Stream<R> visitAll(A a, Term t){
            builder = Stream.builder();
            this.visit(a, t);
            return builder.build();
        }
    }

    class PreOrder<A, R> extends Ordered<A, R> {

        public PreOrder(Visitor<A, R> sub){
            super(sub);
        }

        @Override
        public Void visitApp(A a, App t) {
            builder.accept(sub.visitApp(a, t));
            this.visit(a, t.getLhs());
            this.visit(a, t.getRhs());
            return null;
        }

        @Override
        public Void visitFun(A a, Fun t) {
            builder.accept(sub.visitFun(a, t));
            this.visit(a, t.getHead());
            this.visit(a, t.getBody());
            return null;
        }

        @Override
        public Void visitVar(A a, Var t) {
            builder.accept(sub.visitVar(a, t));
            return null;
        }
    }

    interface Recursive<R>{

        R visitApp(R lhs, R rhs, App a);

        R visitFun(R head, R body, Fun f);

        R visitVar(Var v);

        default R visit(Term t){
            if (t instanceof App){
                return visitApp(visit(((App) t).getLhs()), visit(((App) t).getRhs()), (App)t);
            } else if (t instanceof Fun) {
                return visitFun(visit(((Fun) t).getHead()), visit(((Fun) t).getBody()), (Fun)t);
            } else {
                return visitVar((Var) t);
            }
        }
    }
}
