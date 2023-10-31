package ru.dargen.crowbar.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Map.entry;

@UtilityClass
@SuppressWarnings("unchecked")
public class Unsafe {

    public final sun.misc.Unsafe UNSAFE = Reflection.getStaticFieldValue(sun.misc.Unsafe.class, "theUnsafe");

    public <T> T getFieldValue(Field field, Object object) {
        var isStatic = Modifier.isStatic(field.getModifiers());
        var offset = isStatic ? staticFieldOffset(field) : objectFieldOffset(field);
        object = isStatic ? staticFieldBase(field) : object;

        return Unsafe.<T>getAccessor(field).get(object, offset);
    }

    public <T> T getStaticFieldValue(Field field) {
        return getFieldValue(field, (Object) null);
    }

    public <T> T getFieldValue(Class<?> declaredClass, String fieldName, Object object) {
        return getFieldValue(Reflection.getField(declaredClass, fieldName), object);
    }

    public <T> T getFieldValue(Object object, String fieldName) {
        return getFieldValue(object.getClass(), fieldName, object);
    }

    public <T> T getStaticFieldValue(Class<?> declaredClass, String fieldName) {
        return getStaticFieldValue(Reflection.getField(declaredClass, fieldName));
    }

    public <T> T getStaticFieldValue(Object object, String fieldName) {
        return getStaticFieldValue(object.getClass(), fieldName);
    }

    public void setFieldValue(Field field, Object object, Object value) {
        var isStatic = Modifier.isStatic(field.getModifiers());
        var offset = isStatic ? staticFieldOffset(field) : objectFieldOffset(field);
        object = isStatic ? staticFieldBase(field) : object;

        getAccessor(field).set(object, offset, value);
    }

    public void setStaticFieldValue(Field field, Object value) {
        setFieldValue(field, (Object) null, value);
    }

    public void setFieldValue(Class<?> declaredClass, String fieldName, Object object, Object value) {
        setFieldValue(Reflection.getField(declaredClass, fieldName), object, value);
    }

    public void setFieldValue(Object object, String fieldName, Object value) {
        setFieldValue(object.getClass(), fieldName, object, value);
    }

    public void setStaticFieldValue(Class<?> declaredClass, String fieldName, Object value) {
        setStaticFieldValue(Reflection.getField(declaredClass, fieldName), value);
    }

    public void setStaticFieldValue(Object object, String fieldName, Object value) {
        setStaticFieldValue(object.getClass(), fieldName, value);
    }

    @SneakyThrows
    public <T> T allocateInstance(Class<T> declaredClass) {
        return (T) UNSAFE.allocateInstance(declaredClass);
    }

    public void park(long time, TimeUnit unit) {
        UNSAFE.park(false, unit.toNanos(time));
    }

    public void parkAbsolute(long timestamp) {
        UNSAFE.park(true, timestamp);
    }

    public void unpark(Thread thread) {
        UNSAFE.unpark(thread);
    }

    public void freeBuffer(ByteBuffer buffer) {
        if (buffer.isDirect()) {
            UNSAFE.invokeCleaner(buffer);
        }
    }

    public void sneakyThrow(Throwable throwable) {
        UNSAFE.throwException(throwable);
    }

    @SneakyThrows
    public long objectFieldOffset(Field field) {
        return (long) JDK.MH_UNSAFE_FIELD_OFFSET.invoke(field);
    }

    @SneakyThrows
    public long staticFieldOffset(Field field) {
        return (long) JDK.MH_UNSAFE_STATIC_FIELD_OFFSET.invoke(field);
    }

    @SneakyThrows
    public Object staticFieldBase(Field field) {
        return JDK.MH_UNSAFE_STATIC_FIELD_BASE.invoke(field);
    }

    @SneakyThrows
    public Class<?> defineClass(String name,
                                byte[] bytes, int index, int length,
                                ClassLoader classLoader, ProtectionDomain protectionDomain) {
        return (Class<?>) JDK.MH_UNSAFE_CLASS_DEFINE.invoke(name, bytes, index, length, classLoader, protectionDomain);
    }

    public Class<?> defineClass(String name, byte[] bytes, ClassLoader classLoader) {
        return defineClass(name, bytes, 0, bytes.length, classLoader, null);
    }

    public Class<?> defineClass(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length, null, null);
    }

    //for bypass record && hidden member checks
    @UtilityClass
    public class JDK {

        public final Object JDK_UNSAFE = Reflection.getFieldValue(UNSAFE, "theInternalUnsafe");

        public final MethodHandle MH_UNSAFE_FIELD_OFFSET = MethodHandles.findMethod(JDK_UNSAFE,
                        "objectFieldOffset", long.class, Field.class)
                .bindTo(JDK_UNSAFE);
        public final MethodHandle MH_UNSAFE_STATIC_FIELD_OFFSET = MethodHandles.findMethod(JDK_UNSAFE,
                        "staticFieldOffset", long.class, Field.class)
                .bindTo(JDK_UNSAFE);
        public final MethodHandle MH_UNSAFE_STATIC_FIELD_BASE = MethodHandles.findMethod(JDK_UNSAFE,
                        "staticFieldBase", Object.class, Field.class)
                .bindTo(JDK_UNSAFE);
        public final MethodHandle MH_UNSAFE_CLASS_DEFINE = MethodHandles.findMethod(JDK_UNSAFE,
                        "defineClass0", Class.class,
                        String.class, byte[].class, int.class, int.class,
                        ClassLoader.class, ProtectionDomain.class)
                .bindTo(JDK_UNSAFE);

    }

    public final Accessor<Byte> BYTE_ACCESSOR = Accessor.of(UNSAFE::putByte, UNSAFE::getByte);
    public final Accessor<Boolean> BOOLEAN_ACCESSOR = Accessor.of(UNSAFE::putBoolean, UNSAFE::getBoolean);
    public final Accessor<Short> SHORT_ACCESSOR = Accessor.of(UNSAFE::putShort, UNSAFE::getShort);
    public final Accessor<Character> CHAR_ACCESSOR = Accessor.of(UNSAFE::putChar, UNSAFE::getChar);
    public final Accessor<Integer> INT_ACCESSOR = Accessor.of(UNSAFE::putInt, UNSAFE::getInt);
    public final Accessor<Float> FLOAT_ACCESSOR = Accessor.of(UNSAFE::putFloat, UNSAFE::getFloat);
    public final Accessor<Long> LONG_ACCESSOR = Accessor.of(UNSAFE::putLong, UNSAFE::getLong);
    public final Accessor<Double> DOUBLE_ACCESSOR = Accessor.of(UNSAFE::putDouble, UNSAFE::getDouble);
    public final Accessor<Object> OBJECT_ACCESSOR = Accessor.of(UNSAFE::putObject, UNSAFE::getObject);

    public final Map<Class<?>, Accessor<?>> ACCESSOR_MAP = Map.ofEntries(
            entry(byte.class, BYTE_ACCESSOR),
            entry(boolean.class, BOOLEAN_ACCESSOR),
            entry(short.class, SHORT_ACCESSOR),
            entry(char.class, CHAR_ACCESSOR),
            entry(int.class, INT_ACCESSOR),
            entry(float.class, FLOAT_ACCESSOR),
            entry(long.class, LONG_ACCESSOR),
            entry(double.class, DOUBLE_ACCESSOR),

            entry(Object.class, OBJECT_ACCESSOR)
    );

    public <T> Accessor<T> getAccessor(Class<T> declaredClass) {
        var accessor = ACCESSOR_MAP.get(declaredClass);
        return (Accessor<T>) (accessor == null ? OBJECT_ACCESSOR : accessor);
    }

    public <T> Accessor<T> getAccessor(Field field) {
        return (Accessor<T>) getAccessor(field.getType());
    }

    public record Accessor<T>(Setter<T> setter, Getter<T> getter) {


        public void set(Object base, long offset, T value) {
            setter.set(base, offset, value);
        }

        public T get(Object base, long offset) {
            return getter.get(base, offset);
        }

        static <T> Accessor<T> of(Setter<T> setter, Getter<T> getter) {
            return new Accessor<>(setter, getter);
        }

        @FunctionalInterface
        public interface Setter<T> {

            void set(Object base, long offset, T value);


        }

        @FunctionalInterface
        public interface Getter<T> {

            T get(Object base, long offset);


        }

    }

}
