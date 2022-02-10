package net.alexjeffery.preppy.output;

import net.alexjeffery.preppy.syntax.Declaration;
import net.alexjeffery.preppy.syntax.Expression;
import net.alexjeffery.preppy.syntax.visitor.ExpressionVisitor;
import net.alexjeffery.preppy.vm.MachineInstruction;
import net.alexjeffery.preppy.vm.StackMachine;
import net.alexjeffery.preppy.vm.StackMachineInstruction;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.alexjeffery.preppy.syntax.Expression.*;
import static net.alexjeffery.preppy.vm.StackMachineInstruction.*;

public class StackMachineCodeGenerator implements CodeGenerator<StackMachine> {

    @Override
    @NotNull
    public List<MachineInstruction<StackMachine>> codeGen(@NotNull List<Declaration> program) {
        throw new RuntimeException("Not yet implemented.");
    }

    public static class CodeGenContext {

        @NotNull
        private Integer freshName;

        @NotNull
        private List<StackMachineInstruction> instructions;

        public CodeGenContext() {
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
    }

    public static class CodeGenException extends Exception {

        public CodeGenException(@NotNull String message) {
            super(message);
        }
    }

    public static class GenExpression implements ExpressionVisitor<CodeGenContext, Void, CodeGenException> {

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
            throw new RuntimeException("Not yet implemented.");
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
}
