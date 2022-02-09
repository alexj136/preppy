package net.alexjeffery.preppy.test;

import net.alexjeffery.preppy.parser.PreppyLexer;
import net.alexjeffery.preppy.parser.PreppyParser;
import net.alexjeffery.preppy.syntax.Declaration;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamplesTest {

    @NotNull
    public static Map<String, List<Declaration>> getAstsFromDirectory(@NotNull String directoryName) throws IOException {
        Map<String, List<Declaration>> asts = new HashMap<>();
        File directory = new File(directoryName);
        for(File file : directory.listFiles()) {
            if (file.isDirectory())
                continue;
            asts.put(file.getName(), getAstFromFile(file.getPath()));
        }
        return asts;
    }

    @NotNull
    public static List<Declaration> getAstFromFile(@NotNull String fileName) throws IOException {
        PreppyLexer lexer = new PreppyLexer(new ANTLRFileStream(fileName));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PreppyParser parser = new PreppyParser(tokens);
        return parser.declarations().out;
    }

    @Test
    public void testExamplesParse() throws IOException {
        getAstsFromDirectory("examples/");
    }
}
