package io.github.scyptnex.lcalc.expression;

public class App implements Term{

    private Term lhs;
    private Term rhs;

    //@Override
    //public String toDisplayString() {
    //    return String.format("(%s %s)", lhs.toDisplayString(), rhs.toDisplayString());
    //}

    public App(Term l, Term r){
        this.lhs = l;
        this.rhs = r;
    }

    public Term getLhs() {
        return lhs;
    }

    public Term getRhs() {
        return rhs;
    }

    @Override
    public <A, R> R visitMe(Visitor<A, R> vis, A arg) {
        return vis.visitApp(arg, this);
    }
}
