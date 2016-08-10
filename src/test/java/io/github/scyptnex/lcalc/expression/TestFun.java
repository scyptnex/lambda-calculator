package io.github.scyptnex.lcalc.expression;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestFun {

    @Test
    public void bindsVariableToBody(){
        Var h = new Var("head");
        Term b = new Var("body");
        Fun f = new Fun(h, b);
        assertThat(f.getHead(), is(h));
        assertThat(f.getBody(), is(b));
    }
}
