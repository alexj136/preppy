package net.alexjeffery.preppy.syntax.visitor;

import net.alexjeffery.preppy.syntax.Statement;
import org.antlr.v4.runtime.misc.NotNull;

public interface StatementVisitor<I, O, E extends Throwable> {

    @NotNull
    public O visit(@NotNull Statement statement, @NotNull I input) throws E;

    @NotNull
    public O visit(@NotNull Statement.Block block, @NotNull I input) throws E;

    @NotNull
    public O visit(@NotNull Statement.Assignment assignment, @NotNull I input) throws E;

    @NotNull
    public O visit(@NotNull Statement.While _while, @NotNull I input) throws E;

    @NotNull
    public O visit(@NotNull Statement.Return _return, @NotNull I input) throws E;

    @NotNull
    public O visit(@NotNull Statement.Cond cond, @NotNull I input) throws E;
}
