package ru.dargen.crowbar.accessor;

import java.lang.reflect.Constructor;

public interface ConstructorAccessor<T> {

    Class<? extends T> getDeclaringClass();

    Constructor<T> getConstructor();

    T newInstance(Object... args);

}
