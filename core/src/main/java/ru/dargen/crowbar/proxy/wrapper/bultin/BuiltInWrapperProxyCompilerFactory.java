package ru.dargen.crowbar.proxy.wrapper.bultin;

import lombok.Getter;
import lombok.Setter;
import ru.dargen.crowbar.Accessors;
import ru.dargen.crowbar.accessor.AccessorFactory;
import ru.dargen.crowbar.proxy.wrapper.WrapperProxyCompiler;
import ru.dargen.crowbar.proxy.wrapper.WrapperProxyCompilerFactory;
import ru.dargen.crowbar.proxy.wrapper.data.WrapperProxyData;
import ru.dargen.crowbar.proxy.wrapper.scanner.WrapperProxyScanner;

@Getter @Setter
public class BuiltInWrapperProxyCompilerFactory implements WrapperProxyCompilerFactory {

    public static BuiltInWrapperProxyCompilerFactory INSTANCE = new BuiltInWrapperProxyCompilerFactory();

    protected AccessorFactory accessorFactory = Accessors.invoke();

    public <T> WrapperProxyCompiler<T, ?> create(WrapperProxyData<T> data, AccessorFactory accessorFactory) {
        return new AccessorWrapperProxyCompiler<T>(data, accessorFactory);
    }

    @Override
    public <T> WrapperProxyCompiler<T, ?> create(WrapperProxyData<T> data) {
        return create(data, accessorFactory);
    }

    public <T> WrapperProxyCompiler<T, ?> create(Class<T> proxyClass, AccessorFactory accessorFactory) {
        return create(WrapperProxyScanner.scan(proxyClass), accessorFactory);
    }

    public <T> WrapperProxyCompiler<T, ?> create(Class<T> proxyClass) {
        return create(proxyClass, accessorFactory);
    }

}
