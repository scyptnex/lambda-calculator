package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Var;
import io.github.scyptnex.lcalc.parser.TestUntypedExpression;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestTransformFinder {

    private Optional<TransformationEvent> find(String expr, String...nameTerms) throws Exception {
        Map<String, Term> m = new HashMap<>();
        for(int i=0; i<nameTerms.length; i+=2){
            m.put(nameTerms[i], TestUntypedExpression.parse(nameTerms[i+1]));
        }
        return TransformationFinder.find(TestUntypedExpression.parse(expr), m);
    }

    @Test
    public void simpleExpressionsCannotBeTransformed() throws Exception {
        assertThat(find("a"), is(Optional.empty()));
        assertThat(find("\\x.x"), is(Optional.empty()));
        assertThat(find("a b"), is(Optional.empty()));
    }

    @Test
    public void applyToDefinitionTriggersDelta() throws Exception {
        Optional<TransformationEvent> ev = find("\\ b . a b", "a", "\\x.x");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(TransformationEvent.TransformType.DELTA));
    }

    @Test
    public void boundNamesAreNotDefinitions() throws Exception {
        Optional<TransformationEvent> ev = find("\\ a . a b", "a", "\\x.x");
        assertThat(ev, is(Optional.empty()));
    }

    @Test
    public void applyTriggersBeta() throws Exception {
        Optional<TransformationEvent> ev = find("(\\f.f) x");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(TransformationEvent.TransformType.BETA));
        // TODO make sure it actually betas the right thing
    }

    @Test
    public void applyWithNameConflictTriggersAlpha() throws Exception {
        Optional<TransformationEvent> ev = find("(\\f a.f a) (\\a.a)");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(TransformationEvent.TransformType.ALPHA));
        assertThat(((Var)tev.relevantSubTerm).getBaseName(), is("a"));
    }

    @Test
    public void alphaSimplifiesName() throws Exception {
        Optional<TransformationEvent> ev = find("(\\f a'.f a') (\\a'.a')");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(TransformationEvent.TransformType.ALPHA));
        assertThat(((Var)tev.relevantSubTerm).getBaseName(), is("a'"));
        assertThat(((Var)tev.transformation).getBaseName(), is("a"));
    }

    @Test
    public void alphaFindsLowestNameAvailable() throws Exception {
        Optional<TransformationEvent> ev = find("(\\f a'.f a a' a'2 a'3) (\\a'.a')");
        assertThat(ev, is(not(Optional.empty())));
        TransformationEvent tev = ev.get();
        assertThat(tev.type, is(TransformationEvent.TransformType.ALPHA));
        assertThat(((Var)tev.relevantSubTerm).getBaseName(), is("a'"));
        assertThat(((Var)tev.transformation).getBaseName(), is("a'1"));
    }

}
