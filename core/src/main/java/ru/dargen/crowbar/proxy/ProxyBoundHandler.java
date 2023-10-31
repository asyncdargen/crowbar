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

    private final Map<Method, ProxyMethodExecutor> executorMap;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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
