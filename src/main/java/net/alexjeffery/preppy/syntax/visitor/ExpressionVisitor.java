package net.alexjeffery.preppy.syntax.visitor;

import net.alexjeffery.preppy.syntax.Expression;
import org.antlr.v4.runtime.misc.NotNull;

public interface ExpressionVisitor<I, O, E extends Throwable> {

    @NotNull
    public O visit(@NotNull Expression expression, @NotNull I input) throws E;

    @NotNull
    public O visit(@NotNull Expression.IntLiteral intLiteral, @NotNull I input) throws E;

    @NotNull
    public O visit(@NotNull Expression.Variable variable, @NotNull I input) throws E;

    @NotNull
    public O visit(@NotNull Expression.BinOp binOp, @NotNull I input) throws E;

    @NotNull
    public O visit(@NotNull Expression.UnOp unOp, @NotNull I input) throws E;

    @NotNull
    public O visit(@NotNull Expression.Call call, @NotNull I input) throws E;
}
