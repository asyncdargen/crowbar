package ru.dargen.crowbar.reflect;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.util.Reflection;

import java.lang.reflect.Field;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ReflectFieldAccessor<T> implements FieldAccessor<T> {

    private final Field field;

    @Override
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    @Override
    public void setValue(Object object, T value) {
        Reflection.setFieldValue(field, object, value);
    }

    @Override
    public T getValue(Object object) {
        return Reflection.getFieldValue(field, object);
    }

    @Override
    public String toString() {
        return "ReflectFieldAccessor[%s]".formatted(field);
    }

}
