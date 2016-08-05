package io.github.scyptnex.lcalc.exception;

import java.util.Optional;
import java.util.function.Function;

public class Wrap {

    public static <T, R> Function<T, Optional<R>> wrap(ThrowingFunction<T, R> sub){
        return t -> {
            try{
                return Optional.of(sub.apply(t));
            } catch (LambdaException exc){
                return Optional.empty();
            }
        };
    }

    public interface ThrowingFunction<T, R>{
        R apply(T t) throws LambdaException;
    }

}
