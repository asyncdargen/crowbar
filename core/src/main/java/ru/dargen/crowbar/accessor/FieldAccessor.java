package ru.dargen.crowbar.accessor;

import java.lang.reflect.Field;

public interface FieldAccessor<T> {

    Class<?> getDeclaringClass();

    Field getField();

    void setValue(Object object, T value);

    T getValue(Object object);

    default void setStaticValue(T value) {
        setValue(null, value);
    }

    default T getStaticValue() {
        return getValue(null);
    }

}
