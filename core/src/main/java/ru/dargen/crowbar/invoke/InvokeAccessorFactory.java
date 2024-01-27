package ru.dargen.crowbar.invoke;

import ru.dargen.crowbar.accessor.AccessorFactory;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.accessor.MethodAccessor;
import ru.dargen.crowbar.util.MethodHandles;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isStatic;

@SuppressWarnings("unchecked")
public class InvokeAccessorFactory implements AccessorFactory {

    public static final InvokeAccessorFactory INSTANCE = new InvokeAccessorFactory();

    public <T> FieldAccessor<T> newFieldAccessor(Class<?> declaredClass,
                                                 boolean isStatic, String memberName,
                                                 MethodHandle getter, MethodHandle setter) {
        return new InvokeFieldAccessor<>(declaredClass, isStatic, memberName, getter, setter);
    }

    @Override
    public <T> FieldAccessor<T> openField(Class<?> declaredClass, String fieldName,
                                          boolean isStatic, Class<?> fieldType) {
        return newFieldAccessor(declaredClass, isStatic, fieldName,
                isStatic
                        ? MethodHandles.findStaticGetter(declaredClass, fieldName, fieldType)
                        : MethodHandles.findGetter(declaredClass, fieldName, fieldType),
                isStatic
                        ? MethodHandles.findStaticSetter(declaredClass, fieldName, fieldType)
                        : MethodHandles.findSetter(declaredClass, fieldName, fieldType));
    }

    @Override
    public <T> FieldAccessor<T> openField(Field field) {
        return openField(field.getDeclaringClass(), field.getName(), isStatic(field.getModifiers()), field.getType());
    }

    public <T> MethodAccessor<T> newMethodAccessor(Class<?> declaredClass, boolean isStatic,
                                                   String methodName, MethodHandle mh) {
        return new InvokeMethodAccessor<>(declaredClass, isStatic, methodName, mh);
    }

    @Override
    public <T> MethodAccessor<T> openMethod(Class<?> declaredClass, String methodName, boolean isStatic,
                                            Class<? extends T> returnType, Class<?>... argTypes) {
        return newMethodAccessor(declaredClass, isStatic, methodName,
                isStatic
                        ? MethodHandles.findStaticMethod(declaredClass, methodName, returnType, argTypes)
                        : MethodHandles.findMethod(declaredClass, methodName, returnType, argTypes));
    }

    @Override
    public <T> MethodAccessor<T> openMethod(Method method) {
        return openMethod(method.getDeclaringClass(), method.getName(), isStatic(method.getModifiers()),
                (Class<? extends T>) method.getReturnType(), method.getParameterTypes());
    }

    public <T> MethodAccessor<T> openMethod(Class<?> declaredClass, String methodName,
                                            Class<?> superClass, Class<?> returnType, Class<?>... argTypes) {
        return newMethodAccessor(declaredClass, false, methodName,
                MethodHandles.findSpecial(declaredClass, superClass, methodName, returnType, argTypes));
    }

    public <T> MethodAccessor<T> openMethod(Method method, Class<?> superClass) {
        return openMethod(method.getDeclaringClass(), method.getName(), superClass,
                method.getReturnType(), method.getParameterTypes());
    }

    public <T> ConstructorAccessor<T> newConstructorAccessor(Class<T> declaredClass, MethodHandle mh) {
        return new InvokeConstructorAccessor<>(declaredClass, mh);
    }

    @Override
    public <T> ConstructorAccessor<T> openConstructor(Constructor<T> constructor) {
        return newConstructorAccessor(constructor.getDeclaringClass(), MethodHandles.unreflectConstructor(constructor));
    }

    @Override
    public <T> ConstructorAccessor<T> openConstructor(Class<?> declaredClass, Class<?>... argumentsTypes) {
        return newConstructorAccessor((Class<T>) declaredClass, MethodHandles.findConstructor(declaredClass, argumentsTypes));
    }
}
