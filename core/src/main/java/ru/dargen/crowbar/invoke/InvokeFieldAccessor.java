package ru.dargen.crowbar.invoke;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.util.Reflection;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

@Getter
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("unchecked")
public class InvokeFieldAccessor<T> extends AbstractInvokeAccessor<Object> implements FieldAccessor<T> {

    private Field field;
    private final MethodHandle getter, setter;

    public InvokeFieldAccessor(Class<?> declaringClass, boolean isStatic, String memberName,
                               MethodHandle getter, MethodHandle setter) {
        super((Class<Object>) declaringClass, isStatic, memberName, getter.type());

        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Field getField() {
        return field == null ? field = Reflection.getField(declaringClass, memberName) : field;
    }

    @Override
    @SneakyThrows
    public void setValue(Object object, T value) {
        if (isStatic) setter.invoke(value);
        else setter.invoke(object, value);
    }

    @Override
    @SneakyThrows
    public T getValue(Object object) {
        return (T) (isStatic ? getter.invoke() : getter.invoke(object));
    }

    @Override
    public String toString() {
        return "InvokeFieldAccessor[%s]".formatted(field);
    }

}
