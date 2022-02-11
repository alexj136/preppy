package net.alexjeffery.preppy.vm;

import net.alexjeffery.preppy.syntax.Expression;
import org.antlr.v4.runtime.misc.NotNull;

import static net.alexjeffery.preppy.syntax.Expression.Opcode;

public interface StackMachineInstruction extends MachineInstruction<StackMachine> {

    public static class OpcodeInstruction implements StackMachineInstruction {

        @NotNull
        private Expression.Opcode opcode;

        public OpcodeInstruction(@NotNull Opcode opcode) {
            this.opcode = opcode;
        }

        @NotNull
        public Expression.Opcode getOpcode() {
            return opcode;
        }
    }

    public static class PushImm implements StackMachineInstruction {

        private int value;

        public PushImm(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static class Label implements StackMachineInstruction {

        @NotNull
        private String name;

        public Label(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        public String getName() {
            return name;
        }
    }

    public static class Jump implements StackMachineInstruction {

        @NotNull
        private String labelName;

        public Jump(@NotNull String labelName) {
            this.labelName = labelName;
        }

        @NotNull
        public String getName() {
            return labelName;
        }
    }

    public static class JumpIf implements StackMachineInstruction {

        @NotNull
        private String labelName;

        public JumpIf(@NotNull String labelName) {
            this.labelName = labelName;
        }

        @NotNull
        public String getName() {
            return labelName;
        }
    }

    public static class JumpLink implements StackMachineInstruction {

        @NotNull
        private String labelName;

        public JumpLink(@NotNull String labelName) {
            this.labelName = labelName;
        }

        @NotNull
        public String getName() {
            return labelName;
        }
    }

    public static class JumpAddr implements StackMachineInstruction {

        public JumpAddr() { }
    }

    public static class Copy implements StackMachineInstruction {

        // The value on top of the stack is a reference somewhere else in the stack.
        // Copy the value from the referenced point on the stack to the top of the stack, popping the reference first.
        public Copy() { }
    }

    public static class Save implements StackMachineInstruction {

        // Save the value on top of the stack into the stack reference underneath the value. Pop both of them.
        public Save() { }
    }

    public static class PushFramePointer implements StackMachineInstruction {

        // Push the current frame pointer on the stack
        public PushFramePointer() { }
    }

    public static class SetFramePointer implements StackMachineInstruction {

        // Set the frame pointer to point to the current stack size (i.e. address of top stack element + 1)
        public SetFramePointer() { }
    }
}
