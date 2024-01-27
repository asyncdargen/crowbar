package ru.dargen.crowbar.unsafe;

import ru.dargen.crowbar.accessor.AccessorFactory;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.accessor.MethodAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UnsafeAccessorFactory implements AccessorFactory {

    public static final UnsafeAccessorFactory INSTANCE = new UnsafeAccessorFactory();

    @Override
    public <T> FieldAccessor<T> openField(Field field) {
        return new UnsafeFieldAccessor<>(field);
    }

    @Override
    public <T> MethodAccessor<T> openMethod(Method method) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> ConstructorAccessor<T> openConstructor(Constructor<T> constructor) {
        return new UnsafeConstructorAccessor<>(constructor.getDeclaringClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ConstructorAccessor<T> openConstructor(Class<?> declaredClass, Class<?>... argumentsTypes) {
        return new UnsafeConstructorAccessor<>((Class<T>) declaredClass);
    }
}
