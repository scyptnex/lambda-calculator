package io.github.scyptnex.lcalc.expression;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestVar {

    @Test
    public void varHasName(){
        assertThat(new Var("foo").getBaseName(), is("foo"));
    }

}
