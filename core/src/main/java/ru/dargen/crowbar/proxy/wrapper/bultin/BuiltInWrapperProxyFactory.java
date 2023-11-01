package ru.dargen.crowbar.proxy.wrapper.bultin;

import lombok.Getter;
import lombok.Setter;
import ru.dargen.crowbar.Accessors;
import ru.dargen.crowbar.accessor.AccessorFactory;
import ru.dargen.crowbar.proxy.wrapper.WrapperProxy;
import ru.dargen.crowbar.proxy.wrapper.WrapperProxyFactory;
import ru.dargen.crowbar.proxy.wrapper.data.WrapperProxyData;
import ru.dargen.crowbar.proxy.wrapper.scanner.WrapperProxyScanner;

@Getter @Setter
public class BuiltInWrapperProxyFactory implements WrapperProxyFactory {

    public static BuiltInWrapperProxyFactory INSTANCE = new BuiltInWrapperProxyFactory();

    protected AccessorFactory accessorFactory = Accessors.invoke();

    public <T> WrapperProxy<T, ?> create(WrapperProxyData<T> data, AccessorFactory accessorFactory) {
        return new BuiltInWrapperProxy<T>(data, accessorFactory);
    }

    @Override
    public <T> WrapperProxy<T, ?> create(WrapperProxyData<T> data) {
        return create(data, accessorFactory);
    }

    public <T> WrapperProxy<T, ?> create(Class<T> proxyClass, AccessorFactory accessorFactory) {
        return create(WrapperProxyScanner.scan(proxyClass), accessorFactory);
    }

    public <T> WrapperProxy<T, ?> create(Class<T> proxyClass) {
        return create(proxyClass, accessorFactory);
    }

}
