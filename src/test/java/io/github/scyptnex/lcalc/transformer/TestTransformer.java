package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.App;
import io.github.scyptnex.lcalc.expression.Fun;
import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Var;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestTransformer {

    @Test
    public void debugRenamerWorks(){
        Var x = new Var("X");
        Var y = new Var("Y");
        Fun f = (Fun)new Transformer.DebugRenamer().visit(new HashMap<>(), new Fun(x, new App(y, x)));
        assertThat(f.getHead().getBaseName(), is("v0"));
        App a = (App) f.getBody();
        assertThat(a.getRhs(), is(f.getHead()));
        assertThat(((Var)a.getLhs()).getBaseName(), is("v1"));
    }

    @Test
    public void debugRenamerDoesNotRenameDefinitions(){
        Var name = new Var("NAME");
        Var repl = new Var("REPLACE");
        Var t = (Var)new Transformer.DebugRenamer().visit(Collections.singletonMap("NAME", repl), name);
        assertThat(t.getBaseName(), is("NAME"));
    }

    @Test
    public void alphaDoesNotDuplicate(){
        Var from = new Var("foo");
        Var to = new Var("bar");
        Fun wrap = new Fun(from, from);
        TransformationEvent tev = new TransformationEvent.Alpha(wrap, from, to);
        Fun out = (Fun)new Transformer().apply(tev);
        assertThat(out.getHead(), is(to));
        assertThat(out.getBody(), is(to));
    }

    @Test
    public void alphaDuplicatesUnrelatedTerms(){
        Var from = new Var("foo");
        Var to = new Var("bar");
        Fun wrap = new Fun(from, from);
        TransformationEvent tev = new TransformationEvent.Alpha(wrap, from, to);
        Term out = new Transformer().apply(tev);
        assertThat(out, is(not(wrap)));
    }

    @Test
    public void betaDoesWhatItSaysOnTheTin(){
        Var v1 = new Var("x");
        Fun ident = new Fun(v1, v1);
        Var v2 = new Var("y");
        App total = new App(ident, v2);
        TransformationEvent tev = new TransformationEvent.Beta(total, ident, v2);
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
        TransformationEvent tev = new TransformationEvent.Beta(t, (Fun)t.getLhs(), t.getRhs());
        Fun f = (Fun)new Transformer().apply(tev);
        assertThat(f, is(not(pseudoResult)));
        assertThat(f.getHead(), is(not(important)));
        assertThat(f.getHead().getBaseName(), is(important.getBaseName()));
        assertThat(((App)f.getBody()).getRhs(), is(f.getHead()));
    }

    @Test
    public void betaPreservesAcrossBoundaries(){
        Var u = new Var("unchanged");
        Var s = new Var("substituted");
        Var m1 = new Var("misc1");
        Var m2 = new Var("misc2");
        Fun used = new Fun(s, new App(s, new App(u, m1)));
        App needed = new App(m2, u);
        App beta = new App(used, needed);
        Fun outer = new Fun(u, beta);
        // \\u.(\\s.s (u m1)) (m2, u)
        TransformationEvent tev = new TransformationEvent.Beta(outer, used, needed);
        Fun t = (Fun)new Transformer().apply(tev);
        Var newU = t.getHead();
        assertThat(t.getHead(), is(newU));
        App lhs = (App)(((App)t.getBody()).getLhs());
        App rhs = (App)(((App)t.getBody()).getRhs());
        assertThat("the substitution itself should preserve: " + newU.getBaseName() + "=" + ((Var)lhs.getRhs()).getBaseName(), lhs.getRhs(), is(newU));
        assertThat("the thing that was not substituted should preserve: " + newU.getBaseName() + "=" + ((Var)rhs.getLhs()).getBaseName(), rhs.getLhs(), is(newU));
    }

    @Test
    public void deltaDuplicatesEntireTerm(){
        Var body = new Var("foo");
        Var def = new Var("bar");
        Var head = new Var("baz");
        Fun wrap = new Fun(head, body);
        TransformationEvent tev = new TransformationEvent.Delta(wrap, body, def);
        Fun out = (Fun)new Transformer().apply(tev);
        assertThat(out, is(not(wrap)));
        assertThat(out.getHead(), is(not(head)));
        assertThat(out.getBody(), is(not(def)));
        assertThat(out.getHead().getBaseName(), is(head.getBaseName()));
        assertThat(((Var)out.getBody()).getBaseName(), is(def.getBaseName()));
    }

    // TODO this is actually a temporary, ideally delta would not be allowed to replace more than once
    @Test
    public void deltaReplacesWithDifferentDuplicates() {
        Var src = new Var("replaceMe");
        Var dst = new Var("withMe");
        TransformationEvent tev = new TransformationEvent.Delta(new App(src, src), src, dst);
        App out = (App) new Transformer().apply(tev);
        assertThat(out.getLhs(), is(not(dst)));
        assertThat(out.getRhs(), is(not(dst)));
        assertThat(((Var)out.getLhs()).getBaseName(), is(dst.getBaseName()));
        assertThat(((Var)out.getRhs()).getBaseName(), is(dst.getBaseName()));
        assertThat("Each replacement must be different from the others", out.getLhs(), is(not(out.getRhs())));
    }

    @Test
    public void sigmaDuplicatesItsReplacement() {
        Var x = new Var("x");
        Var y = new Var("y");
        Var z = new Var("z");
        App a = new App(x, y);
        Var out = (Var) new Transformer().apply(new TransformationEvent.Sigma(a, a, z));
        assertThat(out, is(not(z)));
        assertThat(out.getBaseName(), is(z.getBaseName()));
    }

}
