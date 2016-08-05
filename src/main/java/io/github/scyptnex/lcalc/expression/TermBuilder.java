package io.github.scyptnex.lcalc.expression;

import io.github.scyptnex.lcalc.exception.LambdaException;

import java.util.ArrayList;

public class TermBuilder {

    private ArrayList<Term> currentList = new ArrayList<>();

    public TermBuilder pushVar(String varName){
        return this;
    }

    public TermBuilder popBodyHeadPushFun() throws LambdaException {
        return this;
    }

    public TermBuilder popHeadBodyPushFun() throws LambdaException {
        return this;
    }

    public TermBuilder popRhsLhsPushApp(){
        return this;
    }

    public TermBuilder popLhsRhsPushApp(){
        return this;
    }

    public TermBuilder toFunChain() throws LambdaException {
        return this;
    }

    public TermBuilder toAppChain(){
        return this;
    }

    private static void error(String msg) throws LambdaException {
        throw new LambdaException(msg, TermBuilder.class.getClass());
    }

}
