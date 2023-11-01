package ru.dargen.crowbar;

import lombok.experimental.UtilityClass;
import ru.dargen.crowbar.accessor.AccessorFactory;
import ru.dargen.crowbar.invoke.InvokeAccessorFactory;
import ru.dargen.crowbar.reflect.ReflectAccessorFactory;
import ru.dargen.crowbar.unsafe.UnsafeAccessorFactory;
import ru.dargen.crowbar.util.Reflection;

@UtilityClass
public class Accessors {

    public ReflectAccessorFactory reflect() {
        return ReflectAccessorFactory.INSTANCE;
    }

    public InvokeAccessorFactory invoke() {
        return InvokeAccessorFactory.INSTANCE;
    }

    public UnsafeAccessorFactory unsafe() {
        return UnsafeAccessorFactory.INSTANCE;
    }

    private AccessorFactory ASM_FACTORY; //if using crowbar-asm
    public AccessorFactory asm() {
        if (ASM_FACTORY == null) {
            ASM_FACTORY = Reflection.getStaticFieldValue(
                    Reflection.getClass("ru.dargen.crowbar.asm.AsmAccessorFactory"), "INSTANCE");
        }

        return ASM_FACTORY;
    }

}
