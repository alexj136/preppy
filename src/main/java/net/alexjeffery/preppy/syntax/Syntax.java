package net.alexjeffery.preppy.syntax;

import org.antlr.v4.runtime.misc.NotNull;

public interface Syntax {

    public static class SyntaxException extends Exception {

        public SyntaxException(@NotNull String message) {
            super(message);
        }
    }
}
