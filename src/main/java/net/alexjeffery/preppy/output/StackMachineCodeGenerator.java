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
import static net.alexjeffery.preppy.syntax.Expression.BinOp.Type.ADD;
import static net.alexjeffery.preppy.syntax.Statement.*;
import static net.alexjeffery.preppy.vm.StackMachineInstruction.*;

public class StackMachineCodeGenerator implements CodeGenerator<StackMachine> {

    @Override
    @NotNull
    public List<MachineInstruction<StackMachine>> codeGen(@NotNull List<Declaration> program) {
        throw new RuntimeException("Not yet implemented.");
    }

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
        private List<StackMachineInstruction> instructions;

        public CodeGenContext(@NotNull List<Declaration> astList) throws SyntaxException {
            this.astList = astList;
            this.astMap = Declaration.listToMap(astList);
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

    public static class CodeGenException extends Exception {

        public CodeGenException(@NotNull String message) {
            super(message);
        }
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
            Integer offset = context.getVariableOffset(variable.getName()) + 2;
            context.appendInstruction(new PushFramePointer());
            context.appendInstruction(new PushImm(2));
            context.appendInstruction(new OpcodeInstruction(ADD));
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
        public Void visit(@NotNull Call call, @NotNull CodeGenContext input) throws CodeGenException {
            throw new RuntimeException("Not yet implemented.");
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
            Integer offset = context.getVariableOffset(assignment.getName()) + 2;
            context.appendInstruction(new PushFramePointer());
            context.appendInstruction(new PushImm(2));
            context.appendInstruction(new OpcodeInstruction(ADD));
            assignment.getValue().accept(GenExpression.getInstance(), context);
            context.appendInstruction(new Save());
            return null;
        }

        @Override
        public Void visit(While _while, CodeGenContext context) throws CodeGenException {
            return null;
        }

        @Override
        public Void visit(Return _return, CodeGenContext context) throws CodeGenException {
            return null;
        }

        @Override
        public Void visit(Cond cond, CodeGenContext context) throws CodeGenException {
            return null;
        }
    }
}
