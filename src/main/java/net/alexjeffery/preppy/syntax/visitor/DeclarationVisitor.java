package net.alexjeffery.preppy.syntax.visitor;

import net.alexjeffery.preppy.syntax.Declaration;
import org.antlr.v4.runtime.misc.NotNull;

public interface DeclarationVisitor<I, O, E extends Throwable> {

    @NotNull
    public O visit(@NotNull Declaration declaration, @NotNull I input) throws E;
}
