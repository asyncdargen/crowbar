package ru.dargen.crowbar.proxy;

import lombok.experimental.UtilityClass;
import ru.dargen.crowbar.proxy.wrapper.bultin.BuiltInWrapperProxyCompilerFactory;

@UtilityClass
public class ProxyWrappers {

    public BuiltInWrapperProxyCompilerFactory builtIn() {
        return BuiltInWrapperProxyCompilerFactory.INSTANCE;
    }

}
