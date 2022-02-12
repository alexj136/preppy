package net.alexjeffery.preppy.test;

import net.alexjeffery.preppy.interpret.Interpreter;
import net.alexjeffery.preppy.output.StackMachineCodeGenerator;
import net.alexjeffery.preppy.parser.PreppyLexer;
import net.alexjeffery.preppy.parser.PreppyParser;
import net.alexjeffery.preppy.syntax.Declaration;
import net.alexjeffery.preppy.syntax.Expression;
import net.alexjeffery.preppy.syntax.Statement;
import net.alexjeffery.preppy.vm.MachineInstruction;
import net.alexjeffery.preppy.vm.StackMachine;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.alexjeffery.preppy.interpret.Interpreter.InterpreterException;
import static net.alexjeffery.preppy.syntax.Syntax.SyntaxException;
import static net.alexjeffery.preppy.output.StackMachineCodeGenerator.CodeGenException;

public class ExamplesTest {

    public static final List<Declaration> FIBONACCI = List.of(
            new Declaration("main", List.of("arg"), new Statement.Cond(
                    new Expression.BinOp(
                            new Expression.Variable("arg"),
                            new Expression.IntLiteral(1),
                            Expression.BinOp.Type.LESS_EQ),
                    new Statement.Return(new Expression.IntLiteral(1)),
                    new Statement.Return(new Expression.BinOp(
                            new Expression.Call("main", List.of(new Expression.BinOp(
                                    new Expression.Variable("arg"),
                                    new Expression.IntLiteral(1),
                                    Expression.BinOp.Type.SUB
                            ))),
                            new Expression.Call("main", List.of(new Expression.BinOp(
                                    new Expression.Variable("arg"),
                                    new Expression.IntLiteral(2),
                                    Expression.BinOp.Type.SUB
                            ))),
                            Expression.BinOp.Type.ADD
                    ))
            ))
    );

    @Test
    public void testInterpreterReturnValueFibonacci() throws InterpreterException, SyntaxException {
        Interpreter interpreter = new Interpreter(FIBONACCI);
        Assertions.assertEquals(interpreter.run(new int[] { 1 }), 1);
        Assertions.assertEquals(interpreter.run(new int[] { 2 }), 2);
        Assertions.assertEquals(interpreter.run(new int[] { 3 }), 3);
        Assertions.assertEquals(interpreter.run(new int[] { 4 }), 5);
        Assertions.assertEquals(interpreter.run(new int[] { 5 }), 8);
        Assertions.assertEquals(interpreter.run(new int[] { 6 }), 13);
        Assertions.assertEquals(interpreter.run(new int[] { 7 }), 21);
    }

    @Test
    public void testCodeGenReturnValueFibonacci() throws CodeGenException {
        List<MachineInstruction<StackMachine>> code = new StackMachineCodeGenerator().codeGen(FIBONACCI);
        for(MachineInstruction<StackMachine> line : code) {
            System.out.println(line);
        }
    }

    @NotNull
    public static Map<String, List<Declaration>> getAstsFromDirectory(@NotNull String directoryName) throws IOException {
        Map<String, List<Declaration>> asts = new HashMap<>();
        File directory = new File(directoryName);
        for(File file : directory.listFiles()) {
            if (file.isDirectory())
                asts.putAll(getAstsFromDirectory(file.getName()));
            else
                asts.put(file.getName(), getAstFromFile(file.getPath()));
        }
        return asts;
    }

    @NotNull
    public static List<Declaration> getAstFromFile(@NotNull String fileName) throws IOException {
        PreppyLexer lexer = new PreppyLexer(new ANTLRFileStream(fileName));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PreppyParser parser = new PreppyParser(tokens);
        List<Declaration> decls = parser.declarations().out;
        if (decls == null)
            throw new IOException("Parser returned null.");
        return decls;
    }

    @Test
    public void testExamplesParse() throws IOException {
        getAstsFromDirectory("examples/");
    }
}
