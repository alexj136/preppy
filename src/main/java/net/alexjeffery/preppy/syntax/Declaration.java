package net.alexjeffery.preppy.syntax;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

public class Declaration implements Syntax {

    @NotNull
    private String name;

    @NotNull
    private List<String> parameterNames;

    @NotNull
    private Statement body;

    public Declaration(@NotNull String name, @NotNull List<String> parameterNames, @NotNull Statement body) {
        this.name = name;
        this.parameterNames = parameterNames;
        this.body = body;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public List<String> getParameterNames() {
        return parameterNames;
    }

    @NotNull
    public Statement getBody() {
        return body;
    }
}
