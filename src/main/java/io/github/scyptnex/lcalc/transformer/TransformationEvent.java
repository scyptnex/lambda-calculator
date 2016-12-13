package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.App;
import io.github.scyptnex.lcalc.expression.Fun;
import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.expression.Var;

import javax.lang.model.element.Name;

/**
 * Determines which transformation will be used
 * In order to prevent infinite-recursion lambdas from polluting the screen, we accumulate these, and only print
 * them for the user if a non-recursive reduction exists
 */
public abstract class TransformationEvent {

    public final Term totalTerm;

    private TransformationEvent(Term total){
        this.totalTerm = total;
    }

    public abstract void visitMe(TransformationEventVisitor tev);

    public String name(){
        return this.getClass().getSimpleName();
    }

    /**
     * For preventing name conflicts
     */
    public static class Alpha extends TransformationEvent {
        public final Var relevantSubTerm;
        public final Var transformation;
        public Alpha(Term total, Var from, Var to){
            super(total);
            relevantSubTerm = from;
            transformation = to;
        }

        @Override
        public void visitMe(TransformationEventVisitor tev) {
            tev.visitAlpha(this);
        }
    }

    /**
     * For calling abstractions
     */
    public static class Beta extends TransformationEvent {
        public final Fun relevantSubTerm;
        public final Term transformation;
        public Beta(Term total, Fun application, Term applied){
            super(total);
            relevantSubTerm = application;
            transformation = applied;
        }

        @Override
        public void visitMe(TransformationEventVisitor tev) {
            tev.visitBeta(this);
        }
    }

    /**
     * For defined name substitutions
     */
    public static class Delta extends TransformationEvent {
        public final Var relevantSubTerm;
        public final Term transformation;
        public Delta(Term total, Var name, Term def){
            super(total);
            relevantSubTerm = name;
            transformation = def;
        }

        @Override
        public void visitMe(TransformationEventVisitor tev) {
            tev.visitDelta(this);
        }
    }

    /**
     * For eagerly evaluating constant terms into existing definitions
     */
    public static class Sigma extends TransformationEvent {
        public final App relevantSubTerm;
        public final Var transformation;
        public Sigma(Term total, App from, Var to){
            super(total);
            relevantSubTerm = from;
            transformation = to;
        }

        @Override
        public void visitMe(TransformationEventVisitor tev) {
            tev.visitSigma(this);
        }
    }

    public interface TransformationEventVisitor {
        void visitAlpha(Alpha tev);
        void visitBeta(Beta tev);
        void visitDelta(Delta tev);
        void visitSigma(Sigma tev);
        default void visit(TransformationEvent tev){
            tev.visitMe(this);
        }
    }

}
