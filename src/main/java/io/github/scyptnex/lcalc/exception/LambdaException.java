package io.github.scyptnex.lcalc.exception;

public class LambdaException extends Exception{

    private final String message;
    private final Class<?> source;

    public LambdaException(String msg, Class<?> src){
        this.message = msg;
        this.source = src;
    }

    public void log(){
        //TODO this should actually print to my logger (when i implement it)
        System.err.println(String.format("[%s] - %s", source.getSimpleName(), message));
    }

}
