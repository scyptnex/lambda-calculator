package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.Fun;
import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Var;

/**
 * Determines which transformation will be used
 * In order to prevent infinite-recursion lambdas from polluting the screen, we accumulate these, and only print
 * them for the user if a non-recursive reduction exists
 */
public class TransformationEvent {

    public enum TransformType{
        ALPHA, BETA, ETA
    }

    public final Term totalTerm;
    public final Term relevantSubTerm;
    public final TransformType type;
    public final Term transformation;

    private TransformationEvent(Term total, Term relevant, TransformType ty, Term trans){
        this.totalTerm = total;
        this.relevantSubTerm = relevant;
        this.type = ty;
        this.transformation = trans;
    }

    public static TransformationEvent makeAlpha(Term total, Var from, Term to){
        return new TransformationEvent(total, from, TransformType.ALPHA, to);
    }

    public static TransformationEvent makeBeta(Term total, Fun abstraction, Term applied){
        return new TransformationEvent(total, abstraction, TransformType.BETA, applied);
    }

}
