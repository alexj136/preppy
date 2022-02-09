package net.alexjeffery.preppy.syntax;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

public interface Statement extends Syntax {

    public static class Block implements Statement {

        @NotNull
        private List<Statement> statements;

        public Block(@NotNull List<Statement> statements) {
            this.statements = statements;
        }

        @NotNull
        public List<Statement> getStatements() {
            return statements;
        }
    }

    public static class Assignment implements Statement {

        @NotNull
        private String name;

        @NotNull
        private Expression value;

        public Assignment(@NotNull String name, @NotNull Expression value) {
            this.name = name;
            this.value = value;
        }

        @NotNull
        public String getName() {
            return name;
        }

        @NotNull
        public Expression getValue() {
            return value;
        }
    }

    public static class While implements Statement {

        @NotNull
        private Expression condition;

        @NotNull
        private Statement body;

        public While(@NotNull Expression condition, @NotNull Statement body) {
            this.condition = condition;
            this.body = body;
        }

        @NotNull
        public Expression getCondition() {
            return condition;
        }

        @NotNull
        public Statement getBody() {
            return body;
        }
    }

    public static class Return implements Statement {

        @NotNull
        private Expression value;

        public Return(@NotNull Expression value) {
            this.value = value;
        }

        @NotNull
        public Expression getValue() {
            return value;
        }
    }

    public static class Cond implements Statement {

        @NotNull
        private Expression condition;

        @NotNull
        private Statement trueBranch;

        @NotNull
        private Statement falseBranch;

        public Cond(@NotNull Expression condition, @NotNull Statement trueBranch, @NotNull Statement falseBranch) {
            this.condition = condition;
            this.trueBranch = trueBranch;
            this.falseBranch = falseBranch;
        }

        @NotNull
        public Expression getCondition() {
            return condition;
        }

        @NotNull
        public Statement getTrueBranch() {
            return trueBranch;
        }

        @NotNull
        public Statement getFalseBranch() {
            return falseBranch;
        }
    }
}
