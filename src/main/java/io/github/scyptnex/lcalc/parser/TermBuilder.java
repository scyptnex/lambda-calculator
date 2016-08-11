package io.github.scyptnex.lcalc.parser;

import io.github.scyptnex.lcalc.expression.App;
import io.github.scyptnex.lcalc.expression.Fun;
import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Var;
import io.github.scyptnex.lcalc.parser.gen.UntypedBaseVisitor;
import io.github.scyptnex.lcalc.parser.gen.UntypedParser;

import java.util.Iterator;

public class TermBuilder extends UntypedBaseVisitor<Term> {

    private TermBuilder(){
        // override default public constructor
    }

    public static Term build(UntypedParser.ExpressionContext expr){
        return new TermBuilder().visit(expr);
    }

    @Override
    public Term visitAbstraction(UntypedParser.AbstractionContext ctx) {
        return recurseBuild(ctx.var().iterator(), this.visit(ctx.expression()));
    }

    private Term recurseBuild(Iterator<UntypedParser.VarContext> it, Term body){
        return it.hasNext() ? new Fun(visitVar(it.next()), recurseBuild(it, body)) : body;
    }

    @Override
    public Term visitChain(UntypedParser.ChainContext ctx) {
        Term ret = null;
        for(UntypedParser.UnitContext uc : ctx.unit()){
            Term cur = this.visit(uc);
            if(ret == null) ret = cur;
            else ret = new App(ret, cur);
        }
        if(ctx.expression() != null){
            ret = new App(ret, this.visit(ctx.expression()));
        }
        return ret;
    }

    @Override
    public Term visitVariable(UntypedParser.VariableContext ctx) {
        return this.visit(ctx.var());
    }

    @Override
    public Term visitSubExpression(UntypedParser.SubExpressionContext ctx) {
        return this.visit(ctx.expression());
    }

    @Override
    public Var visitVar(UntypedParser.VarContext ctx) {
        return new Var(ctx.ID().getText());
    }
}
