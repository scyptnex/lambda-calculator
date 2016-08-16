package io.github.scyptnex.lcalc.parser;

import io.github.scyptnex.lcalc.expression.*;
import io.github.scyptnex.lcalc.parser.gen.UntypedBaseVisitor;
import io.github.scyptnex.lcalc.parser.gen.UntypedParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TermBuilder extends UntypedBaseVisitor<Term> {

    private TermBuilder(){
        // override default public constructor
    }

    public static Term build(UntypedParser.ExpressionContext expr){
        return new Unifier().visit(new HashMap<>(), new TermBuilder().visit(expr));
    }

    private static class Unifier implements Visitor<Map<String, Var>, Term> {

        @Override
        public Term visitApp(Map<String, Var> stringVarMap, App t) {
            Map<String, Var> dupl = new HashMap<>(stringVarMap);
            return new App(this.visit(stringVarMap, t.getLhs()), this.visit(dupl, t.getRhs()));
        }

        @Override
        public Term visitFun(Map<String, Var> stringVarMap, Fun t) {
            stringVarMap.put(t.getHead().getBaseName(), t.getHead());
            return new Fun(t.getHead(), this.visit(stringVarMap, t.getBody()));
        }

        @Override
        public Term visitVar(Map<String, Var> stringVarMap, Var t) {
            return stringVarMap.getOrDefault(t.getBaseName(), t);
        }
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
