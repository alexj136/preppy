package net.alexjeffery.preppy.vm;

public class StackMachine implements Machine {

    public static enum Instruction {
        PUSH, POP, ADD, SUB, MUL, DIV, MOD, EQ, LESS_EQ, LABEL, JUMP, JUMPIF, JUMPLINK
    }
}
