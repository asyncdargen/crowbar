package ru.dargen.crowbar.proxy.wrapper.scanner.resolver;

import ru.dargen.crowbar.proxy.wrapper.data.accessor.AccessorData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface AccessorResolver<A extends Annotation> {

    AccessorData resolve(Class<?> proxiedClass, Method method, A annotation);

}
