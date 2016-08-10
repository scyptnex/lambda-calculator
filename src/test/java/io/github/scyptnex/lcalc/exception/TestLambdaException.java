package io.github.scyptnex.lcalc.exception;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestLambdaException {

    @Test
    public void toStringSameAsGetMessage(){
        LambdaException exc = new LambdaException("Foo", LambdaException.class.getClass());
        assertThat(exc.toString(), is(exc.getMessage()));
    }

    @Test
    public void toStringContainsMessage(){
        for(String msg : Arrays.asList("FOO", "fOO", "supercalifragilisticexpealidocious")) {
            LambdaException exc = new LambdaException(msg, LambdaException.class.getClass());
            assertThat(exc.toString(), containsString(msg));
        }
    }
}
