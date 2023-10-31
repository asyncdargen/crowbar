package ru.dargen.crowbar.proxy.wrapper.data.accessor;

import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class ConstructorAccessorData extends AccessorData {

    private final Class<?>[] parameterTypes;

    public ConstructorAccessorData(Class<?> proxiedClass, Method method, Class<?>[] parameterTypes) {
        super(proxiedClass, method);
        this.parameterTypes = parameterTypes;
    }

}
