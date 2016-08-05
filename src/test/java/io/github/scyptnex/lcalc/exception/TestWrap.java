package io.github.scyptnex.lcalc.exception;

import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestWrap {

    @Test
    public void throwBecomesOption(){
        List<Optional<String>> slist = Stream.of("a", "b", "c").map(Wrap.wrap(s -> {
            if (s.equals("b")) throw new LambdaException("b", TestWrap.class.getClass());
            else return s;
        })).collect(Collectors.toList());
        assertThat(slist.get(0), not(Optional.empty()));
        assertThat(slist.get(1), is(Optional.empty()));
        assertThat(slist.get(2), not(Optional.empty()));
    }

}
