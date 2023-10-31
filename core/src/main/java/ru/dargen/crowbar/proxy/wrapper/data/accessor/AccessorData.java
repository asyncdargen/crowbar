package ru.dargen.crowbar.proxy.wrapper.data.accessor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

@Getter
@RequiredArgsConstructor
public abstract class AccessorData {

    protected final Class<?> proxiedClass;
    protected final Method method;

}
