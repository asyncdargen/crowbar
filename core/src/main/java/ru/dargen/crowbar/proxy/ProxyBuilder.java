package ru.dargen.crowbar.proxy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import ru.dargen.crowbar.util.Reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true, chain = true)
public class ProxyBuilder<T> {

    private final Class<T> proxyClass;
    private final Map<Method, ProxyMethodExecutor> executorMap = new HashMap<>();

    public ProxyBuilder<T> bindMethod(Method method, ProxyMethodExecutor executor) {
        executorMap.put(method, executor);
        return this;
    }

    public ProxyBuilder<T> bindMethod(Class<?> declaredClass, String name, Class<?>[] args,
                                      ProxyMethodExecutor executor) {
        return bindMethod(Reflection.getMethod(declaredClass, name, args), executor);
    }

    public ProxyBuilder<T> bindMethod(String name, Class<?>[] args, ProxyMethodExecutor executor) {
        return bindMethod(proxyClass, name, args, executor);
    }

    @SuppressWarnings("unchecked")
    public T build(ClassLoader classLoader) {
        var boundInvocationHandler = new ProxyBoundHandler(executorMap);
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{proxyClass}, boundInvocationHandler);
    }

    public T build() {
        return build(Thread.currentThread().getContextClassLoader());
    }

    public static <T> ProxyBuilder<T> newBuilder(Class<T> proxyClass) {
        return new ProxyBuilder<T>(proxyClass);
    }

}
