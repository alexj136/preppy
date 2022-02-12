package net.alexjeffery.preppy.output;

import net.alexjeffery.preppy.syntax.Declaration;
import net.alexjeffery.preppy.syntax.Expression;
import net.alexjeffery.preppy.syntax.Statement;
import net.alexjeffery.preppy.syntax.visitor.ExpressionVisitor;
import net.alexjeffery.preppy.syntax.visitor.StatementVisitor;
import net.alexjeffery.preppy.vm.MachineInstruction;
import net.alexjeffery.preppy.vm.StackMachine;
import net.alexjeffery.preppy.vm.StackMachineInstruction;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.alexjeffery.preppy.syntax.Expression.*;
import static net.alexjeffery.preppy.syntax.Expression.BinOp.Type.SUB;
import static net.alexjeffery.preppy.syntax.Statement.*;
import static net.alexjeffery.preppy.vm.StackMachineInstruction.*;

public class StackMachineCodeGenerator implements CodeGenerator<StackMachine, StackMachineCodeGenerator.CodeGenException> {

    public static class CodeGenContext {

        @NotNull
        private List<Declaration> astList;

        @NotNull
        private Map<String, Declaration> astMap;

        @NotNull
        private String currentDeclaration;

        @NotNull
        private Integer freshName;

        @NotNull
        private List<MachineInstruction<StackMachine>> instructions;

        public CodeGenContext(@NotNull List<Declaration> astList) throws CodeGenException {
            this.astList = astList;
            try {
                this.astMap = Declaration.listToMap(astList);
            } catch (SyntaxException e) {
                throw new CodeGenException(e.getMessage());
            }
            this.currentDeclaration = astList.get(0).getName();
            this.freshName = 0;
            this.instructions = new ArrayList<>();
        }

        @NotNull
        public String freshLabel() {
            return "l" + (freshName++);
        }

        public void appendInstruction(@NotNull StackMachineInstruction instruction) {
            instructions.add(instruction);
        }

        @NotNull
        public int getVariableOffset(String name) throws CodeGenException {
            int offset = astMap.get(currentDeclaration).getParameterNames().indexOf(name);
            if (offset == -1) {
                throw new CodeGenException("Variable '" + name + "' not found.");
            }
            return offset;
        }
    }

    @NotNull
    private static String entryLabelName(@NotNull String functionName) {
        return "ENTRY_" + functionName;
    }

    public static class CodeGenException extends Exception {

        public CodeGenException(@NotNull String message) {
            super(message);
        }
    }

    @Override
    @NotNull
    public List<MachineInstruction<StackMachine>> codeGen(@NotNull List<Declaration> program) throws CodeGenException {
        CodeGenContext context = new CodeGenContext(program);
        context.appendInstruction(new SetFramePointer());
        context.appendInstruction(new JumpLink("main"));
        context.appendInstruction(new Jump("PROG_END"));
        for(Declaration declaration : program) {
            context.appendInstruction(new Label(entryLabelName(declaration.getName())));
            declaration.getBody().accept(GenStatement.getInstance(), context);
        }
        context.appendInstruction(new Label("PROG_END"));
        return context.instructions;
    }

    public static class GenExpression implements ExpressionVisitor<CodeGenContext, Void, CodeGenException> {

        public static GenExpression INSTANCE = null;
        private GenExpression() {}
        public static GenExpression getInstance() { if (INSTANCE == null) INSTANCE = new GenExpression(); return INSTANCE; }

        @Override
        public Void visit(@NotNull Expression expression, @NotNull CodeGenContext context) throws CodeGenException {
            throw new CodeGenException("Unsupported Expression type '" + expression.getClass().getName() + "'.");
        }

        @Override
        public Void visit(@NotNull IntLiteral intLiteral, @NotNull CodeGenContext context) throws CodeGenException {
            context.appendInstruction(new PushImm(intLiteral.getValue()));
            return null;
        }

        @Override
        public Void visit(@NotNull Variable variable, @NotNull CodeGenContext context) throws CodeGenException {
            Integer offset = context.getVariableOffset(variable.getName());
            context.appendInstruction(new PushFramePointer());
            context.appendInstruction(new PushImm(offset));
            context.appendInstruction(new OpcodeInstruction(SUB));
            context.appendInstruction(new Copy());
            return null;
        }

        @Override
        public Void visit(@NotNull BinOp binOp, @NotNull CodeGenContext context) throws CodeGenException {
            binOp.getLeft().accept(this, context);
            binOp.getRight().accept(this, context);
            context.appendInstruction(new OpcodeInstruction(binOp.getType()));
            return null;
        }

        @Override
        public Void visit(@NotNull UnOp unOp, @NotNull CodeGenContext context) throws CodeGenException {
            unOp.getArgument().accept(this, context);
            context.appendInstruction(new OpcodeInstruction(unOp.getType()));
            return null;
        }

        @Override
        public Void visit(@NotNull Call call, @NotNull CodeGenContext context) throws CodeGenException {
            String calleeName = call.getName();
            List<Expression> argumentExpressions = call.getArguments();
            context.appendInstruction(new PushFramePointer());
            for(int i = argumentExpressions.size() - 1; i >= 0; i++) {
                Expression argument = argumentExpressions.get(i);
                argument.accept(this, context);
            }
            context.appendInstruction(new SetFramePointer());
            context.appendInstruction(new JumpLink(entryLabelName(calleeName)));
            for(int i = 0; i < argumentExpressions.size(); i++) {
                context.appendInstruction(new Swap());
                context.appendInstruction(new Pop());
            }
            context.appendInstruction(new Swap());
            context.appendInstruction(new PopFramePointer());
            return null;
        }
    }

    public static class GenStatement implements StatementVisitor<CodeGenContext, Void, CodeGenException> {

        public static GenStatement INSTANCE = null;
        private GenStatement() { }
        public static GenStatement getInstance() { if (INSTANCE == null) INSTANCE = new GenStatement(); return INSTANCE; }

        @Override
        public Void visit(Statement statement, CodeGenContext context) throws CodeGenException {
            throw new CodeGenException("Unsupported Statement type '" + statement.getClass().getName() + "'.");
        }

        @Override
        public Void visit(Block block, CodeGenContext context) throws CodeGenException {
            for(Statement statement : block.getStatements())
                statement.accept(this, context);
            return null;
        }

        @Override
        public Void visit(Assignment assignment, CodeGenContext context) throws CodeGenException {
            Integer offset = context.getVariableOffset(assignment.getName());
            context.appendInstruction(new PushFramePointer());
            context.appendInstruction(new PushImm(offset));
            context.appendInstruction(new OpcodeInstruction(SUB));
            assignment.getValue().accept(GenExpression.getInstance(), context);
            context.appendInstruction(new Save());
            return null;
        }

        @Override
        public Void visit(While _while, CodeGenContext context) throws CodeGenException {
            String uniqueLabelPart = context.freshLabel();
            String whileCondLabel = "WHILE_COND_" + uniqueLabelPart;
            String whileBodyLabel = "WHILE_BODY_" + uniqueLabelPart;
            context.appendInstruction(new Jump(whileCondLabel));
            context.appendInstruction(new Label(whileBodyLabel));
            _while.getBody().accept(this, context);
            context.appendInstruction(new Label(whileCondLabel));
            _while.getCondition().accept(GenExpression.getInstance(), context);
            context.appendInstruction(new JumpIf(whileBodyLabel));
            return null;
        }

        @Override
        public Void visit(Return _return, CodeGenContext context) throws CodeGenException {
            _return.getValue().accept(GenExpression.getInstance(), context);
            context.appendInstruction(new Swap());
            context.appendInstruction(new JumpAddr());
            return null;
        }

        @Override
        public Void visit(Cond cond, CodeGenContext context) throws CodeGenException {
            String uniqueLabelPart = context.freshLabel();
            String ifTrueLabel = "IF_TRUE_" + uniqueLabelPart;
            String ifEndLabel = "IF_END_" + uniqueLabelPart;
            cond.getCondition().accept(GenExpression.getInstance(), context);
            context.appendInstruction(new JumpIf(ifTrueLabel));
            cond.getFalseBranch().accept(this, context);
            context.appendInstruction(new Jump(ifEndLabel));
            context.appendInstruction(new Label(ifTrueLabel));
            cond.getTrueBranch().accept(this, context);
            context.appendInstruction(new Label(ifEndLabel));
            return null;
        }
    }
}
