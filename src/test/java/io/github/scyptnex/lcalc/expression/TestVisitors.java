package io.github.scyptnex.lcalc.expression;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class TestVisitors {

    @Test
    public void baseVisitorReturnsNullWhenUndefined(){
        Visitor.Base<String, Integer> bv = new Visitor.Base<>();
        assertThat(bv.visit("", new Var("foo")), is(nullValue()));
        assertThat(bv.visit("", new Fun(new Var("a"), new Var("b"))), is(nullValue()));
        assertThat(bv.visit("", new App(new Var("x"), new Var("y"))), is(nullValue()));
    }

    public static final Visitor<Void, Term> echoVisitor = new Visitor<Void, Term>(){
        public Term visitApp(Void o, App t) {
            return t;
        }
        public Term visitFun(Void o, Fun t) {
            return t;
        }
        public Term visitVar(Void o, Var t) {
            return t;
        }
    };

    @Test
    public void preOrderBaseCase(){
        Visitor.PreOrder<Void, Term> pre = new Visitor.PreOrder<>(echoVisitor);
        Term expr = new Var("a");
        assertThat(pre.visitAll(null, expr).collect(Collectors.toList()), is(Collections.singletonList(expr)));
    }

    @Test
    public void preOrderFunHeadBody(){
        Visitor.PreOrder<Void, Term> pre = new Visitor.PreOrder<>(echoVisitor);
        Var h = new Var("x");
        Term b = new Var("y");
        Term expr = new Fun(h, b);
        assertThat(pre.visitAll(null, expr).collect(Collectors.toList()), is(Arrays.asList(expr, h, b)));
    }

    @Test
    public void preOrderAppLhsRhs(){
        Visitor.PreOrder<Void, Term> pre = new Visitor.PreOrder<>(echoVisitor);
        Term l = new Var("x");
        Term r = new Var("y");
        Term expr = new App(l, r);
        assertThat(pre.visitAll(null, expr).collect(Collectors.toList()), is(Arrays.asList(expr, l, r)));
    }

}
