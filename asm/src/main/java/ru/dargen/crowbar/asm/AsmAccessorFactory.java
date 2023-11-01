package ru.dargen.crowbar.asm;

import ru.dargen.crowbar.accessor.AccessorFactory;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.accessor.MethodAccessor;
import ru.dargen.crowbar.util.Reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isStatic;

@SuppressWarnings("unchecked")
public class AsmAccessorFactory implements AccessorFactory {

    public static AsmAccessorFactory INSTANCE = new AsmAccessorFactory();

    @Override
    public <T> FieldAccessor<T> openField(Field field) {
        return openField(field.getDeclaringClass(), isStatic(field.getModifiers()), field.getName(), field.getType());
    }

    @Override
    public <T> FieldAccessor<T> openField(Class<?> declaredClass, String fieldName) {
        return openField(Reflection.getField(declaredClass, fieldName));
    }

    @Override
    public <T> FieldAccessor<T> openField(Class<?> declaredClass, String fieldName, Class<?> fieldType) {
        return AsmFieldAccessor.create(declaredClass, false, fieldName, fieldType);
    }

    public <T> FieldAccessor<T> openField(Class<?> declaredClass, boolean isStatic, String fieldName, Class<?> fieldType) {
        return AsmFieldAccessor.create(declaredClass, isStatic, fieldName, fieldType);
    }

    @Override
    public <T> MethodAccessor<T> openMethod(Method method) {
        return openMethod(method.getDeclaringClass(), isStatic(method.getModifiers()), method.getName(),
                (Class<T>) method.getReturnType(), method.getParameterTypes());
    }

    @Override
    public <T> MethodAccessor<T> openMethod(Class<?> declaredClass, String methodName, Class<T> returnType, Class<?>... argsTypes) {
        return openMethod(declaredClass, false, methodName, returnType, argsTypes);
    }

    public <T> MethodAccessor<T> openMethod(Class<?> declaredClass, boolean isStatic, String methodName,
                                            Class<T> returnType, Class<?>... argsTypes) {
        return AsmMethodAccessor.create(declaredClass, isStatic, methodName, returnType, argsTypes);
    }


    @Override
    public <T> ConstructorAccessor<T> openConstructor(Constructor<T> constructor) {
        return openConstructor(constructor.getDeclaringClass(), constructor.getParameterTypes());
    }

    @Override
    public <T> ConstructorAccessor<T> openConstructor(Class<?> declaredClass, Class<?>... argumentsTypes) {
        return AsmConstructorAccessor.create(declaredClass, argumentsTypes);
    }

}
