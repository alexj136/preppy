package net.alexjeffery.preppy.interpret;

import net.alexjeffery.preppy.syntax.Declaration;
import net.alexjeffery.preppy.syntax.Expression;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {

    @NotNull
    private Map<String, Declaration> declarations;

    public Interpreter(@NotNull List<Declaration> declarations) throws InterpreterException {
        this.declarations = mapDeclarations(declarations);
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

    @NotNull
    public static Map<String, Declaration> mapDeclarations(@NotNull List<Declaration> declarationList) throws InterpreterException {
        Map<String, Declaration> declarationMap = new HashMap<>();
        for(Declaration declaration : declarationList) {
            String name = declaration.getName();
            if (declarationMap.containsKey(name)) {
                throw new InterpreterException("Multiple functions with name '" + name + "'.");
            }
            declarationMap.put(name, declaration);
        }
        return declarationMap;
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
        public void putVariable(@NotNull String name, @NotNull Integer value) {
            store.put(name, value);
        }

        @NotNull
        public Integer lookupVariable(@NotNull String name) throws InterpreterException {
            if (!store.containsKey(name))
                throw new InterpreterException("Variable '" + name + "' not found.");
            return store.get(name);
        }
    }

}
