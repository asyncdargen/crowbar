package ru.dargen.crowbar.invoke;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.util.Reflection;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;

@Getter
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("unchecked")
public class InvokeConstructorAccessor<T> extends AbstractInvokeAccessor<T> implements ConstructorAccessor<T> {

    private Constructor<T> constructor;
    private final MethodHandle mh;

    public InvokeConstructorAccessor(Class<T> declaringClass, MethodHandle mh) {
        super(declaringClass, true, "<init>", mh.type());

        this.mh = mh;
    }

    public Constructor<T> getConstructor() {
        return constructor == null ? constructor = Reflection.getConstructor(declaringClass, type.parameterArray()) : constructor;
    }

    @Override
    @SneakyThrows
    public T newInstance(Object... args) {
        return (T) mh.invokeWithArguments(args);
    }

    @Override
    public String toString() {
        return "InvokeMethodAccessor[%s]".formatted(constructor);
    }

}
