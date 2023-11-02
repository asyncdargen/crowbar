package ru.dargen.crowbar.proxy.wrapper.scanner;

import lombok.experimental.UtilityClass;
import ru.dargen.crowbar.proxy.wrapper.annotation.*;
import ru.dargen.crowbar.proxy.wrapper.data.WrapperProxyData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.AccessorData;
import ru.dargen.crowbar.proxy.wrapper.scanner.resolver.AccessorResolver;
import ru.dargen.crowbar.proxy.wrapper.scanner.resolver.ConstructorAccessorResolver;
import ru.dargen.crowbar.proxy.wrapper.scanner.resolver.FieldAccessorResolver;
import ru.dargen.crowbar.proxy.wrapper.scanner.resolver.MethodAccessorResolver;
import ru.dargen.crowbar.util.Reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@UtilityClass
public class WrapperProxyScanner {

    public final Map<Class<? extends Annotation>, AccessorResolver<?>> RESOLVER_MAP = Map.of(
            ConstructorAccessor.class, ConstructorAccessorResolver.INSTANCE,
            FieldAccessor.class, FieldAccessorResolver.INSTANCE,
            MethodAccessor.class, MethodAccessorResolver.INSTANCE
    );

    public <T> WrapperProxyData<T> scan(Class<T> proxyClass) {
        if (!proxyClass.isInterface()) {
            throw new IllegalArgumentException("Not interface");
        }

        var proxiedClass = (Class<?>) (proxyClass.isAnnotationPresent(ProxiedClass.class)
                ? resolveClass(proxyClass.getDeclaredAnnotation(ProxiedClass.class), Object.class)
                : Object.class);

        var accessorsData = Arrays.stream(proxyClass.getDeclaredMethods())
                .map(method -> resolveAccessorData(proxiedClass, method))
                .filter(Objects::nonNull)
                .toList();

        return new WrapperProxyData<>(proxyClass, accessorsData);
    }

    @SuppressWarnings("unchecked")
    private AccessorData resolveAccessorData(Class<?> proxiedClass, Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            return null;
        }

        var annotation = Arrays.stream(method.getDeclaredAnnotations())
                .map(Annotation::annotationType)
                .filter(RESOLVER_MAP::containsKey)
                .findFirst();
        var resolver = (AccessorResolver<Annotation>) annotation.map(RESOLVER_MAP::get).orElse(null);

        return resolver == null
                ? null
                : resolver.resolve(proxiedClass, method, method.getDeclaredAnnotation(annotation.get()));
    }

    public Class<?> resolveClass(ProxiedClass proxiedClass, Supplier<Class<?>> defaultSupplier) {
        if (!proxiedClass.className().isBlank()) {
            return Reflection.getClass(proxiedClass.className());
        }

        return proxiedClass.value() == void.class ? defaultSupplier.get() : proxiedClass.value();
    }

    public Class<?> resolveClass(ProxiedClass proxiedClass, Class<?> orDefault) {
        return resolveClass(proxiedClass, () -> orDefault);
    }

    public Class<?> resolveClass(ProxiedClass proxiedClass) {
        return resolveClass(proxiedClass, (Class<?>) null);
    }

}
