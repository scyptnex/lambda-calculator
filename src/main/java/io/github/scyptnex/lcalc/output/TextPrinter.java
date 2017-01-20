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
        line("- ALPHA -");
        line(makePrettyString(input) + " <--> " + makePrettyString(rename));
        line("---------");
    }

    @Override
    public void detailBeta(Term input, Fun relevantFunc, Term relevantVal, Term output) {
        line("- BETA  -");
        line(makePrettyString(input));
        line(makePrettyString(relevantFunc.getHead()) + " <- " + makePrettyString(relevantVal));
        line("---------");

    }

    @Override
    public void detailDelta(Term input, Var name, Term def, Term output) {
        line("- DELTA -");
        line(makePrettyString(input));
        line(makePrettyString(name) + " <- " + makePrettyString(def));
        line("---------");
    }

    @Override
    public void detailSigma(Term input, App expression, Var result, Term output) {
        line("- SIGMA -");
        line(makePrettyString(input));
        line(makePrettyString(expression) + " => " + makePrettyString(result));
        line("---------");
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
