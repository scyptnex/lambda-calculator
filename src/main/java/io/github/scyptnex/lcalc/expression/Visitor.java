package io.github.scyptnex.lcalc.expression;

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
}
