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
            new Decoder(next).visit(tev);
        } else {
            show(tev.name().toLowerCase(), next);
        }
    }

    /**
     * Used to dynamically decode the type of the transformation events
     */
    private class Decoder implements TransformationEvent.TransformationEventVisitor {
        public Term next;
        public Decoder(Term nxt){
            this.next = nxt;
        }
        @Override
        public void visitAlpha(TransformationEvent.Alpha tev) {
            detailAlpha(tev.totalTerm, tev.relevantSubTerm, next);
        }

        @Override
        public void visitBeta(TransformationEvent.Beta tev) {
            detailBeta(tev.totalTerm, tev.relevantSubTerm, tev.transformation, next);
        }

        @Override
        public void visitDelta(TransformationEvent.Delta tev) {
            detailDelta(tev.totalTerm, tev.relevantSubTerm, tev.transformation, next);
        }

        @Override
        public void visitSigma(TransformationEvent.Sigma tev) {
            detailSigma(tev.totalTerm, tev.relevantSubTerm, tev.transformation, next);
        }
    }
}
