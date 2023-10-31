package ru.dargen.crowbar.reflect;

import ru.dargen.crowbar.accessor.AccessorFactory;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.accessor.MethodAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectAccessorFactory implements AccessorFactory {

    public static ReflectAccessorFactory INSTANCE = new ReflectAccessorFactory();

    @Override
    public <T> FieldAccessor<T> openField(Field field) {
        return new ReflectFieldAccessor<>(field);
    }

    @Override
    public <T> MethodAccessor<T> openMethod(Method method) {
        return new ReflectMethodAccessor<>(method);
    }

    @Override
    public <T> ConstructorAccessor<T> openConstructor(Constructor<T> constructor) {
        return new ReflectConstructorAccessor<>(constructor);
    }

}
