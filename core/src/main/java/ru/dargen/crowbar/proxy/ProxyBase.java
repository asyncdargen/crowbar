package ru.dargen.crowbar.proxy;

import lombok.SneakyThrows;

import java.lang.reflect.Proxy;

public interface ProxyBase {

    @SneakyThrows
    default ProxyBoundHandler proxyHandler() {
        if (Proxy.isProxyClass(getClass())
                && Proxy.getInvocationHandler(this) instanceof ProxyBoundHandler handler) {
            return handler;
        }

        throw new IllegalAccessException(getClass().getName());
    }

}
