package ru.dargen.crowbar.proxy.wrapper.scanner.resolver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.dargen.crowbar.proxy.wrapper.annotation.MethodAccessor;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.MemberAccessorData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.MethodAccessorData;
import ru.dargen.crowbar.proxy.wrapper.scanner.WrapperProxyScanner;

import java.lang.reflect.Method;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodAccessorResolver implements AccessorResolver<MethodAccessor> {

    public static final MethodAccessorResolver INSTANCE = new MethodAccessorResolver();

    @Override
    public MemberAccessorData resolve(Class<?> proxiedClass, Method method, MethodAccessor annotation) {
        var owningClass = WrapperProxyScanner.resolveClass(annotation.owner(), proxiedClass);
        var methodName = MemberAccessorData.getAccessingMemberName(annotation.value(), method);
        var isStatic = annotation.isStatic();
        var inlineOwner = annotation.inlinedOwner() && !isStatic;

        var returnType = WrapperProxyScanner.resolveClass(annotation.returnType(), method.getReturnType());
        var parameterTypes = annotation.parameterTypes().length == 0
                ? getParameterTypes(method.getParameterTypes(), inlineOwner)
                : Arrays.stream(annotation.parameterTypes()).map(WrapperProxyScanner::resolveClass).toArray(Class[]::new);

        return new MethodAccessorData(
                owningClass, method, isStatic, methodName,
                returnType, parameterTypes, inlineOwner);
    }

    private Class<?>[] getParameterTypes(Class<?>[] parameterTypes, boolean inlineOwner) {
        if (parameterTypes.length == 0 || inlineOwner) {
            return parameterTypes;
        }

        return Arrays.copyOfRange(parameterTypes, 1, parameterTypes.length);
    }

}
