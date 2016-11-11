package io.github.scyptnex.lcalc.exception;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestWrap {

    @Test
    public void throwFunctionBecomesOption(){
        Wrap.ThrowingFunction<String, Integer> tf = Integer::parseInt;
        assertThat(Wrap.wrap(tf).apply("0"), not(Optional.empty()));
        assertThat(Wrap.wrap(tf).apply("12345"), not(Optional.empty()));
        assertThat(Wrap.wrap(tf).apply("not a number"), is(Optional.empty()));
        assertThat(Wrap.wrap(tf).apply("999999999999999999999"), is(Optional.empty()));
    }

    @Test
    public void throwBiFunctionBecomesOption(){
        Wrap.ThrowingBiFunction<String, String, Integer> tf = (s1, s2) -> Integer.parseInt(s1) + Integer.parseInt(s2);
        assertThat(Wrap.wrap(tf).apply("0", "5"), not(Optional.empty()));
        assertThat(Wrap.wrap(tf).apply("12345", "12345"), not(Optional.empty()));
        assertThat(Wrap.wrap(tf).apply("not a number", "10"), is(Optional.empty()));
        assertThat(Wrap.wrap(tf).apply("10", "not a number"), is(Optional.empty()));
    }

}
