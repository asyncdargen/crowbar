package ru.dargen.crowbar.reflect;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.crowbar.accessor.MethodAccessor;
import ru.dargen.crowbar.util.Reflection;

import java.lang.reflect.Method;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ReflectMethodAccessor<T> implements MethodAccessor<T> {

    private final Method method;

    @Override
    public Class<?> getDeclaringClass() {
        return method.getDeclaringClass();
    }

    @Override
    public T invoke(Object object, Object... args) {
        return Reflection.invokeMethod(method, object, args);
    }

    @Override
    public String toString() {
        return "ReflectMethodAccessor[%s]".formatted(method);
    }

}
