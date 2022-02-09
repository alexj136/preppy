package net.alexjeffery.preppy.main;

import net.alexjeffery.preppy.parser.PreppyLexer;
import net.alexjeffery.preppy.parser.PreppyParser;
import net.alexjeffery.preppy.syntax.Declaration;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String inputFileName = "examples/lambda.hcs";
        PreppyLexer lexer = new PreppyLexer(new ANTLRFileStream(inputFileName));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PreppyParser parser = new PreppyParser(tokens);
        List<Declaration> declarations = List.of(parser.declaration().out);
    }
}
