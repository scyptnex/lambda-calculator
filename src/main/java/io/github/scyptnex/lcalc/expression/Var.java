package io.github.scyptnex.lcalc.expression;

public class Var implements Term {

    private final String baseName;

    public Var(String name){
        this.baseName = name;
    }

    public String getBaseName() {
        return baseName;
    }
}
