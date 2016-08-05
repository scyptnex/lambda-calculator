package io.github.scyptnex.lcalc.expression;

public class Fun implements Term{

    private Var head;
    private Term body;

    @Override
    public String toDisplayString() {
        return String.format("(\\%s.%s)", head.toDisplayString(), body.toDisplayString());
    }
}
