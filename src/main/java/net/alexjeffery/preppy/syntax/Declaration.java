package net.alexjeffery.preppy.syntax;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

public class Declaration implements Syntax {

    @NotNull
    private String name;

    @NotNull
    private List<String> argumentNames;

    @NotNull
    private Statement body;

    public Declaration(@NotNull String name, @NotNull List<String> argumentNames, @NotNull Statement body) {
        this.name = name;
        this.argumentNames = argumentNames;
        this.body = body;
    }
}
