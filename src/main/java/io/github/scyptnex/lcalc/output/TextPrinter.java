package io.github.scyptnex.lcalc.output;

import io.github.scyptnex.lcalc.expression.*;

import java.io.PrintStream;

/**
 * TextPrinter, for printing lambda characters inr eadable text (i.e. to the console)
 */
public class TextPrinter extends LambdaPrinter implements Visitor<Void, String>{

    private final String lambda;

    private TextPrinter(PrintStream out, String lmb){
        super(out);
        this.lambda = lmb;
    }

    public static TextPrinter unicode(PrintStream out){
        return new TextPrinter(out, "\u03BB");
    }

    public static TextPrinter ascii(PrintStream out){
        return new TextPrinter(out, "\\");
    }

    @Override
    public void detailAlpha(Term input, Var rename, Term output) {
        out.println("- ALPHA -");
        out.println(makePrettyString(input) + " <--> " + makePrettyString(rename));
        out.println("---------");
    }

    @Override
    public void detailBeta(Term input, Fun relevantFunc, Term relevantVal, Term output) {
        out.println("- BETA  -");
        out.println(makePrettyString(input));
        out.println(makePrettyString(relevantFunc.getHead()) + " <- " + makePrettyString(relevantVal));
        out.println("---------");

    }

    @Override
    public void detailDelta(Term input, Var name, Term def, Term output) {
        out.println("- DELTA -");
        out.println(makePrettyString(input));
        out.println(makePrettyString(name) + " <- " + makePrettyString(def));
        out.println("---------");
    }

    @Override
    public void detailSigma(Term input, App expression, Var result, Term output) {
        out.println("- SIGMA -");
        out.println(makePrettyString(input));
        out.println(makePrettyString(expression) + " => " + makePrettyString(result));
        out.println("---------");
    }

    @Override
    public String makePrettyString(Term t) {
        return this.visit(null, t);
    }

    @Override
    public String visitApp(Void v, App t) {
        String l = visit(null, t.getLhs());
        if(t.getLhs() instanceof Fun) l = "(" + l + ")";
        String r = visit(null, t.getRhs());
        if(t.getRhs() instanceof App) r = "(" + r + ")";
        return l + " " + r;
    }

    @Override
    public String visitFun(Void v, Fun t) {
        String bod = visit(null, t.getBody());
        String hd = lambda + " " + visit(null, t.getHead());
        if(bod.startsWith(lambda)){
            return hd + bod.substring(lambda.length());
        } else {
            return hd + "." + bod;
        }
    }

    @Override
    public String visitVar(Void ld, Var t) {
        return t.getBaseName();
    }
}
