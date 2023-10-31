package ru.dargen.crowbar.proxy.wrapper.data.accessor;

import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class MethodAccessorData extends MemberAccessorData {

    private final Class<?> returnType;
    private final Class<?>[] parametersTypes;

    public MethodAccessorData(Class<?> proxiedClass, Method method,
                              boolean isStatic, String methodName, Class<?> returnType,
                              Class<?>[] parametersTypes, boolean requireInstance) {
        super(proxiedClass, method, isStatic, methodName, requireInstance);

        this.returnType = returnType;
        this.parametersTypes = parametersTypes;
    }

}
