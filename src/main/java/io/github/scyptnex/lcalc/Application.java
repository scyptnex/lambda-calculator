package io.github.scyptnex.lcalc;

import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Util;
import io.github.scyptnex.lcalc.parser.ScriptParser;
import io.github.scyptnex.lcalc.transformer.TransformationEvent;
import io.github.scyptnex.lcalc.transformer.TransformationFinder;
import io.github.scyptnex.lcalc.transformer.Transformer;

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
    private Scanner inStream = null;

    public void acceptArguments(String[] args) throws IOException{
        for(String a : args){
            if(a.equals("-h")){
                usage(System.out);
                interpreting = false;
                return;
            }
            else if(a.equals("-qq")) verb = Verbosity.SILENT;
            else if(a.equals("-q")) verb = Verbosity.QUIET;
            else if(a.equals("-v")) verb = Verbosity.LOUD;
            else if(a.equals("-vv")) verb = Verbosity.DEAFENING;
            else if(Files.exists(Paths.get(a))){
                interpreting = false;
                getScriptParser().parse(Paths.get(a));
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
        inStream = new Scanner(in);
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
        Term original = t;
        List<TransformationEvent> transfs = new ArrayList<>();
        Optional<TransformationEvent> cur = TransformationFinder.find(t, state);
        int count = 0;
        while(cur.isPresent()){
            transfs.add(cur.get());
            t = new Transformer().apply(cur.get());
            count++;
            if(count >= MAXIMUM_ITERS){
                t = original;
                break;
                //System.out.println("Maximum iterations reached, keep going? [y/N]");
                //String response = inStream.nextLine().toLowerCase();
                //if(!response.contains("y")){
                //    break;
                //}
            }
            cur = TransformationFinder.find(t, state);
        }
        if(t != original){
            for (TransformationEvent tev : transfs){
                System.out.println("-- " + tev.type.name() + " --");
                System.out.println(Util.prettyPrint(tev.totalTerm));
                System.out.println(Util.prettyPrint(tev.relevantSubTerm) + " -> " + Util.prettyPrint(tev.transformation));
                System.out.println("--------");
            }
        }
        System.out.println(Util.prettyPrint(t));
        return t;
    }

    /**
     * Evaluate the lambda expression t, and store its result in the identifier id
     * @param t the lambda expression to evaluate
     * @param id the name of the variable to store this lambda in
     */
    public void evaluate(Term t, String id){
        state.put(id, evaluate(t));
    }

}
