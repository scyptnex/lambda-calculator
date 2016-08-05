package io.github.scyptnex.lcalc.expression;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestVar {

    @Test
    public void varHasName(){
        assertThat(new Var("foo").getBaseName(), is("foo"));
    }

    @Test
    public void varHasVersion(){
        Var v = new Var("foo");
        assertThat(v.getAlphaVersion(), is(0));
        v.setAlphaVersion(3);
        assertThat(v.getAlphaVersion(), is(3));
    }
}
