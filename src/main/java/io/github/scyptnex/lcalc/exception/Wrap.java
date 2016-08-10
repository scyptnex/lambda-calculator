package io.github.scyptnex.lcalc.exception;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Wrap {

    static <T, R> Function<T, Optional<R>> wrap(ThrowingFunction<T, R> sub){
        return t -> {
            try{
                return Optional.of(sub.apply(t));
            } catch (Exception exc){
                return Optional.empty();
            }
        };
    }

    static <A, B, R> BiFunction<A, B, Optional<R>> wrap(ThrowingBiFunction<A, B, R> sub){
        return (a, b) -> {
            try{
                return Optional.of(sub.apply(a, b));
            } catch (Exception exc){
                return Optional.empty();
            }
        };
    }

    interface ThrowingFunction<T, R>{
        R apply(T t) throws Exception;
    }

    interface ThrowingBiFunction<A, B, R>{
        R apply(A a, B b) throws Exception;
    }

}
