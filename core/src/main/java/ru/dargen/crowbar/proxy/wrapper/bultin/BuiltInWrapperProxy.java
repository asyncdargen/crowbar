package ru.dargen.crowbar.proxy.wrapper.bultin;

import lombok.Getter;
import lombok.experimental.Accessors;
import ru.dargen.crowbar.accessor.AccessorFactory;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.accessor.MethodAccessor;
import ru.dargen.crowbar.proxy.ProxyBuilder;
import ru.dargen.crowbar.proxy.wrapper.WrapperProxy;
import ru.dargen.crowbar.proxy.wrapper.data.WrapperProxyData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.ConstructorAccessorData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.FieldAccessorData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.MethodAccessorData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BuiltInWrapperProxy<T>
        extends WrapperProxy<T, BuiltInWrapperProxy.BuiltInWrappingData<T>> {

    private final AccessorFactory accessorFactory;

    private final Map<FieldAccessorData, FieldAccessor<?>> fieldAccessorMap = new HashMap<>();
    private final Map<MethodAccessorData, MethodAccessor<?>> methodAccessorMap = new HashMap<>();
    private final Map<ConstructorAccessorData, ConstructorAccessor<?>> constructorAccessorMap = new HashMap<>();

    public BuiltInWrapperProxy(WrapperProxyData<T> data, AccessorFactory accessorFactory) {
        super(data);
        this.accessorFactory = accessorFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void appendFieldAccessor(Class<?> proxyClass, FieldAccessorData data, BuiltInWrappingData<T> proxyData) {
        var accessor = (FieldAccessor<Object>) fieldAccessorMap.computeIfAbsent(data,
                d -> accessorFactory.openField(data.getProxiedClass(), data.getMemberName(), data.getFieldType()));

        proxyData.builder().bindMethod(data.getMethod(), (p, m, args) -> {
            var object = proxyData.getInlinedObject();

            if (data.isStatic()) {
                object = null;
            } else if (!data.isRequiredInlinedOwner()) {
                object = args[0];
            }

            if (data.getAccessorType() == FieldAccessorData.Type.SETTER) {
                accessor.setValue(object, data.isStatic() || data.isRequiredInlinedOwner() ? args[0] : args[args.length - 1]);
                return null;
            }

            return accessor.getValue(object);
        });
    }

    @Override
    protected void appendMethodAccessor(Class<?> proxyClass, MethodAccessorData data, BuiltInWrappingData<T> proxyData) {
        var accessor = methodAccessorMap.computeIfAbsent(data,
                d -> accessorFactory.openMethod(data.getProxiedClass(), data.getMemberName(),
                        data.getReturnType(), data.getParametersTypes()));

        proxyData.builder().bindMethod(data.getMethod(), (p, m, args) -> {
            var object = proxyData.getInlinedObject();

            if (data.isStatic()) {
                object = null;
            } else if (!data.isRequiredInlinedOwner()) {
                object = args[0];
                args = Arrays.copyOfRange(args, 1, args.length - 1);
            }

            return accessor.invoke(object, args);
        });
    }

    @Override
    protected void appendConstructorAccessor(Class<?> proxyClass, ConstructorAccessorData data, BuiltInWrappingData<T> proxyData) {
        var accessor = constructorAccessorMap.computeIfAbsent(data,
                d -> accessorFactory.openConstructor(data.getProxiedClass(), data.getParametersTypes()));

        proxyData.builder().bindMethod(data.getMethod(), (p, m, args) -> accessor.newInstance(args));
    }

    @Override
    protected BuiltInWrappingData<T> createCompileData(Object inlinedObject) {
        return new BuiltInWrappingData<>(inlinedObject, ProxyBuilder.newBuilder(data.proxyClass()));
    }

    @Override
    protected T wrap0(BuiltInWrappingData<T> data) {
        return data.builder().build();
    }

    @Getter
    @Accessors(fluent = true)
    public static class BuiltInWrappingData<T> extends WrappingData {

        private final ProxyBuilder<T> builder;

        public BuiltInWrappingData(Object inlinedObject, ProxyBuilder<T> builder) {
            super(inlinedObject);
            this.builder = builder;
        }

    }

}
