package ru.dargen.crowbar.invoke;

import ru.dargen.crowbar.accessor.AccessorFactory;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.accessor.MethodAccessor;
import ru.dargen.crowbar.util.MethodHandles;
import ru.dargen.crowbar.util.Reflection;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isStatic;

@SuppressWarnings("unchecked")
public class InvokeAccessorFactory implements AccessorFactory {

    public static final InvokeAccessorFactory INSTANCE = new InvokeAccessorFactory();

    public <T> FieldAccessor<T> newFieldAccessor(Class<?> declaredClass, boolean isStatic, String memberName,
                                                 MethodHandle getter, MethodHandle setter) {
        return new InvokeFieldAccessor<>(declaredClass, isStatic, memberName, getter, setter);
    }

    public <T> FieldAccessor<T> openField(Class<?> declaredClass, boolean isStatic,
                                          String fieldName, Class<?> fieldType) {
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
        return openField(field.getDeclaringClass(), isStatic(field.getModifiers()), field.getName(), field.getType());
    }

    @Override
    public <T> FieldAccessor<T> openField(Class<?> declaredClass, String fieldName, Class<?> fieldType) {
        return openField(declaredClass, false, fieldName, fieldType);
    }

    @Override
    public <T> FieldAccessor<T> openField(Class<?> declaredClass, String fieldName) {
        return openField(Reflection.getField(declaredClass, fieldName));
    }

    public <T> MethodAccessor<T> newMethodAccessor(Class<?> declaredClass, boolean isStatic,
                                                   String methodName, MethodHandle mh) {
        return new InvokeMethodAccessor<>(declaredClass, isStatic, methodName, mh);
    }

    public <T> MethodAccessor<T> openMethod(Class<?> declaredClass, boolean isStatic, String methodName,
                                            Class<?> returnType, Class<?>... argTypes) {
        return newMethodAccessor(declaredClass, isStatic, methodName,
                isStatic
                        ? MethodHandles.findStaticMethod(declaredClass, methodName, returnType, argTypes)
                        : MethodHandles.findMethod(declaredClass, methodName, returnType, argTypes)
        );
    }

    public <T> MethodAccessor<T> openMethod(Class<?> declaredClass, String methodName,
                                            Class<?> superClass, Class<?> returnType, Class<?>... argTypes) {
        return newMethodAccessor(declaredClass, false, methodName,
                MethodHandles.findSpecial(declaredClass, superClass, methodName, returnType, argTypes)
        );
    }

    @Override
    public <T> MethodAccessor<T> openMethod(Method method) {
        return openMethod(method.getDeclaringClass(), isStatic(method.getModifiers()),
                method.getName(), method.getReturnType(), method.getParameterTypes());
    }

    public <T> MethodAccessor<T> openMethod(Method method, Class<?> superClass) {
        return openMethod(method.getDeclaringClass(), method.getName(), superClass,
                method.getReturnType(), method.getParameterTypes());
    }

    @Override
    public <T> MethodAccessor<T> openMethod(Class<?> declaredClass, String methodName, Class<T> returnType, Class<?>... argsTypes) {
        return openMethod(declaredClass, false, methodName, returnType, argsTypes);
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
        return this.<T>newConstructorAccessor((Class<T>) declaredClass, MethodHandles.findConstructor(declaredClass, argumentsTypes));
    }
}
