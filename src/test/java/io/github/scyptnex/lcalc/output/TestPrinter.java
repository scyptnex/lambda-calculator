package io.github.scyptnex.lcalc.output;

import io.github.scyptnex.lcalc.expression.App;
import io.github.scyptnex.lcalc.expression.Fun;
import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Var;
import io.github.scyptnex.lcalc.transformer.TransformationEvent;
import io.github.scyptnex.lcalc.util.Bi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TestPrinter {

    @Parameterized.Parameters
    public static Collection<Object[]> versions(){
        return Arrays.asList(
                new Object[]{0},
                new Object[]{1}
        );
    }

    @Parameterized.Parameter
    public Integer style;

    private Bi<ByteArrayOutputStream, LambdaPrinter> makePrinter(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        switch(style){
            case 0  : return new Bi<>(baos, TextPrinter.unicode(new PrintStream(baos)));
            default : return new Bi<>(baos, TextPrinter.ascii(new PrintStream(baos)));
        }
    }

    private String getPrinted(Bi<ByteArrayOutputStream, LambdaPrinter> b){
        return new String(b.first.toByteArray(), StandardCharsets.UTF_8);
    }

    @Test
    public void prettyPrintShowsItsVar(){
        Bi<ByteArrayOutputStream, LambdaPrinter> cur = makePrinter();
        String pretty = cur.second.makePrettyString(new Var("foobar"));
        assertThat(pretty, containsString("foobar"));
    }

    @Test
    public void prettyPrintShowsItsFun(){
        Bi<ByteArrayOutputStream, LambdaPrinter> cur = makePrinter();
        String pretty = cur.second.makePrettyString(new Fun(new Var("foo"), new Var("bar")));
        assertThat(pretty, containsString("foo"));
        assertThat(pretty, containsString("bar"));
    }

    @Test
    public void prettyPrintShowsItsApp(){
        Bi<ByteArrayOutputStream, LambdaPrinter> cur = makePrinter();
        String pretty = cur.second.makePrettyString(new App(new Var("foo"), new Var("bar")));
        assertThat(pretty, containsString("foo"));
        assertThat(pretty, containsString("bar"));
    }

    @Test
    public void prettyPrintDoesNotOutput(){
        Bi<ByteArrayOutputStream, LambdaPrinter> cur = makePrinter();
        cur.second.makePrettyString(new Var("x"));
        String printed = getPrinted(cur);
        assertThat(printed, is(""));
    }

    private void compareShowToDescribe(TransformationEvent tev, Term result){
        Bi<ByteArrayOutputStream, LambdaPrinter> show = makePrinter();
        show.second.decode(tev, result, false);
        String showText = getPrinted(show);

        Bi<ByteArrayOutputStream, LambdaPrinter> desc = makePrinter();
        desc.second.decode(tev, result, true);
        String descText = getPrinted(desc);

        assertThat(showText, is(not("")));
        assertThat(descText, is(not("")));
        assertTrue(descText.length() > showText.length());
    }

    @Test
    public void showIsShorterThanDescribeAlpha(){
        Var rnm = new Var("x");
        TransformationEvent tev = new TransformationEvent.Alpha(rnm, rnm, new Var("z"));
        Term result = new Var("z");
        compareShowToDescribe(tev, result);
    }

    @Test
    public void showIsShorterThanDescribeBeta(){
        Var v = new Var("v");
        Fun lhs = new Fun(v, v);
        Var rhs = new Var("x");
        TransformationEvent tev = new TransformationEvent.Beta(new App(lhs, rhs), lhs, rhs);
        Term result = new Var("x");
        compareShowToDescribe(tev, result);
    }

    @Test
    public void showIsShorterThanDescribeDelta(){
        Var name = new Var("name");
        TransformationEvent tev = new TransformationEvent.Delta(new App(name, new Var("z")), name, new Var("definition"));
        Term result = new Var("definition");
        compareShowToDescribe(tev, result);
    }

}
