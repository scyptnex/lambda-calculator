package io.github.scyptnex.lcalc.expression;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestApp {

    @Test
    public void storesLeftAndRight(){
        Term l = new Var("left");
        Term r = new Var("right");
        App a = new App(l, r);
        assertThat(a.getLhs(), is(l));
        assertThat(a.getRhs(), is(r));
    }
}
