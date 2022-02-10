package net.alexjeffery.preppy.interpret;

import net.alexjeffery.preppy.syntax.Declaration;
import net.alexjeffery.preppy.syntax.Expression;
import net.alexjeffery.preppy.syntax.visitor.ExpressionVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class ExpressionInterpreter implements ExpressionVisitor<Interpreter.InterpreterScope, Integer, Interpreter.InterpreterException> {

    private ExpressionInterpreter() { }

    private static ExpressionInterpreter INSTANCE = null;

    @NotNull
    public static ExpressionInterpreter getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ExpressionInterpreter();
        return INSTANCE;
    }

    @Override
    public Integer visit(Expression expression, Interpreter.InterpreterScope input) throws Interpreter.InterpreterException {
        throw new Interpreter.InterpreterException("Unsupported Expression type: " + expression.getClass().getName());
    }

    @Override
    public Integer visit(Expression.IntLiteral intLiteral, Interpreter.InterpreterScope input) throws Interpreter.InterpreterException {
        return intLiteral.getValue();
    }

    @Override
    public Integer visit(Expression.Variable variable, Interpreter.InterpreterScope input) throws Interpreter.InterpreterException {
        return input.lookupVariable(variable.getName());
    }

    @Override
    public Integer visit(Expression.BinOp binOp, Interpreter.InterpreterScope input) throws Interpreter.InterpreterException {
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
        }
        throw new Interpreter.InterpreterException("Unsupported BinOp type: " + binOp.getType().name());
    }

    @Override
    public Integer visit(Expression.UnOp unOp, Interpreter.InterpreterScope input) throws Interpreter.InterpreterException {
        Integer argument = unOp.getArgument().accept(this, input);
        switch(unOp.getType()) {
            case NOT: return argument == 0 ? 1 : 0;
        }
        throw new Interpreter.InterpreterException("Unsupported UnOp type: " + unOp.getType().name());
    }

    @Override
    public Integer visit(Expression.Call call, Interpreter.InterpreterScope input) throws Interpreter.InterpreterException {
        Declaration callee = input.lookupDeclaration(call.getName());
        if (callee.getParameterNames().size() != call.getArguments().size()) {
            throw new Interpreter.InterpreterException("Declaration '" + callee.getName() +
                    "' called with incorrect number of arguments; got " + call.getArguments().size() +
                    ", expected " + callee.getParameterNames().size());
        }
        Interpreter.InterpreterScope child = input.child();
        for (int i = 0; i < call.getArguments().size(); i++) {
            child.putVariable(callee.getParameterNames().get(i), call.getArguments().get(i).accept(this, input));
        }
        callee.getBody().accept(StatementInterpreter.getInstance(), child);
        return child.getReturnValue();
    }
}
