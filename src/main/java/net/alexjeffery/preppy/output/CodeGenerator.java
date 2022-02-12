package net.alexjeffery.preppy.output;

import net.alexjeffery.preppy.syntax.Declaration;
import net.alexjeffery.preppy.vm.Machine;
import net.alexjeffery.preppy.vm.MachineInstruction;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

public interface CodeGenerator<T extends Machine, E extends Throwable> {

    @NotNull
    public List<MachineInstruction<T>> codeGen(@NotNull List<Declaration> program) throws E;
}
