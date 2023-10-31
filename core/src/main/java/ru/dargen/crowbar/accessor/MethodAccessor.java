package ru.dargen.crowbar.accessor;

import java.lang.reflect.Method;

public interface MethodAccessor<T> {

    Class<?> getDeclaringClass();

    Method getMethod();

    T invoke(Object object, Object... args);

    default T invokeStatic(Object... args) {
        return invoke(null, args);
    }

}
