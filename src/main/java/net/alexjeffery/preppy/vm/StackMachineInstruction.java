package net.alexjeffery.preppy.vm;

import net.alexjeffery.preppy.syntax.Expression;
import org.antlr.v4.runtime.misc.NotNull;

public interface StackMachineInstruction {

    public static class OpcodeInstruction implements StackMachineInstruction {

        @NotNull
        private Expression.Opcode opcode;

        public OpcodeInstruction(@NotNull Expression.Opcode opcode) {
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
}
