package net.alexjeffery.preppy.vm;

import net.alexjeffery.preppy.syntax.Expression;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.alexjeffery.preppy.vm.StackMachineInstruction.*;

public class StackMachine implements Machine {

    @NotNull
    private List<StackMachineInstruction> program;

    @NotNull
    private List<Integer> stack;

    @NotNull
    private Integer programCounter;

    @NotNull
    private Map<String, Integer> labels;

    public StackMachine(List<StackMachineInstruction> program) {
        this.program = program;
        this.stack = new ArrayList<>();
        this.programCounter = 0;
        this.labels = new HashMap<>();
        for(int i = 0; i < program.size(); i++) {
            StackMachineInstruction instruction = program.get(i);
            if (instruction instanceof Label) {
                Label label = (Label) instruction;
                labels.put(label.getName(), i);
            }
        }
    }

    private void push(@NotNull Integer value) {
        stack.add(value);
    }

    @NotNull
    private Integer pop() {
        return stack.remove(stack.size() - 1);
    }

    private void doJump(@NotNull String labelName) throws StackMachineException {
        Integer jumpTarget = labels.get(labelName);
        if (jumpTarget == null)
            throw new StackMachineException("Label '" + labelName + "' not found.");
        doJump(jumpTarget);
    }

    private void doJump(@NotNull Integer address) {
        programCounter = address;
    }

    public void step() throws StackMachineException {

        if (programCounter >= program.size())
            throw new StackMachineException("Program counter out of bounds.");

        StackMachineInstruction instruction = program.get(programCounter);

        if (instruction instanceof OpcodeInstruction) {
            OpcodeInstruction opcodeInstruction = (OpcodeInstruction) instruction;
            Expression.Opcode opcode = opcodeInstruction.getOpcode();
            if (opcode instanceof Expression.BinOp.Type) {
                Expression.BinOp.Type type = (Expression.BinOp.Type) opcode;
                Integer right = pop();
                Integer left = pop();
                switch (type) {
                    case ADD: push(left + right); break;
                    case SUB: push(left - right); break;
                    case MUL: push(left * right); break;
                    case DIV: push(left / right); break;
                    case MOD: push(left % right); break;
                    case EQ: push(left.equals(right) ? 1 : 0); break;
                    case LESS: push(left < right ? 1 : 0); break;
                    case GREATER: push(left > right ? 1 : 0); break;
                    case LESS_EQ: push(left <= right ? 1 : 0); break;
                    case GREATER_EQ: push(left >= right ? 1 : 0); break;
                    case NEQ: push(!left.equals(right) ? 1 : 0); break;
                    case AND: push((left != 0) && (right != 0) ? 1 : 0); break;
                    case OR: push((left != 0) || (right != 0) ? 1 : 0); break;
                }
            } else if (opcode instanceof Expression.UnOp.Type) {
                Expression.UnOp.Type type = (Expression.UnOp.Type) opcode;
                Integer argument = pop();
                switch (type) {
                    case NOT: push(argument == 0 ? 1 : 0); break;
                }
            }

        } else if (instruction instanceof PushImm) {
            PushImm pushImm = (PushImm) instruction;
            push(pushImm.getValue());
            programCounter++;

        } else if (instruction instanceof Label) {
            programCounter++;

        } else if (instruction instanceof Jump) {
            Jump jump = (Jump) instruction;
            doJump(jump.getName());

        } else if (instruction instanceof JumpIf) {
            JumpIf jumpIf = (JumpIf) instruction;
            if (pop() != 0) {
                doJump(jumpIf.getName());
            }

        } else if (instruction instanceof JumpLink) {
            JumpLink jumpLink = (JumpLink) instruction;
            push(programCounter + 1);
            doJump(jumpLink.getName());

        } else if (instruction instanceof JumpAddr) {
            doJump(pop());

        } else {
            throw new StackMachineException("Unsupported instruction type '" + instruction.getClass().getName() + "'.");
        }
    }

    public static class StackMachineException extends Exception {

        public StackMachineException(@NotNull String message) {
            super(message);
        }
    }
}
