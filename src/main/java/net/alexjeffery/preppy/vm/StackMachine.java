package net.alexjeffery.preppy.vm;

public class StackMachine implements Machine {

    public enum Instruction {
        PUSH, POP, ADD, SUB, MUL, DIV, MOD, EQ, LABEL, JUMP
    }
}
