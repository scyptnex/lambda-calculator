package io.github.scyptnex.lcalc.output;

import io.github.scyptnex.lcalc.expression.App;
import io.github.scyptnex.lcalc.expression.Fun;
import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Var;
import io.github.scyptnex.lcalc.transformer.TransformationEvent;

import java.io.PrintStream;

/**
 * LambdaPrinter, used to print lambda terms to the user (under varying circumstances)
 */
public abstract class LambdaPrinter {

    protected final PrintStream out;

    public LambdaPrinter(PrintStream out){
        this.out = out;
    }

    public abstract void detailAlpha(Term input, Var rename, Term output);

    public abstract void detailBeta(Term input, Fun relevantFunc, Term relevantVal, Term output);

    public abstract void detailDelta(Term input, Var name, Term def, Term output);

    public abstract void detailSigma(Term input, App expression, Var result, Term output);

    public abstract String makePrettyString(Term t);

    public void show(String name, Term result) {
        out.println(name + " - " + makePrettyString(result));
    }

    public void printLambda(Term l){
        out.println(makePrettyString(l));
    }

    public void decode(TransformationEvent tev, Term next, boolean detail){
        if(detail){
            switch(tev.type){
                case ALPHA: {detailAlpha(tev.totalTerm, (Var)tev.relevantSubTerm, next); break;}
                case BETA:  {detailBeta(tev.totalTerm, (Fun)tev.relevantSubTerm, tev.transformation, next); break;}
                case DELTA: {detailDelta(tev.totalTerm, (Var)tev.relevantSubTerm, tev.transformation, next); break;}
                default:    {detailSigma(tev.totalTerm, (App)tev.relevantSubTerm, (Var)tev.transformation, next); break;}
            }
        } else {
            show(tev.type.name().toLowerCase(), next);
        }
    }

}
