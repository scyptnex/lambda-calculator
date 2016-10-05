package io.github.scyptnex.lcalc;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException{
        Application app = new Application();
        app.acceptArguments(args);
        if(app.interpreting) app.interpret(System.in);
    }

}
