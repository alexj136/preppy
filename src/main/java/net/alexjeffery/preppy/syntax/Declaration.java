package net.alexjeffery.preppy.syntax;

import net.alexjeffery.preppy.interpret.Interpreter;
import net.alexjeffery.preppy.syntax.visitor.DeclarationVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @NotNull
    public <I, O, E extends Throwable> O accept(@NotNull DeclarationVisitor<I, O, E> visitor, @NotNull I input) throws E {
        return visitor.visit(this, input);
    }

    @NotNull
    public static Map<String, Declaration> listToMap(@NotNull List<Declaration> declarationList) throws SyntaxException {
        Map<String, Declaration> declarationMap = new HashMap<>();
        for(Declaration declaration : declarationList) {
            String name = declaration.getName();
            if (declarationMap.containsKey(name)) {
                throw new SyntaxException("Multiple functions with name '" + name + "'.");
            }
            declarationMap.put(name, declaration);
        }
        return declarationMap;
    }
}
