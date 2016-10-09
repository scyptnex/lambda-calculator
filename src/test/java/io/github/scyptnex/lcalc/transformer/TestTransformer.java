package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.App;
import io.github.scyptnex.lcalc.expression.Fun;
import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Var;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestTransformer {

    @Test
    public void alphaDoesNotDuplicate(){
        Var from = new Var("foo");
        Var to = new Var("bar");
        Fun wrap = new Fun(from, from);
        TransformationEvent tev = TransformationEvent.makeAlpha(wrap, from, to);
        Fun out = (Fun)new Transformer().apply(tev);
        assertThat(out.getHead(), is(to));
        assertThat(out.getBody(), is(to));
    }

    @Test
    public void alphaDuplicatesUnrelatedTerms(){
        Var from = new Var("foo");
        Var to = new Var("bar");
        Fun wrap = new Fun(from, from);
        TransformationEvent tev = TransformationEvent.makeAlpha(wrap, from, to);
        Term out = new Transformer().apply(tev);
        assertThat(out, is(not(wrap)));
    }

    @Test
    public void betaDoesWhatItSaysOnTheTin(){
        Var v1 = new Var("x");
        Fun ident = new Fun(v1, v1);
        Var v2 = new Var("y");
        App total = new App(ident, v2);
        TransformationEvent tev = TransformationEvent.makeBeta(total, ident, v2);
        Var ret = (Var)new Transformer().apply(tev);
        assertThat(ret, is(not(v2)));
        assertThat(ret.getBaseName(), is(v2.getBaseName()));
    }

    @Test
    public void betaPreservesUnrelatedVars(){
        Var important = new Var("important");
        Var replaceMe = new Var("replaceMe");
        Fun pseudoResult = new Fun(important, new App(replaceMe, important));
        App t = new App(new Fun(replaceMe, pseudoResult), new Var("withMe"));
        TransformationEvent tev = TransformationEvent.makeBeta(t, (Fun)t.getLhs(), t.getRhs());
        Fun f = (Fun)new Transformer().apply(tev);
        assertThat(f, is(not(pseudoResult)));
        assertThat(f.getHead(), is(not(important)));
        assertThat(f.getHead().getBaseName(), is(important.getBaseName()));
        assertThat(((App)f.getBody()).getRhs(), is(f.getHead()));
    }

    @Test
    public void deltaDuplicatesAllTerms(){
        Var body = new Var("foo");
        Var def = new Var("bar");
        Var head = new Var("baz");
        Fun wrap = new Fun(head, body);
        TransformationEvent tev = TransformationEvent.makeDelta(wrap, body, def);
        Fun out = (Fun)new Transformer().apply(tev);
        assertThat(out, is(not(wrap)));
        assertThat(out.getHead(), is(not(head)));
        assertThat(out.getBody(), is(not(def)));
        assertThat(out.getHead().getBaseName(), is(head.getBaseName()));
        assertThat(((Var)out.getBody()).getBaseName(), is(def.getBaseName()));
    }

}
