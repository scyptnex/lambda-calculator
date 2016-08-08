package io.github.scyptnex.lcalc.expression;

public class App implements Term{

    private Term lhs;
    private Term rhs;

    @Override
    public String toDisplayString() {
        return String.format("(%s %s)", lhs.toDisplayString(), rhs.toDisplayString());
    }

    public Term getLhs() {
        return lhs;
    }

    public Term getRhs() {
        return rhs;
    }
}
