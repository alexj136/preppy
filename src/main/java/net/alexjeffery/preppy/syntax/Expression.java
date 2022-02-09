package net.alexjeffery.preppy.syntax;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

public interface Expression extends Syntax {

    public static class IntLiteral implements Expression {

        private int value;

        public IntLiteral(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static class Variable implements Expression {

        @NotNull
        private String name;

        public Variable(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        public String getName() {
            return name;
        }
    }

    public static class BinOp implements Expression {

        public static enum Type {
            // Arithmetic
            ADD, SUB, MUL, DIV, MOD,

            // Comparison
            EQ, LESS, GREATER, LESS_EQ, GREATER_EQ, NEQ,

            // Boolean
            AND, OR, NOT
        }

        @NotNull
        private Expression left;

        @NotNull
        private Expression right;

        @NotNull
        private Type type;

        public BinOp(@NotNull Expression left, @NotNull Expression right, @NotNull Type type) {
            this.left = left;
            this.right = right;
            this.type = type;
        }

        @NotNull
        public Expression getLeft() {
            return left;
        }

        @NotNull
        public Expression getRight() {
            return right;
        }

        @NotNull
        public Type getType() {
            return type;
        }
    }

    public static class Call implements Expression {

        @NotNull
        public String name;

        @NotNull
        public List<Expression> arguments;

        public Call(@NotNull String name, @NotNull List<Expression> arguments) {
            this.name = name;
            this.arguments = arguments;
        }

        @NotNull
        public String getName() {
            return name;
        }

        @NotNull
        public List<Expression> getArguments() {
            return arguments;
        }
    }
}
