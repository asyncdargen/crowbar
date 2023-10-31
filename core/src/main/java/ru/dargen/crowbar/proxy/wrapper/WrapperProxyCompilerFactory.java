package ru.dargen.crowbar.proxy.wrapper;

import ru.dargen.crowbar.proxy.wrapper.data.WrapperProxyData;
import ru.dargen.crowbar.proxy.wrapper.scanner.WrapperProxyScanner;

public interface WrapperProxyCompilerFactory {

    <T> WrapperProxyCompiler<T, ?> create(WrapperProxyData<T> data);

    default <T> WrapperProxyCompiler<T, ?> create(Class<T> proxyClass) {
        return create(WrapperProxyScanner.scan(proxyClass));
    }

}
