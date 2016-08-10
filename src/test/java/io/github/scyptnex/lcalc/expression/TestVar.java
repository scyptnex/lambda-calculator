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

    @Test
    public void varHasVersion(){
        Var v = new Var("foo");
        assertThat(v.getAlphaVersion(), is(0));
        v.setAlphaVersion(3);
        assertThat(v.getAlphaVersion(), is(3));
    }

    @Test
    public void versionZeroIsNotDifferentFromVarName(){
        Var v = new Var("foo");
        v.setAlphaVersion(0);
        assertThat(v.toString(), is("foo"));
    }

    @Test
    public void differentVersionsHaveDifferentStrings(){
        Var v = new Var("foo");
        Set<String> nms = new HashSet<>();
        for(int i=0; i<10; i++){
            v.setAlphaVersion(i);
            nms.add(v.toString());
        }
        assertThat("All 10 strings must be distinct", nms.size(), is(10));
    }
}
