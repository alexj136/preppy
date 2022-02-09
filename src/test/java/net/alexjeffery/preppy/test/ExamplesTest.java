package net.alexjeffery.preppy.test;

import net.alexjeffery.preppy.parser.PreppyLexer;
import net.alexjeffery.preppy.parser.PreppyParser;
import net.alexjeffery.preppy.syntax.Declaration;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class ExamplesTest {

    @NotNull
    public static List<Declaration> getAstFromFile(@NotNull String fileName) throws IOException {
        PreppyLexer lexer = new PreppyLexer(new ANTLRFileStream(fileName));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PreppyParser parser = new PreppyParser(tokens);
        List<Declaration> declarations = List.of(parser.declaration().out);
        return declarations;
    }

    @Test
    public void testFibonacci() throws IOException {
        List<Declaration> fibonacci = getAstFromFile("examples/fibonacci.ppp");
    }
}
