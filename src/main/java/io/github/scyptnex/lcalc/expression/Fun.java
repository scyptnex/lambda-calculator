package io.github.scyptnex.lcalc.expression;

public class Fun implements Term{

    private Var head;
    private Term body;

    //@Override
    //public String toDisplayString() {
    //    return String.format("(\\%s.%s)", head.toDisplayString(), body.toDisplayString());
    //}

    public Fun(Var h, Term b){
        this.head = h;
        this.body = b;
    }

    public Var getHead() {
        return head;
    }

    public Term getBody() {
        return body;
    }

    @Override
    public <A, R> R visitMe(Visitor<A, R> vis, A arg) {
        return vis.visitFun(arg, this);
    }
}
