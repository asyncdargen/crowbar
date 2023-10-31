package ru.dargen.crowbar.accessor;

import ru.dargen.crowbar.util.Reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
public interface AccessorFactory {

    <T> FieldAccessor<T> openField(Field field);

    default <T> FieldAccessor<T> openField(Class<?> declaredClass, String fieldName) {
        return openField(Reflection.getField(declaredClass, fieldName));
    }

    default <T> FieldAccessor<T> openField(Class<?> declaredClass, String fieldName, Class<?> fieldType) {
        return openField(declaredClass, fieldName);
    }

    <T> MethodAccessor<T> openMethod(Method method);

    default <T> MethodAccessor<T> openMethod(Class<?> declaredClass, String methodName,
                                             Class<T> returnType, Class<?>... argsTypes) {
        return openMethod(Reflection.getMethod(declaredClass, methodName, argsTypes));
    }

    <T> ConstructorAccessor<T> openConstructor(Constructor<T> constructor);

    default <T> ConstructorAccessor<T> openConstructor(Class<?> declaredClass, Class<?>... argumentsTypes) {
        return openConstructor(Reflection.getConstructor((Class<T>) declaredClass, argumentsTypes));
    }

    default <T> ConstructorAccessor<T> openNoArgConstructor(Class<?> declaredClass) {
        return openConstructor(declaredClass);
    }


}
