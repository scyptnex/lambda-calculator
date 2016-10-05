package io.github.scyptnex.lcalc.expression;

public interface Term {

    <A, R> R visitMe(Visitor<A, R> vis, A arg);

}
