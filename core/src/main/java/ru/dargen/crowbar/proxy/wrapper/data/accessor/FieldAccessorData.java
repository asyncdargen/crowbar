package ru.dargen.crowbar.proxy.wrapper.data.accessor;

import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class FieldAccessorData extends MemberAccessorData {

    private final Class<?> fieldType;
    private final Type accessorType;

    public FieldAccessorData(Class<?> proxiedClass, Method method,
                             boolean isStatic, String fieldName, Class<?> fieldType,
                             Type type, boolean requireInstance) {
        super(proxiedClass, method, isStatic, fieldName, requireInstance);

        this.fieldType = fieldType;
        this.accessorType = type;
    }

    public enum Type {

        GETTER, SETTER;

    }

}
