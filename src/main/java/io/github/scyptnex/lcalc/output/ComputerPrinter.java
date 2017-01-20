package io.github.scyptnex.lcalc.output;

import io.github.scyptnex.lcalc.expression.Term;
import io.github.scyptnex.lcalc.transformer.Computer;
import io.github.scyptnex.lcalc.transformer.TransformationEvent;

import java.util.List;

public class ComputerPrinter {

    public final LambdaPrinter printer;

    public ComputerPrinter(LambdaPrinter lp){
        this.printer = lp;
    }

    public void print(Term finalTerm, List<TransformationEvent> evs, boolean detail, boolean recursive){
        for(int i=0; i<evs.size(); i++){
            TransformationEvent tev = evs.get(i);
            if(recursive && tev instanceof TransformationEvent.Sigma && ((TransformationEvent.Sigma) tev).proof.isPresent()){
                printer.indent();
                print(((TransformationEvent.Sigma) tev).proof.get(), detail, true);
                printer.unindent();
            }
            Term nxt = i+1 < evs.size() ? evs.get(i+1).totalTerm : finalTerm;
            printer.decode(tev, nxt, detail);
        }
    }

    public void print(Computer cmp, boolean detail, boolean recursive){
        print(cmp.result, cmp.steps, detail, recursive);
    }

}
