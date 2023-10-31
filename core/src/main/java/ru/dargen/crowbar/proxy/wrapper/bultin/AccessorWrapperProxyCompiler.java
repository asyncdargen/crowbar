package ru.dargen.crowbar.proxy.wrapper.bultin;

import lombok.Getter;
import lombok.experimental.Accessors;
import ru.dargen.crowbar.accessor.AccessorFactory;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.accessor.MethodAccessor;
import ru.dargen.crowbar.proxy.ProxyBuilder;
import ru.dargen.crowbar.proxy.wrapper.WrapperProxyCompiler;
import ru.dargen.crowbar.proxy.wrapper.data.WrapperProxyData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.ConstructorAccessorData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.FieldAccessorData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.MethodAccessorData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class AccessorWrapperProxyCompiler<T>
        extends WrapperProxyCompiler<T, AccessorWrapperProxyCompiler.CrowbarProxyCompileData<T>> {

    private final AccessorFactory accessorFactory;

    private final Map<FieldAccessorData, FieldAccessor<?>> fieldAccessorMap = new HashMap<>();
    private final Map<MethodAccessorData, MethodAccessor<?>> methodAccessorMap = new HashMap<>();
    private final Map<ConstructorAccessorData, ConstructorAccessor<?>> constructorAccessorMap = new HashMap<>();

    public AccessorWrapperProxyCompiler(WrapperProxyData<T> data, AccessorFactory accessorFactory) {
        super(data);
        this.accessorFactory = accessorFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void appendFieldAccessor(Class<?> proxyClass, FieldAccessorData data, CrowbarProxyCompileData<T> proxyData) {
        var accessor = (FieldAccessor<Object>) fieldAccessorMap.computeIfAbsent(data,
                d -> accessorFactory.openField(data.getProxiedClass(), data.getMemberName(), data.getFieldType()));

        proxyData.builder().bindMethod(data.getMethod(), (p, m, args) -> {
            var object = proxyData.getInlinedObject();

            if (data.isStatic()) {
                object = null;
            } else if (!data.isRequiredInlineOwner()) {
                object = args[0];
            }

            if (data.getAccessorType() == FieldAccessorData.Type.SETTER) {
                accessor.setValue(object, data.isStatic() || data.isRequiredInlineOwner() ? args[0] : args[args.length - 1]);
                return null;
            }

            return accessor.getValue(object);
        });
    }

    @Override
    protected void appendMethodAccessor(Class<?> proxyClass, MethodAccessorData data, CrowbarProxyCompileData<T> proxyData) {
        var accessor = methodAccessorMap.computeIfAbsent(data,
                d -> accessorFactory.openMethod(data.getProxiedClass(), data.getMemberName(),
                        data.getReturnType(), data.getParametersTypes()));

        proxyData.builder().bindMethod(data.getMethod(), (p, m, args) -> {
            var object = proxyData.getInlinedObject();

            if (data.isStatic()) {
                object = null;
            } else if (!data.isRequiredInlineOwner()) {
                object = args[0];
                args = Arrays.copyOfRange(args, 1, args.length - 1);
            }

            return accessor.invoke(object, args);
        });
    }

    @Override
    protected void appendConstructorAccessor(Class<?> proxyClass, ConstructorAccessorData data, CrowbarProxyCompileData<T> proxyData) {
        var accessor = constructorAccessorMap.computeIfAbsent(data,
                d -> accessorFactory.openConstructor(data.getProxiedClass(), data.getParameterTypes()));

        proxyData.builder().bindMethod(data.getMethod(), (p, m, args) -> accessor.newInstance(args));
    }

    @Override
    protected CrowbarProxyCompileData<T> createCompileData(Object inlinedObject) {
        return new CrowbarProxyCompileData<>(inlinedObject, ProxyBuilder.newBuilder(data.proxyClass()));
    }

    @Override
    protected T compile0(CrowbarProxyCompileData<T> data) {
        return data.builder().build();
    }

    @Getter
    @Accessors(fluent = true)
    public static class CrowbarProxyCompileData<T> extends ProxyCompileData {

        private final ProxyBuilder<T> builder;

        public CrowbarProxyCompileData(Object inlinedObject, ProxyBuilder<T> builder) {
            super(inlinedObject);
            this.builder = builder;
        }

    }

}
