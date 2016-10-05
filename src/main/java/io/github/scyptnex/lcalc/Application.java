package io.github.scyptnex.lcalc;

import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.parser.ScriptParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Application
 *
 * Holds the running lambda program, all its settings, and including a map for all of its definitions
 */
public class Application {

    public enum Verbosity{
        SILENT,QUIET,NORMAL,LOUD,DEAFENING;
    }

    public boolean interpreting = true;
    public Verbosity verb = Verbosity.NORMAL;
    public Map<String, Term> state = new HashMap<>();

    public void acceptArguments(String[] args) throws IOException{
        for(String a : args){
            if(a.equals("-qq")) verb = Verbosity.SILENT;
            else if(a.equals("-q")) verb = Verbosity.QUIET;
            else if(a.equals("-v")) verb = Verbosity.LOUD;
            else if(a.equals("-vv")) verb = Verbosity.DEAFENING;
            else if(Files.exists(Paths.get(a))){
                interpreting = false;
                getScriptParser().parse(Paths.get(a));
            }
        }
    }

    public void interpret(InputStream in) throws IOException{
        Scanner sca = new Scanner(in);
        while(sca.hasNextLine()){
            String line = sca.nextLine();
            System.err.println(line);
            getScriptParser().parse(line);
        }
    }

    private ScriptParser getScriptParser(){
        return new ScriptParser(this);
    }

}
