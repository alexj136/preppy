package net.alexjeffery.preppy.vm;

import net.alexjeffery.preppy.syntax.Expression;
import org.antlr.v4.runtime.misc.NotNull;

import static net.alexjeffery.preppy.syntax.Expression.Opcode;

public abstract class StackMachineInstruction implements MachineInstruction<StackMachine> {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public static class OpcodeInstruction extends StackMachineInstruction {

        @NotNull
        private Expression.Opcode opcode;

        public OpcodeInstruction(@NotNull Opcode opcode) {
            this.opcode = opcode;
        }

        @NotNull
        public Expression.Opcode getOpcode() {
            return opcode;
        }

        @Override
        public String toString() {
            return opcode.getClass().getSimpleName();
        }
    }

    public static class PushImm extends StackMachineInstruction {

        private int value;

        public PushImm(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return super.toString() + " " + value;
        }
    }

    public static class Label extends StackMachineInstruction {

        @NotNull
        private String name;

        public Label(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name + ":";
        }
    }

    public static abstract class AbstractLabelJump extends StackMachineInstruction {

        @NotNull
        private String labelName;

        public AbstractLabelJump(@NotNull String labelName) {
            this.labelName = labelName;
        }

        @NotNull
        public String getName() {
            return labelName;
        }

        @Override
        public String toString() {
            return super.toString() + " " + labelName;
        }
    }

    public static class Jump extends AbstractLabelJump {

        public Jump(String labelName) {
            super(labelName);
        }
    }

    public static class JumpIf extends AbstractLabelJump {

        public JumpIf(String labelName) {
            super(labelName);
        }
    }

    public static class JumpLink extends AbstractLabelJump {

        public JumpLink(String labelName) {
            super(labelName);
        }
    }

    /**
     * Jump to the code address on top of the stack, popping it.
     */
    public static class JumpAddr extends StackMachineInstruction { }

    /**
     * The value on top of the stack is a reference somewhere else in the stack.
     * Copy the value from the referenced point on the stack to the top of the stack, popping the reference first.
     */
    public static class Copy extends StackMachineInstruction { }

    /**
     * Save the value on top of the stack into the stack reference underneath the value. Pop both of them.
     */
    public static class Save extends StackMachineInstruction { }

    /**
     * Push the current frame pointer on the stack
     */
    public static class PushFramePointer extends StackMachineInstruction { }

    /**
     * Pop the stack into the frame pointer.
     */
    public static class PopFramePointer extends StackMachineInstruction { }

    /**
     * Set the frame pointer to point to the top element of the stack.
     */
    public static class SetFramePointer extends StackMachineInstruction { }

    /**
     * Swap the two top elements on the stack.
     */
    public static class Swap extends StackMachineInstruction { }

    /**
     * Discard the top element of the stack.
     */
    public static class Pop extends StackMachineInstruction { }
}
