package net.alexjeffery.preppy.interpret;

import net.alexjeffery.preppy.syntax.Declaration;
import net.alexjeffery.preppy.syntax.Expression;
import net.alexjeffery.preppy.syntax.Statement;
import net.alexjeffery.preppy.syntax.visitor.DeclarationVisitor;
import net.alexjeffery.preppy.syntax.visitor.ExpressionVisitor;
import net.alexjeffery.preppy.syntax.visitor.StatementVisitor;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {

    @NotNull
    private Map<String, Declaration> program;

    public Interpreter(@NotNull List<Declaration> program) throws InterpreterException {
        Map<String, Declaration> map = new HashMap<>();
        DeclarationNameGetter nameGetter = new DeclarationNameGetter();
        for (Declaration declaration : program) {
            declaration.accept(nameGetter, map);
        }
        this.program = map;
    }

    public int run(@NotNull int[] input) throws InterpreterException {
        Declaration main = program.get("main");
        if (main == null)
            throw new InterpreterException("Function 'main' not found");
        List<Expression> arguments = new ArrayList<>(input.length);
        for(int arg : input) {
            arguments.add(new Expression.IntLiteral(arg));
        }
        return new Expression.Call("main", arguments).accept(ExpressionInterpreter.getInstance(), new InterpreterScope(program));
    }

    public static class InterpreterException extends Exception {

        public InterpreterException(@NotNull String message) {
            super(message);
        }
    }

    public static class DeclarationNameGetter implements DeclarationVisitor<Map<String, Declaration>, Void, InterpreterException> {

        private DeclarationNameGetter() { }

        private static DeclarationNameGetter INSTANCE = null;

        @NotNull
        public static DeclarationNameGetter getInstance() {
            if (INSTANCE == null)
                INSTANCE = new DeclarationNameGetter();
            return INSTANCE;
        }

        @Override
        public Void visit(@NotNull Declaration declaration, @NotNull Map<String, Declaration> input) throws InterpreterException {
            input.put(declaration.getName(), declaration);
            return null;
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
            if (!store.containsKey(name))
                throw new InterpreterException("Declaration '" + name + "' not found");
            return declarations.get(name);
        }

        @NotNull
        public void putVariable(@NotNull String name, @NotNull Integer value) {
            store.put(name, value);
        }

        @NotNull
        public Integer lookupVariable(@NotNull String name) throws InterpreterException {
            if (!store.containsKey(name))
                throw new InterpreterException("Variable '" + name + "' not found");
            return store.get(name);
        }
    }

    public static class StatementInterpreter implements StatementVisitor<InterpreterScope, Boolean, InterpreterException> {

        private StatementInterpreter() { }

        private static StatementInterpreter INSTANCE = null;

        @NotNull
        public static StatementInterpreter getInstance() {
            if (INSTANCE == null)
                INSTANCE = new StatementInterpreter();
            return INSTANCE;
        }

        @Override
        public Boolean visit(Statement statement, InterpreterScope input) throws InterpreterException {
            throw new InterpreterException("Unsupported Statement type: " + statement.getClass().getName());
        }

        @Override
        public Boolean visit(Statement.Block block, InterpreterScope input) throws InterpreterException {
            for(Statement statement : block.getStatements()) {
                if (statement.accept(this, input)) return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

        @Override
        public Boolean visit(Statement.Assignment assignment, InterpreterScope input) throws InterpreterException {
            input.putVariable(assignment.getName(),
                    assignment.getValue().accept(ExpressionInterpreter.getInstance(), input));
            return Boolean.FALSE;
        }

        @Override
        public Boolean visit(Statement.While _while, InterpreterScope input) throws InterpreterException {
            Boolean returning = Boolean.FALSE;
            while(_while.getCondition().accept(ExpressionInterpreter.getInstance(), input) != 0) {
                returning = _while.getBody().accept(this, input);
            }
            return returning;
        }

        @Override
        public Boolean visit(Statement.Return _return, InterpreterScope input) throws InterpreterException {
            input.setReturnValue(_return.getValue().accept(ExpressionInterpreter.getInstance(), input));
            return Boolean.TRUE;
        }

        @Override
        public Boolean visit(Statement.Cond cond, InterpreterScope input) throws InterpreterException {
            Statement toExecute = cond.getCondition().accept(ExpressionInterpreter.getInstance(), input) != 0 ?
                    cond.getTrueBranch() : cond.getFalseBranch();
            return toExecute.accept(this, input);
        }
    }

    public static class ExpressionInterpreter implements ExpressionVisitor<InterpreterScope, Integer, InterpreterException> {

        private ExpressionInterpreter() { }

        private static ExpressionInterpreter INSTANCE = null;

        @NotNull
        public static ExpressionInterpreter getInstance() {
            if (INSTANCE == null)
                INSTANCE = new ExpressionInterpreter();
            return INSTANCE;
        }

        @Override
        public Integer visit(Expression expression, InterpreterScope input) throws InterpreterException {
            throw new InterpreterException("Unsupported Expression type: " + expression.getClass().getName());
        }

        @Override
        public Integer visit(Expression.IntLiteral intLiteral, InterpreterScope input) throws InterpreterException {
            return intLiteral.getValue();
        }

        @Override
        public Integer visit(Expression.Variable variable, InterpreterScope input) throws InterpreterException {
            return input.lookupVariable(variable.getName());
        }

        @Override
        public Integer visit(Expression.BinOp binOp, InterpreterScope input) throws InterpreterException {
            Integer left = binOp.getLeft().accept(this, input);
            Integer right = binOp.getRight().accept(this, input);
            switch(binOp.getType()) {
                case ADD: return left + right;
                case SUB: return left - right;
                case MUL: return left * right;
                case DIV: return left / right;
                case MOD: return left % right;
                case EQ: return left.equals(right) ? 1 : 0;
                case LESS: return left < right ? 1 : 0;
                case GREATER: return left > right ? 1 : 0;
                case LESS_EQ: return left <= right ? 1 : 0;
                case GREATER_EQ: return left >= right ? 1 : 0;
                case NEQ: return !left.equals(right) ? 1 : 0;
                case AND: return (left != 0) && (right != 0) ? 1 : 0;
                case OR: return (left != 0) || (right != 0) ? 1 : 0;
                case NOT: throw new RuntimeException("Not yet implemented");
            }
            throw new InterpreterException("Unsupported BinOp type: " + binOp.getType().name());
        }

        @Override
        public Integer visit(Expression.Call call, InterpreterScope input) throws InterpreterException {
            Declaration callee = input.lookupDeclaration(call.getName());
            if (callee.getParameterNames().size() != call.getArguments().size()) {
                throw new InterpreterException("Declaration '" + callee.getName() +
                        "' called with incorrect number of arguments; got " + call.getArguments().size() +
                        ", expected " + callee.getParameterNames().size());
            }
            InterpreterScope child = input.child();
            for (int i = 0; i < call.getArguments().size(); i++) {
                child.putVariable(callee.getParameterNames().get(i), call.getArguments().get(i).accept(this, input));
            }
            callee.getBody().accept(StatementInterpreter.getInstance(), child);
            return child.getReturnValue();
        }
    }
}
