package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.BaseTest;
import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Util;
import io.github.scyptnex.lcalc.expression.Var;
import io.github.scyptnex.lcalc.parser.TestUntypedExpression;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestTransformFinder extends BaseTest{

    @Test
    public void simpleExpressionsCannotBeTransformed() throws Exception {
        assertThat(findEvent("a"), is(Optional.empty()));
        assertThat(findEvent("\\x.x"), is(Optional.empty()));
        assertThat(findEvent("a b"), is(Optional.empty()));
    }

    @Test
    public void applyToDefinitionTriggersDelta() throws Exception {
        Optional<TransformationEvent> ev = findEvent("\\ b . a b", "a", "\\x.x");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(TransformationEvent.TransformType.DELTA));
    }

    @Test
    public void boundNamesAreNotDefinitions() throws Exception {
        Optional<TransformationEvent> ev = findEvent("\\ a . a b", "a", "\\x.x");
        assertThat(ev, is(Optional.empty()));
    }

    @Test
    public void applyTriggersBeta() throws Exception {
        Optional<TransformationEvent> ev = findEvent("(\\f.f) x");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(TransformationEvent.TransformType.BETA));
        // TODO make sure it actually betas the right thing
    }

    @Test
    public void applyWithNameConflictTriggersAlpha() throws Exception {
        Optional<TransformationEvent> ev = findEvent("(\\f a.f a) a");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(TransformationEvent.TransformType.ALPHA));
        assertThat(((Var)tev.relevantSubTerm).getBaseName(), is("a"));
    }

    @Test
    public void applyingToAFunctionThatContainsMeIsNotANameConflictWhenThatFunctionDoesntRedefineMyName() throws Exception {
        Optional<TransformationEvent> ev = findEvent("\\x.(\\y.(y x))x");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(not(TransformationEvent.TransformType.ALPHA)));
    }

    @Test
    public void alphaNeverRenamesDefs() throws Exception {
        Optional<TransformationEvent> ev = findEvent("(\\f a.f a I) I", "I", "\\x.x");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(not(TransformationEvent.TransformType.ALPHA)));
    }

    @Test
    public void alphaSimplifiesName() throws Exception {
        Optional<TransformationEvent> ev = findEvent("(\\f a'.f a') (\\a'.a')");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(TransformationEvent.TransformType.ALPHA));
        assertThat(((Var)tev.relevantSubTerm).getBaseName(), is("a'"));
        assertThat(((Var)tev.transformation).getBaseName(), is("a"));
    }

    @Test
    public void alphaFindsLowestNameAvailable() throws Exception {
        Optional<TransformationEvent> ev = findEvent("(\\f a'.f a a' a'2 a'3) (\\a'.a')");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(TransformationEvent.TransformType.ALPHA));
        assertThat(((Var)tev.relevantSubTerm).getBaseName(), is("a'"));
        assertThat(((Var)tev.transformation).getBaseName(), is("a'1"));
    }

}
