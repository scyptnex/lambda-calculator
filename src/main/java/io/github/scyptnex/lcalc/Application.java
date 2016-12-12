package io.github.scyptnex.lcalc;

import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Util;
import io.github.scyptnex.lcalc.output.LambdaPrinter;
import io.github.scyptnex.lcalc.output.TextPrinter;
import io.github.scyptnex.lcalc.parser.ScriptParser;
import io.github.scyptnex.lcalc.transformer.*;
import io.github.scyptnex.lcalc.util.Bi;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Application
 *
 * Holds the running lambda program, all its settings, and including a map for all of its definitions
 */
public class Application {

    public static final int MAXIMUM_ITERS = 500; // TODO make this a resources flag

    public enum Verbosity{
        SILENT,QUIET,NORMAL,LOUD,DEAFENING;
    }

    public boolean interpreting = true;
    public Verbosity verb = Verbosity.NORMAL;
    public Map<String, Term> state = new HashMap<>();
    public Simplifier simpl = new Simplifier(this.state);
    public Term lastResult = null;

    public void acceptArguments(String[] args) throws IOException{
        for(String a : args){
            if(a.equals("-h")){
                usage(System.out);
                interpreting = false;
                return;
            }
            else if(a.equals("-i")) interpreting = true;
            else if(a.equals("-qq")) verb = Verbosity.SILENT;
            else if(a.equals("-q")) verb = Verbosity.QUIET;
            else if(a.equals("-v")) verb = Verbosity.LOUD;
            else if(a.equals("-vv")) verb = Verbosity.DEAFENING;
            else if(Files.exists(Paths.get(a))){
                interpreting = false;
                getScriptParser().parse(Paths.get(a));
            } else {
                throw new IOException("Non-existant file or unrecognised option: " + a);
            }
        }
    }

    public static void usage(PrintStream out){
        out.println("Lambda Calculator");
        out.println();
        out.println("Usage: lambda-calculator [OPTIONS] [FILES]");
        out.println("    Executes the lambda calculus commands in each of the files in sequence.  If no files are given, will execute an interactive interpreter.");
        out.println();
        out.println("Options:");
        out.println("    -qq      Execute silently, output only when demanded");
        out.println("    -q       Execute quietly, showing all results");
        out.println("    -v       Execute loudly, showing all intermediate steps");
        out.println("    -vv      Execute verbosely, showing all intermediate steps in detail");
    }

    public void interpret(InputStream in) throws IOException{
        Scanner inStream = new Scanner(in);
        while(inStream.hasNextLine()){
            String line = inStream.nextLine();
            getScriptParser().parse(line);
        }
    }

    private ScriptParser getScriptParser(){
        return new ScriptParser(this);
    }

    /**
     * Evaluate the lambda expression t, depending on how the evaluation is configured, this may cause
     * log messages to be printed.
     * @param t The input lambda expression to evaluate
     * @return The simplest form of the input expression, which could be the fully-executed form (if execution
     * terminates) or the shortest form (if execution does not terminate)
     */
    public Term evaluate(Term t){
        Computer computation = Computer.compute(t, MAXIMUM_ITERS, state, simpl);
        LambdaPrinter lp = TextPrinter.unicode(System.out);
        if(computation != null){
            t = computation.result;
            if(verb == Verbosity.LOUD || verb == Verbosity.DEAFENING) for(int i=0; i<computation.steps.size(); i++){
                Term nxt = i+1 < computation.steps.size() ? computation.steps.get(i+1).totalTerm : t;
                lp.decode(computation.steps.get(i), nxt, verb == Verbosity.DEAFENING);
            }
        }
        lp.printLambda(t);
        lastResult = t;
        return t;
    }

    /**
     * Evaluate the lambda expression t, and store its result in the identifier id
     * @param t the lambda expression to evaluate
     * @param id the name of the variable to store this lambda in
     */
    public void define(Term t, String id){
        state.put(id, evaluate(t));
    }

}
