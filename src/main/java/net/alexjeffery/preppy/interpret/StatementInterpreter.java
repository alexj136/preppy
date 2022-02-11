package net.alexjeffery.preppy.interpret;

import net.alexjeffery.preppy.syntax.Statement;
import net.alexjeffery.preppy.syntax.visitor.StatementVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import static net.alexjeffery.preppy.interpret.Interpreter.InterpreterException;
import static net.alexjeffery.preppy.interpret.Interpreter.InterpreterScope;

public class StatementInterpreter implements StatementVisitor<InterpreterScope, Boolean, InterpreterException> {

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
        throw new InterpreterException("Unsupported Statement type '" + statement.getClass().getName() + "'.");
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
