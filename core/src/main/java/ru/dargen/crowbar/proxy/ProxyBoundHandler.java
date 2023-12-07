package ru.dargen.crowbar.proxy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.crowbar.Accessors;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ProxyBoundHandler implements InvocationHandler {

    private static final Object[] EMPTY_ARGS = new Object[0];

    private final Map<Method, ProxyMethodExecutor> executorMap;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args == null) {
            args = EMPTY_ARGS;
        }

        var executor = executorMap.get(method);

        if (executor == null) {
            executor = getDelegateExecutor(method);
        }

        return executor.execute(proxy, method, args);
    }

    private ProxyMethodExecutor getDelegateExecutor(Method method) {
        return executorMap.computeIfAbsent(method,
                m -> ProxyMethodExecutor.wrap(Accessors.invoke().openMethod(method, method.getDeclaringClass())));
    }

}
