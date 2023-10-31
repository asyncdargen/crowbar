package ru.dargen.crowbar.invoke;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.dargen.crowbar.accessor.MethodAccessor;
import ru.dargen.crowbar.util.Reflection;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

@Getter
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("unchecked")
public class InvokeMethodAccessor<T> extends AbstractInvokeAccessor<Object> implements MethodAccessor<T> {

    private Method method;
    private final MethodHandle mh;

    public InvokeMethodAccessor(Class<?> declaringClass, boolean isStatic, String memberName,
                                MethodHandle mh) {
        super((Class<Object>) declaringClass, isStatic, memberName, mh.type());

        this.mh = mh;
    }

    public Method getMethod() {
        return method == null ? method = Reflection.getMethod(declaringClass, memberName, type.parameterArray()) : method;
    }

    @Override
    @SneakyThrows
    public T invoke(Object object, Object... args) {
        return (T) (isStatic ? mh.invokeWithArguments(args) : mh.invokeWithArguments(composeArray(object, args)));
    }

    @Override
    public String toString() {
        return "InvokeMethodAccessor[%s]".formatted(method);
    }

    private static Object[] composeArray(Object instance, Object... args) {
        var newArgs = new Object[args.length + 1];
        newArgs[0] = instance;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return newArgs;
    }

}
