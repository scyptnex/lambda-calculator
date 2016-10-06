package io.github.scyptnex.lcalc.expression;

import io.github.scyptnex.lcalc.parser.TestUntypedExpression;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestUtil {

    private Set<Term> setof(Term...ts){
        return Arrays.stream(ts).collect(Collectors.toSet());
    }

    @Test
    public void stupidMandatoryCoverage(){
        new Util();
    }

    @Test
    public void singleVarIsFree() {
        Term t = new Var("foo");
        Util.BoundFree bf = Util.getBoundFree(t);
        assertThat(bf.bound, is(setof()));
        assertThat(bf.free, is(setof(t)));
    }

    @Test
    public void simpleAppsAreFree() {
        Term l = new Var("foo");
        Term r = new Var("bar");
        Term t = new App(l, r);
        Util.BoundFree bf = Util.getBoundFree(t);
        assertThat(bf.bound, is(setof()));
        assertThat(bf.free, is(setof(l, r)));
    }

    @Test
    public void functionsBindHead() {
        Var x = new Var("foo");
        Term t = new Fun(x, x);
        Util.BoundFree bf = Util.getBoundFree(t);
        assertThat(bf.bound, is(setof(x)));
        assertThat(bf.free, is(setof()));
    }

    @Test
    public void freeOnOuterEvenIfBoundOnInner() throws Exception {
        App t = (App)TestUntypedExpression.parse("foo \\foo.foo");
        Util.BoundFree bf = Util.getBoundFree(t);
        assertThat(bf.bound, is(setof(((Fun)t.getRhs()).getHead())));
        assertThat(bf.free, is(setof(t.getLhs())));
    }

}
