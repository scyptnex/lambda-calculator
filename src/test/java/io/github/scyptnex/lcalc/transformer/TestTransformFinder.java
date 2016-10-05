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
    public void applyToDefinitionTriggersAlpha() throws Exception {
        Optional<TransformationEvent> ev = find("a b", "a", "\\x.x");
        assertThat(ev, is(not(Optional.empty())));
        assertThat(ev.map(te -> te.type).orElse(null), is(TransformationEvent.TransformType.ALPHA));
        // TODO make sure it actually alphas the right thing
    }

}
