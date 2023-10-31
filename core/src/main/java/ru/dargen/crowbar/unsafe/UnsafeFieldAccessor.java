package ru.dargen.crowbar.unsafe;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.util.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Getter
@EqualsAndHashCode
public class UnsafeFieldAccessor<T> implements FieldAccessor<T> {

    private final Field field;
    private final Unsafe.Accessor<T> unsafeAccessor;

    private final boolean isStatic;
    private final Object base;
    private final long offset;

    public UnsafeFieldAccessor(Field field) {
        this.field = field;
        this.unsafeAccessor = Unsafe.getAccessor(field);

        if (isStatic = Modifier.isStatic(field.getModifiers())) {
            base = Unsafe.staticFieldBase(field);
            offset = Unsafe.staticFieldOffset(field);
        } else {
            base = null;
            offset = Unsafe.objectFieldOffset(field);
        }
    }

    @Override
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    @Override
    public void setValue(Object object, T value) {
        unsafeAccessor.set(isStatic ? base : object, offset, value);
    }

    @Override
    public T getValue(Object object) {
        return unsafeAccessor.get(isStatic ? base : object, offset);
    }

    @Override
    public String toString() {
        return "UnsafeFieldAccessor[%s]".formatted(field);
    }

}
