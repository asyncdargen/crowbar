package ru.dargen.crowbar.invoke;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.invoke.MethodType;

@Getter
@RequiredArgsConstructor
public abstract class AbstractInvokeAccessor<T> {

    protected final Class<T> declaringClass;
    protected final boolean isStatic;
    protected final String memberName;
    protected final MethodType type;

}
