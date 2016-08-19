package io.github.scyptnex.lcalc.parser;

import io.github.scyptnex.lcalc.expression.*;
import io.github.scyptnex.lcalc.parser.gen.UntypedLexer;
import io.github.scyptnex.lcalc.parser.gen.UntypedParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TestUntypedExpression {

    private Term parse(String s) throws Exception{
        UntypedLexer lex = new UntypedLexer(new ANTLRInputStream(s));
        CommonTokenStream cts = new CommonTokenStream(lex);
        UntypedParser par = new UntypedParser(cts);

        // We want to throw an error where possible
        par.setErrorHandler(new BailErrorStrategy());
        return TermBuilder.build(par.expression());

    }

    @Test
    public void parsesVar() throws Exception {
        Term t = parse("x");
        assertThat(t, is(instanceOf(Var.class)));
    }

    @Test
    public void parsesFun() throws Exception {
        Term t = parse("\\x.x");
        assertThat(t, is(instanceOf(Fun.class)));
    }

    @Test
    public void funAssociatesRight() throws Exception {
        Term t = parse("\\x y z.x");
        assertThat(((Fun)t).getHead().getBaseName(), is("x"));
        assertThat(((Fun)(((Fun)t).getBody())).getHead().getBaseName(), is("y"));
        assertThat(((Fun)((Fun)(((Fun)t).getBody())).getBody()).getHead().getBaseName(), is("z"));
    }

    @Test
    public void parsesApp() throws Exception {
        Term t = parse("x x");
        assertThat(t, is(instanceOf(App.class)));
    }

    @Test
    public void appAssociatesLeft() throws Exception {
        Term t = parse("a b c");
        assertThat(((Var)((App)t).getRhs()).getBaseName(), is("c"));
        assertThat(((Var)((App)((App)t).getLhs()).getLhs()).getBaseName(), is("a"));
        assertThat(((Var)((App)((App)t).getLhs()).getRhs()).getBaseName(), is("b"));
    }

    @Test
    public void parsesSubExpression() throws Exception {
        Term t = parse("(((x)))");
        assertThat(t, is(instanceOf(Var.class)));
    }

    @Test
    public void funOverridesApp() throws Exception {
        Term t = parse("a \\x . b c");
        assertThat(t, is(instanceOf(App.class)));
        assertThat(((App)t).getLhs(), is(instanceOf(Var.class)));
        assertThat(((App)t).getRhs(), is(instanceOf(Fun.class)));
    }

    @Test
    public void boundVariablesShareInstance() throws Exception {
        Fun f = (Fun)parse("\\x.x");
        Var h = f.getHead();
        Var b = (Var)f.getBody();
        assertTrue("not the same instance", h == b);
    }

    @Test
    public void doublyBoundNameSharesLocalInstance() throws Exception {
        Fun f = (Fun)parse("\\x.\\x.x");
        Var h = f.getHead();
        Fun b = (Fun)f.getBody();
        Var bh = b.getHead();
        Var bb = (Var)b.getBody();
        assertTrue("the two heads should be different", h != bh);
        assertTrue("the body differs from the outer head", bb != h);
        assertTrue("the body matches the inner head", bb == bh);
    }

    @Test
    public void doublyAppliedNamesDiffer() throws Exception {
        App a = (App)parse("x x");
        Var l = (Var)a.getLhs();
        Var r = (Var)a.getRhs();
        assertTrue("The left and right should be different", l != r);
    }

}
