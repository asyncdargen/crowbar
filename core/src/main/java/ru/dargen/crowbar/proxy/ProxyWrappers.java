package ru.dargen.crowbar.proxy;

import lombok.experimental.UtilityClass;
import ru.dargen.crowbar.proxy.wrapper.WrapperProxyFactory;
import ru.dargen.crowbar.proxy.wrapper.bultin.BuiltInWrapperProxyFactory;
import ru.dargen.crowbar.util.Reflection;

@UtilityClass
public class ProxyWrappers {

    public BuiltInWrapperProxyFactory builtIn() {
        return BuiltInWrapperProxyFactory.INSTANCE;
    }

    private static WrapperProxyFactory ASM_FACTORY; //if using crowbar-asm
    public static WrapperProxyFactory asm() {
        if (ASM_FACTORY == null) {
            ASM_FACTORY = Reflection.getStaticFieldValue(
                    Reflection.getClass("ru.dargen.crowbar.proxy.wrapper.asm.AsmWrapperProxyFactory"),
                    "INSTANCE"
            );
        }

        return ASM_FACTORY;
    }

}
