package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.Application;
import io.github.scyptnex.lcalc.BaseTest;
import io.github.scyptnex.lcalc.expression.App;
import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Var;
import io.github.scyptnex.lcalc.util.Bi;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestSimplifier extends BaseTest{

    private Simplifier mockApp(String ... defs) throws Exception {
        Application app = new Application();
        for(int i=0; i<defs.length; i+=2){
            app.define(parse(defs[i+1]), defs[i]);
        }
        return app.simpl;
    }

    @Test
    public void varsAreAlphaEquivalent() throws Exception {
        Simplifier s = mockApp();
        Term a = parse("x");
        Term b = parse("x");
        Term c = parse("y");
        assertThat(s.isAlphaEquivalent(a, b), is(true));
        assertThat(s.isAlphaEquivalent(a, c), is(true));
    }

    @Test
    public void appsAreEquivalent() throws Exception {
        Simplifier s = mockApp();
        Term a = parse("x y");
        Term b = parse("z a");
        assertThat(s.isAlphaEquivalent(a, b), is(true));
        Term c = parse("(a b) c");
        Term d = parse("(x x) x");
        assertThat(s.isAlphaEquivalent(c, d), is(true));
    }

    @Test
    public void appsDoNotAssociate() throws Exception {
        Simplifier s = mockApp();
        Term c = parse("(a b) c");
        Term e = parse("a (b c)");
        assertThat(s.isAlphaEquivalent(c, e), is(false));
    }

    @Test
    public void funcsAreAlphaEquivalent() throws Exception {
        Simplifier s = mockApp();
        Term a = parse("\\a.b a");
        Term b = parse("\\x.y x");
        assertThat(s.isAlphaEquivalent(a, b), is(true));
    }

    @Test
    public void identityIsNotSubstitute() throws Exception {
        Simplifier s = mockApp();
        Term a = parse("\\a.a");
        Term b = parse("\\x.y");
        assertThat(s.isAlphaEquivalent(a, b), is(false));
    }

    @Test
    public void bindingsMatterToTheApp() throws Exception {
        Simplifier s = mockApp();
        Term a = parse("\\a.a b");
        Term b = parse("\\b.a b");
        assertThat(s.isAlphaEquivalent(a, b), is(false));
    }

    @Test
    public void boundNamesAreNotEquivalentWithDefinitions() throws Exception {
        Term a = parse("\\x.FOO x");
        Term b = parse("\\FOO.x FOO");
        Simplifier s1 = mockApp();
        assertThat(s1.isAlphaEquivalent(a, b), is(true));
        Simplifier s2 = mockApp("FOO", "\\x.x");
        assertThat(s2.isAlphaEquivalent(a, b), is(false));
    }

    @Test
    public void standalonesAreNotCandidates() throws Exception {
        Simplifier s = mockApp("I", "\\x.x");
        assertThat(s.findCandidate(parse("I")), is(Optional.empty()));
        assertThat(s.findCandidate(parse("f")), is(Optional.empty()));
    }

    @Test
    public void candidateExistsWithinFunction() throws Exception {
        Simplifier s = mockApp("I", "\\x.x");
        assertThat(s.findCandidate(parse("\\f.I I f")), is(not(Optional.empty())));
        assertThat(s.findCandidate(parse("\\f.f (I I)")), is(not(Optional.empty())));
    }

    @Test
    public void leftmostAppCanNotBeCandidate() throws Exception {
        Simplifier s = mockApp("I", "\\x.x");
        assertThat(s.findCandidate(parse("I I f")), is(Optional.empty()));
    }

    @Test
    public void onlyAppConstantsCanBeCandidate() throws Exception {
        Simplifier s = mockApp("I", "\\x.x");
        assertThat(s.findCandidate(parse("f I I")), is(Optional.empty()));
    }

    @Test
    public void simplifyMakesTransformEvent() throws Exception {
        Simplifier s = mockApp("ZERO", "\\s z.z", "ONE", "\\s z.s(z)", "SUCC", "\\n s z.s(n s z)");
        Optional<TransformationEvent.Sigma> res = s.findCandidate(parse("f (SUCC ZERO)"));
        assertThat(res, is(not(Optional.empty())));
        assertThat(res.get(), is(instanceOf(TransformationEvent.Sigma.class)));
        assertThat((res.get()).transformation.getBaseName(), is("ONE"));
    }

    @Test
    public void newSimplificationsReturnAComputation() throws Exception {
        Simplifier s = mockApp("ZERO", "\\s z.z", "ONE", "\\s z.s(z)", "SUCC", "\\n s z.s(n s z)");
        Optional<TransformationEvent.Sigma> res = s.findCandidate(parse("f (SUCC ZERO)"));
        assertThat(res.get().proof, is(not(Optional.empty())));
    }

    @Test
    public void oldSimplificationsOmitTheComputation() throws Exception {
        Simplifier s = mockApp("ZERO", "\\s z.z", "ONE", "\\s z.s(z)", "SUCC", "\\n s z.s(n s z)");
        s.findCandidate(parse("f1 (SUCC ZERO)"));
        Optional<TransformationEvent.Sigma> res2 = s.findCandidate(parse("f2 (SUCC ZERO)"));
        assertThat(res2.get().proof, is(Optional.empty()));
    }
}
