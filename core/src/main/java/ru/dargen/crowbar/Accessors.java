package ru.dargen.crowbar;

import lombok.experimental.UtilityClass;
import ru.dargen.crowbar.invoke.InvokeAccessorFactory;
import ru.dargen.crowbar.reflect.ReflectAccessorFactory;
import ru.dargen.crowbar.unsafe.UnsafeAccessorFactory;

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

}
