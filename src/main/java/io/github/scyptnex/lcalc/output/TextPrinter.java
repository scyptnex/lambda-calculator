package io.github.scyptnex.lcalc.output;

import io.github.scyptnex.lcalc.expression.Fun;
import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Util;
import io.github.scyptnex.lcalc.expression.Var;

import java.io.PrintStream;

/**
 * TextPrinter, for printing lambda characters inr eadable text (i.e. to the console)
 */
public class TextPrinter extends LambdaPrinter {

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
    public String makePrettyString(Term t) {
        return Util.prettyPrint(lambda, t);
    }
}
