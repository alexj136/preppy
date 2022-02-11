package net.alexjeffery.preppy.interpret;

import net.alexjeffery.preppy.syntax.Declaration;
import net.alexjeffery.preppy.syntax.Expression;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.alexjeffery.preppy.syntax.Syntax.SyntaxException;

public class Interpreter {

    @NotNull
    private Map<String, Declaration> declarations;

    public Interpreter(@NotNull List<Declaration> declarations) throws InterpreterException, SyntaxException {
        this.declarations = Declaration.listToMap(declarations);
    }

    public int run(@NotNull int[] input) throws InterpreterException {
        Declaration main = declarations.get("main");
        if (main == null)
            throw new InterpreterException("Declaration 'main' not found.");
        List<Expression> arguments = new ArrayList<>(input.length);
        for(int arg : input) {
            arguments.add(new Expression.IntLiteral(arg));
        }
        return new Expression.Call("main", arguments).accept(ExpressionInterpreter.getInstance(), new InterpreterScope(declarations));
    }

    public static class InterpreterException extends Exception {

        public InterpreterException(@NotNull String message) {
            super(message);
        }
    }

    public static class InterpreterScope {

        @NotNull
        private Map<String, Declaration> declarations;

        @NotNull
        private Map<String, Integer> store;

        @Nullable
        private Integer returnValue = null;

        public InterpreterScope(@NotNull Map<String, Declaration> declarations) {
            this.declarations = declarations;
            this.store = new HashMap<>();
        }

        @Nullable
        public Integer getReturnValue() {
            return returnValue;
        }

        public void setReturnValue(Integer returnValue) {
            this.returnValue = returnValue;
        }

        @NotNull
        public InterpreterScope child() {
            return new InterpreterScope(declarations);
        }

        @NotNull
        public Declaration lookupDeclaration(@NotNull String name) throws InterpreterException {
            Declaration declaration = declarations.get(name);
            if (declaration == null)
                throw new InterpreterException("Declaration '" + name + "' not found.");
            return declaration;
        }

        @NotNull
        public void putVariable(@NotNull String name, @NotNull Integer value) throws InterpreterException {
            if(!variableIsDefined(name))
                variableNotFound(name);
            store.put(name, value);
        }

        public boolean variableIsDefined(@NotNull String name) {
            return store.containsKey(name);
        }

        @NotNull
        public Integer lookupVariable(@NotNull String name) throws InterpreterException {
            if (!variableIsDefined(name))
                variableNotFound(name);
            return store.get(name);
        }

        public void variableNotFound(@NotNull String name) throws InterpreterException {
            throw new InterpreterException("Variable '" + name + "' not found.");
        }
    }

}
