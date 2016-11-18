package io.github.scyptnex.lcalc.transformer;

import io.github.scyptnex.lcalc.expression.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Computer
 */
public class Computer {

    public final Term result;
    public final List<TransformationEvent> steps;

    private Computer(Term original, int max, Map<String, Term> definitions){
        Term result = original;
        List<TransformationEvent> steps = new ArrayList<>();
        Optional<TransformationEvent> tev = TransformationFinder.find(result, definitions);
        int count = 0;
        while(tev.isPresent()){
            steps.add(tev.get());
            result = new Transformer().apply(tev.get());
            count++;
            if(count >= max){
                result = null;
                steps = null;
                break;
            }
            tev = TransformationFinder.find(result, definitions);
        }
        this.result = result;
        this.steps = steps;
    }

    /**
     * Compute the stable state of the lambda term
     * @param original The input term
     * @param max The maximum number of iterations before bailing out
     * @param definitions The list of string - term pairs of defined expressions
     * @return A compute object containing the resulting term of the computation, and
     * the list of transformations which created that term, null if max is exceeded
     */
    public static Computer compute(Term original, int max, Map<String, Term> definitions){
        Computer ret = new Computer(original, max, definitions);
        if(ret.result == null) return null;
        return ret;
    }
}
