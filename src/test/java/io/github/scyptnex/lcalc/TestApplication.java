package io.github.scyptnex.lcalc;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestApplication {

    private Application withArgs(String... args) throws IOException {
        Application ret = new Application();
        ret.acceptArguments(args);
        return ret;
    }

    @Test
    public void stupidMandatoryCoverage() {
        Application.Verbosity.values();
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

}
