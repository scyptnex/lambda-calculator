package io.github.scyptnex.lcalc.exception;

public class LambdaException extends Exception{

    private final String message;
    private final Class<?> source;

    public LambdaException(String msg, Class<?> src){
        this.message = msg;
        this.source = src;
    }

    @Override
    public String getMessage() {
        return String.format("[%s] - %s", source.getSimpleName(), message);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
