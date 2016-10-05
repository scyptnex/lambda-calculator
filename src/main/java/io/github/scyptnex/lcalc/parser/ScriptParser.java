package io.github.scyptnex.lcalc.parser;

import io.github.scyptnex.lcalc.Application;
import io.github.scyptnex.lcalc.parser.gen.UntypedBaseVisitor;
import io.github.scyptnex.lcalc.parser.gen.UntypedLexer;
import io.github.scyptnex.lcalc.parser.gen.UntypedParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ScriptParser
 *
 * Read an entire script
 */
public class ScriptParser extends UntypedBaseVisitor<Void>{

    private final Application app;

    public ScriptParser(Application ap){
        this.app = ap;
    }

    private void parse(ANTLRInputStream ais){
        UntypedLexer lex = new UntypedLexer(ais);
        CommonTokenStream cts = new CommonTokenStream(lex);
        UntypedParser par = new UntypedParser(cts);
        this.visit(par.specification());
    }

    public void parse(Path p) throws IOException {
        parse(new ANTLRInputStream(Files.newInputStream(p)));
    }

    public void parse(String s) throws IOException {
        parse(new ANTLRInputStream(s));
    }

    @Override
    public Void visitEnd(UntypedParser.EndContext ctx) {
        return null;
    }

    @Override
    public Void visitInstruction(UntypedParser.InstructionContext ctx) {
        visit(ctx.command());
        return visit(ctx.specification());
    }

    @Override
    public Void visitDefinition(UntypedParser.DefinitionContext ctx) {
        app.evaluate(TermBuilder.build(ctx.expression()), ctx.ID().getText());
        return null;
    }

    @Override
    public Void visitOutput(UntypedParser.OutputContext ctx) {
        app.evaluate(TermBuilder.build(ctx.expression()));
        return null;
    }
}
