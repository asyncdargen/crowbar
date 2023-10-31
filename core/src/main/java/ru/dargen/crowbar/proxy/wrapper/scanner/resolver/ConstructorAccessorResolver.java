package ru.dargen.crowbar.proxy.wrapper.scanner.resolver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.dargen.crowbar.proxy.wrapper.annotation.ConstructorAccessor;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.ConstructorAccessorData;
import ru.dargen.crowbar.proxy.wrapper.scanner.WrapperProxyScanner;

import java.lang.reflect.Method;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstructorAccessorResolver implements AccessorResolver<ConstructorAccessor> {

    public static final ConstructorAccessorResolver INSTANCE = new ConstructorAccessorResolver();

    @Override
    public ConstructorAccessorData resolve(Class<?> proxiedClass, Method method, ConstructorAccessor annotation) {
        var owningClass = WrapperProxyScanner.resolveClass(annotation.owner(), proxiedClass);
        var parameterTypes = annotation.parameterTypes().length == 0
                ? method.getParameterTypes()
                : Arrays.stream(annotation.parameterTypes()).map(WrapperProxyScanner::resolveClass).toArray(Class[]::new);

        return new ConstructorAccessorData(owningClass, method, parameterTypes);
    }

}
