package ru.dargen.crowbar.proxy.wrapper;

import ru.dargen.crowbar.proxy.wrapper.data.WrapperProxyData;
import ru.dargen.crowbar.proxy.wrapper.scanner.WrapperProxyScanner;

public interface WrapperProxyFactory {

    <T> WrapperProxy<T, ?> create(WrapperProxyData<T> data);

    default <T> WrapperProxy<T, ?> create(Class<T> proxyClass) {
        return create(WrapperProxyScanner.scan(proxyClass));
    }

}
