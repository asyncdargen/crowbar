package ru.dargen.crowbar.proxy;

import ru.dargen.crowbar.accessor.MethodAccessor;

import java.lang.reflect.Method;

public interface ProxyMethodExecutor {

    static ProxyMethodExecutor wrap(MethodAccessor<Object> accessor) {
        return (proxy, method, args) -> accessor.invoke(proxy, args);
    }

    Object execute(Object proxy, Method method, Object[] args);


}
