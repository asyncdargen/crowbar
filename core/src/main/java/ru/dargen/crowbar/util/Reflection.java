package ru.dargen.crowbar.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.*;
import java.util.Arrays;

@UtilityClass
@SuppressWarnings("unchecked")
public class Reflection {

    @SneakyThrows
    public Class<?> getClass(String className) {
        return Class.forName(className);
    }

    @SneakyThrows
    public Class<?> getClass(Object object) {
        return object == null ? Object.class : object.getClass();
    }

    @SneakyThrows
    public Class<?>[] getObjectArrayClasses(Object[] objects) {
        var argsTypes = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            argsTypes[i] = unwrap(objects[i].getClass());
        }

        return argsTypes;
    }


    @SneakyThrows
    public Field getField(Class<?> declaredClass, String name) {
        return declaredClass.getDeclaredField(name);
    }

    @SneakyThrows
    public <T> T getFieldValue(Field field, Object instance) {
        field.trySetAccessible();
        return (T) field.get(instance);
    }

    @SneakyThrows
    public <T> T getFieldValue(Object instance, String fieldName) {
        return getFieldValue(getField(instance, fieldName), instance);
    }

    @SneakyThrows
    public <T> T getFieldValue(Class<?> declaredClass, String fieldName, Object instance) {
        return getFieldValue(getField(declaredClass, fieldName), instance);
    }

    @SneakyThrows
    public <T> T getStaticFieldValue(Field field) {
        return getFieldValue(field, (Object) null);
    }

    @SneakyThrows
    public <T> T getStaticFieldValue(Class<?> declaredClass, String fieldName) {
        return getFieldValue(getField(declaredClass, fieldName), (Object) null);
    }

    @SneakyThrows
    public void setFieldValue(Field field, Object instance, Object value) {
        field.trySetAccessible();
        field.set(instance, value);
    }

    @SneakyThrows
    public void setFieldValue(Object instance, String fieldName, Object value) {
        setFieldValue(getField(instance, fieldName), instance, value);
    }

    @SneakyThrows
    public void setFieldValue(Class<?> declaredClass, String fieldName, Object instance, Object value) {
        setFieldValue(getField(declaredClass, fieldName), instance, value);
    }

    @SneakyThrows
    public void setStaticFieldValue(Field field, Object value) {
        setFieldValue(field, (Object) null, value);
    }

    @SneakyThrows
    public void setStaticFieldValue(Class<?> declaredClass, String fieldName, Object value) {
        setFieldValue(getField(declaredClass, fieldName), (Object) null, value);
    }

    @SneakyThrows
    public Field getField(Object object, String name) {
        return getField(object.getClass(), name);
    }

    @SneakyThrows
    public Field findField(Class<?> declaredClass, Class<?> fieldType, Type[] genericsTypes, int index) {
        int index0 = index;
        field:
        for (Field field : declaredClass.getDeclaredFields()) {
            if (!fieldType.isAssignableFrom(field.getType())) {
                continue;
            } else if (genericsTypes != null) {
                if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
                    var fieldTypeGenerics = parameterizedType.getActualTypeArguments();
                    for (int i = 0; i < fieldTypeGenerics.length; i++) {
                        if (i >= genericsTypes.length) {
                            continue field;
                        }

                        if (fieldTypeGenerics[i] != genericsTypes[i]) {
                            continue field;
                        }
                    }
                } else continue field;
            }

            if (index0-- == 0) {
                return field;
            }
        }

        throw new NoSuchFieldException("Field with type %s in %s at %s".formatted(
                fieldType.getName(), declaredClass.getName(), index));
    }

    @SneakyThrows
    public Field findField(Class<?> declaredClass, Class<?> fieldType, Type... genericsTypes) {
        return findField(declaredClass, fieldType, genericsTypes, 0);
    }

    @SneakyThrows
    public Field findField(Class<?> declaredClass, Class<?> fieldType, int index) {
        return findField(declaredClass, fieldType, null, index);
    }

    @SneakyThrows
    public Method getMethod(Class<?> declaredClass, String name, Class<?>... argsTypes) {
        return declaredClass.getDeclaredMethod(name, argsTypes);
    }

    @SneakyThrows
    public <T> T invokeMethod(Method method, Object instace, Object... args) {
        method.trySetAccessible();
        return (T) method.invoke(instace, args);
    }

    @SneakyThrows
    public <T> T invokeMethod(Object instace, String methodName, Object... args) {
        var method = findMethod(instace.getClass(), getObjectArrayClasses(args));

        return invokeMethod(method, instace, args);
    }

    @SneakyThrows
    public <T> T invokeStaticMethod(Method method, Object... args) {
        return invokeMethod(method, (Object) null, args);
    }

    @SneakyThrows
    public <T> T invokeStaticMethod(Class<?> declaredClass, String methodName, Object... args) {
        var method = findMethod(declaredClass, getObjectArrayClasses(args));

        return invokeStaticMethod(method, args);
    }

    @SneakyThrows
    public Method getMethod(Object object, String name, Class<?>... argsTypes) {
        return getMethod(object.getClass(), name, argsTypes);
    }

    @SneakyThrows
    public Method findMethod(Class<?> declaredClass, Class<?> returnType, Class<?>[] argsTypes, int index) {
        int index0 = index;
        method:
        for (Method method : declaredClass.getMethods()) {
            if (returnType != null && !returnType.isAssignableFrom(method.getReturnType())) {
                continue;
            } else if (argsTypes != null) {
                var parameterTypes = method.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (i >= argsTypes.length) {
                        continue method;
                    }

                    if (!argsTypes[i].isAssignableFrom(parameterTypes[i])) {
                        continue method;
                    }
                }
            } else continue;

            if (index0-- == 0) {
                return method;
            }
        }

        throw new NoSuchMethodException("Method with parameters (%s)%s in %s at %s".formatted(Arrays.toString(argsTypes), returnType, declaredClass.getName(), index));
    }

    @SneakyThrows
    public Method findMethod(Class<?> declaredClass, Class<?>[] argsTypes, int index) {
        return findMethod(declaredClass, null, argsTypes, index);
    }

    @SneakyThrows
    public Method findMethod(Class<?> declaredClass, Class<?>... argsTypes) {
        return findMethod(declaredClass, null, argsTypes, 0);
    }

    @SneakyThrows
    public Method findMethod(Class<?> declaredClass, Class<?> returnType, Class<?>... argsTypes) {
        return findMethod(declaredClass, returnType, argsTypes, 0);
    }

    @SneakyThrows
    public <T> Constructor<T> getConstructor(Class<T> declaredClass, Class<?>... argsTypes) {
        return declaredClass.getDeclaredConstructor(argsTypes);
    }

    @SneakyThrows
    public <T> T newInstance(Constructor<T> constructor, Object... args) {
        constructor.trySetAccessible();
        return constructor.newInstance(args);
    }

    @SneakyThrows
    public <T> T newInstance(Class<?> declaredClass, Object... args) {
        var constructor = getConstructor(declaredClass, getObjectArrayClasses(args));

        return (T) newInstance(constructor, args);
    }

    @SneakyThrows
    public Class<?> wrap(Class<?> primitiveClass) {
        if (primitiveClass == void.class) {
            return Void.class;
        } else if (primitiveClass == byte.class) {
            return Byte.class;
        } else if (primitiveClass == boolean.class) {
            return Boolean.class;
        } else if (primitiveClass == short.class) {
            return Short.class;
        } else if (primitiveClass == char.class) {
            return Character.class;
        } else if (primitiveClass == int.class) {
            return Integer.class;
        } else if (primitiveClass == float.class) {
            return Float.class;
        } else if (primitiveClass == long.class) {
            return Long.class;
        } else if (primitiveClass == double.class) {
            return Double.class;
        }

        return primitiveClass;
    }

    @SneakyThrows
    public Class<?> unwrap(Class<?> wrapperClass) {
        if (wrapperClass == Void.class) {
            return void.class;
        } else if (wrapperClass == Byte.class) {
            return byte.class;
        } else if (wrapperClass == Boolean.class) {
            return boolean.class;
        } else if (wrapperClass == Short.class) {
            return short.class;
        } else if (wrapperClass == Character.class) {
            return char.class;
        } else if (wrapperClass == Integer.class) {
            return int.class;
        } else if (wrapperClass == Float.class) {
            return float.class;
        } else if (wrapperClass == Long.class) {
            return long.class;
        } else if (wrapperClass == Double.class) {
            return double.class;
        }

        return wrapperClass;
    }

    @SneakyThrows
    public StackTraceElement[] currentStackTrace() {
        var stackTrace = Thread.currentThread().getStackTrace();
        return Arrays.copyOfRange(stackTrace, 2, stackTrace.length);
    }

    @SneakyThrows
    public Class<?> getCallerClass() {
        return getCallerClass(0);
    }

    @SneakyThrows
    public Class<?> getCallerClass(int offset) {
        return Reflection.getClass(currentStackTrace()[1 + offset].getClassName());
    }

}
