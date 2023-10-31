package ru.dargen.crowbar.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isStatic;

@UtilityClass
public class MethodHandles {

    public final Lookup LOOKUP = findLookup();

    @SneakyThrows
    public MethodHandle findGetter(Class<?> declaredClass, String fieldName, Class<?> type) {
        return LOOKUP.findGetter(declaredClass, fieldName, type);
    }

    @SneakyThrows
    public MethodHandle findGetter(Object object, String fieldName, Class<?> type) {
        return findGetter(object.getClass(), fieldName, type);
    }

    @SneakyThrows
    public MethodHandle findStaticGetter(Class<?> declaredClass, String fieldName, Class<?> type) {
        return LOOKUP.findStaticGetter(declaredClass, fieldName, type);
    }

    @SneakyThrows
    public MethodHandle findStaticGetter(Object object, String fieldName, Class<?> type) {
        return findStaticGetter(object.getClass(), fieldName, type);
    }

    @SneakyThrows
    public MethodHandle unreflectGetter(Field field) {
        return isStatic(field.getModifiers())
                ? findStaticGetter(field.getDeclaringClass(), field.getName(), field.getType())
                : findGetter(field.getDeclaringClass(), field.getName(), field.getType());
    }

    @SneakyThrows
    public MethodHandle findSetter(Class<?> declaredClass, String fieldName, Class<?> type) {
        return LOOKUP.findSetter(declaredClass, fieldName, type);
    }

    @SneakyThrows
    public MethodHandle findSetter(Object object, String fieldName, Class<?> type) {
        return findSetter(object.getClass(), fieldName, type);
    }

    @SneakyThrows
    public MethodHandle findStaticSetter(Class<?> declaredClass, String fieldName, Class<?> type) {
        return LOOKUP.findStaticSetter(declaredClass, fieldName, type);
    }

    @SneakyThrows
    public MethodHandle findStaticSetter(Object object, String fieldName, Class<?> type) {
        return findStaticSetter(object.getClass(), fieldName, type);
    }

    @SneakyThrows
    public MethodHandle unreflectSetter(Field field) {
        return isStatic(field.getModifiers())
                ? findStaticSetter(field.getDeclaringClass(), field.getName(), field.getType())
                : findSetter(field.getDeclaringClass(), field.getName(), field.getType());
    }

    @SneakyThrows
    public MethodHandle findMethod(Class<?> declaredClass,
                                   String methodName, Class<?> returnType, Class<?>... argumentsTypes) {
        return LOOKUP.findVirtual(declaredClass, methodName, MethodType.methodType(returnType, argumentsTypes));
    }

    @SneakyThrows
    public MethodHandle findMethod(Object object,
                                   String methodName, Class<?> returnType, Class<?>... argumentsTypes) {
        return findMethod(object.getClass(), methodName, returnType, argumentsTypes);
    }

    @SneakyThrows
    public MethodHandle findStaticMethod(Class<?> declaredClass,
                                         String methodName, Class<?> returnType, Class<?>... argumentsTypes) {
        return LOOKUP.findStatic(declaredClass, methodName, MethodType.methodType(returnType, argumentsTypes));
    }

    @SneakyThrows
    public MethodHandle findStaticMethod(Object object,
                                         String methodName, Class<?> returnType, Class<?>... argumentsTypes) {
        return findStaticMethod(object.getClass(), methodName, returnType, argumentsTypes);
    }

    @SneakyThrows
    public MethodHandle unreflectMethod(Method method) {
        return isStatic(method.getModifiers())
                ? findStaticMethod(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes())
                : findMethod(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes());
    }

    @SneakyThrows
    public MethodHandle findConstructor(Class<?> declaredClass, Class<?>... argumentsTypes) {
        return LOOKUP.findConstructor(declaredClass, MethodType.methodType(void.class, argumentsTypes));
    }

    @SneakyThrows
    public MethodHandle findConstructor(Object object, Class<?>... argumentsTypes) {
        return findConstructor(object.getClass(), argumentsTypes);
    }

    @SneakyThrows
    public MethodHandle unreflectConstructor(Constructor<?> constructor) {
        return findConstructor(constructor.getDeclaringClass(), constructor.getParameterTypes());
    }

    @SneakyThrows //like declaredClassObject.super.methodName() (super <-> superClass)
    public MethodHandle findSpecial(Class<?> declaredClass, Class<?> superClass,
                                    String methodName, Class<?> returnType, Class<?>... argumentsTypes) {
        return LOOKUP.findSpecial(superClass, methodName, MethodType.methodType(returnType, argumentsTypes), declaredClass);
    }

    @SneakyThrows
    public MethodHandle unreflectSpecial(Method method, Class<?> childClass) {
        return findSpecial(childClass, method.getDeclaringClass(),
                method.getName(), method.getReturnType(), method.getParameterTypes());
    }

    @SneakyThrows
    public <T> T asProxy(Class<? extends T> invokerClass, MethodHandle methodHandle) {
        return MethodHandleProxies.asInterfaceInstance(invokerClass, methodHandle);
    }

    @SneakyThrows
    public Class<?> defineClass(byte[] bytes) {
        return LOOKUP.defineClass(bytes);
    }

    private Lookup findLookup() {
        var field = Reflection.getField(Lookup.class, "IMPL_LOOKUP");
        return (Lookup) Unsafe.getAccessor(Object.class)
                .get(Unsafe.UNSAFE.staticFieldBase(field), Unsafe.UNSAFE.staticFieldOffset(field));
    }

}
