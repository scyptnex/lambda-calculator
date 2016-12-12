package io.github.scyptnex.lcalc;

import io.github.scyptnex.lcalc.expression.App;
import io.github.scyptnex.lcalc.expression.Fun;
import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Var;
import io.github.scyptnex.lcalc.parser.TestUntypedExpression;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TestApplication extends BaseTest{

    private Application withArgs(String... args) throws IOException {
        Application ret = new Application();
        ret.acceptArguments(args);
        return ret;
    }

    @Test
    public void stupidMandatoryCoverage() {
        assertThat(Application.Verbosity.values().length, is(5));
        assertThat(Application.Verbosity.valueOf("SILENT"   ), is(Application.Verbosity.SILENT));
        assertThat(Application.Verbosity.valueOf("QUIET"    ), is(Application.Verbosity.QUIET));
        assertThat(Application.Verbosity.valueOf("NORMAL"   ), is(Application.Verbosity.NORMAL));
        assertThat(Application.Verbosity.valueOf("LOUD"     ), is(Application.Verbosity.LOUD));
        assertThat(Application.Verbosity.valueOf("DEAFENING"), is(Application.Verbosity.DEAFENING));
    }

    @Test
    public void verbositySetsCorrectly() throws IOException {
        assertThat(withArgs("-qq").verb, is(Application.Verbosity.SILENT));
        assertThat(withArgs("-q").verb , is(Application.Verbosity.QUIET));
        assertThat(withArgs().verb     , is(Application.Verbosity.NORMAL));
        assertThat(withArgs("-v").verb , is(Application.Verbosity.LOUD));
        assertThat(withArgs("-vv").verb, is(Application.Verbosity.DEAFENING));
    }

    @Test
    public void multipleVerbosityGoesWithLatest() throws IOException {
        assertThat(withArgs("-vv", "-qq").verb, is(Application.Verbosity.SILENT));
    }

    @Test
    public void noFilesCausesInterpretMode() throws IOException {
        assertThat(withArgs().interpreting, is(true));
        assertThat(withArgs("-v").interpreting, is(true));
        assertThat(withArgs("/dev/null").interpreting, is(false));
    }

    @Test
    public void helpDisablesInterpretation() throws IOException {
        assertThat(withArgs("-h").interpreting, is(false));
    }

    @Test
    public void helpPreventsFilesFromReading() throws IOException {
        assertThat(withArgs("-h", "nonexistantfile").interpreting, is(false));
    }

    @Test
    public void interpretNothingIsValid() throws IOException {
        withArgs().interpret(new ByteArrayInputStream(new byte[]{}));
    }

    @Test
    public void interpretDefIsValid() throws IOException {
        withArgs().interpret(new ByteArrayInputStream("# foo \\x.x".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void interpretOutputIsValid() throws IOException {
        withArgs().interpret(new ByteArrayInputStream("? \\x.x".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void evaluateReturnsSimplest() throws IOException {
        Var id = new Var("x");
        Var v = (Var)withArgs().evaluate(new App(new Fun(id, id), new Var("y")));
        assertThat(v.getBaseName(), is("y"));
    }

    @Test
    public void evaluateInfiniteReturnsOriginal() throws Exception {
        Term ycomb = parse("\\f.(\\x.f (x x)) (\\x.f (x x))");
        Term t = withArgs().evaluate(ycomb);
        assertThat(t, is(ycomb));
    }

    @Test
    public void loadNonExistantFileIsError() {
        try{
            withArgs("missingFile.txt");
            fail("Failed to throw an exception for reading a non-existant file.");
        } catch (Exception exc){
            //test passed
        }
    }

    /*********************************
     *      Whole program cases      *
     *********************************/

    private Application queryAfterLoading(String query, String... libs) throws IOException {
        String[] args = new String[3 + libs.length];
        args[0] = "-qq";
        System.arraycopy(libs, 0, args, 1, libs.length);
        args[args.length-2] = "-vv";
        args[args.length-1] = "-i";
        Application app = new Application();
        app.acceptArguments(args);
        app.interpret(new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8)));
        return app;
    }

    @Test
    public void powTwoThree() throws Exception {
        Application a = queryAfterLoading("? POW TWO THREE", "src/dist/stdlib.lc");
        Term eight = this.parse("\\s z.s(s(s(s(s(s(s(s z)))))))");
        assertThat(a.simpl.isAlphaEquivalent(a.lastResult, eight), is(true));
    }

    @Test
    public void threeFactorial() throws Exception {
        Application a = queryAfterLoading("? YCOMBI (\\f n . IF (ISZERO n) ONE (MULT (f (PRED n)) n)) THREE", "src/dist/stdlib.lc");
        Term eight = this.parse("\\s z.s(s(s(s(s(s z)))))");
        assertThat(a.simpl.isAlphaEquivalent(a.lastResult, eight), is(true));
    }

}
