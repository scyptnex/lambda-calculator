package io.github.scyptnex.lcalc;

import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.parser.TermBuilder;
import io.github.scyptnex.lcalc.parser.TestUntypedExpression;
import io.github.scyptnex.lcalc.parser.gen.UntypedLexer;
import io.github.scyptnex.lcalc.parser.gen.UntypedParser;
import io.github.scyptnex.lcalc.transformer.TransformationEvent;
import io.github.scyptnex.lcalc.transformer.TransformationFinder;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BaseTest {

    protected Term parse(String s) throws Exception {
        UntypedLexer lex = new UntypedLexer(new ANTLRInputStream(s));
        CommonTokenStream cts = new CommonTokenStream(lex);
        UntypedParser par = new UntypedParser(cts);

        // We want to throw an error where possible
        par.setErrorHandler(new BailErrorStrategy());
        return TermBuilder.build(par.expression());
    }

    protected Optional<TransformationEvent> findEvent(String expr, String... nameTerms) throws Exception {
        Map<String, Term> m = new HashMap<>();
        for (int i = 0; i < nameTerms.length; i += 2) {
            m.put(nameTerms[i], parse(nameTerms[i + 1]));
        }
        return TransformationFinder.find(parse(expr), m);
    }
}
