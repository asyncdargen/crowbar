package ru.dargen.crowbar.proxy.wrapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.crowbar.proxy.wrapper.data.WrapperProxyData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.ConstructorAccessorData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.FieldAccessorData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.MethodAccessorData;

@Getter
@RequiredArgsConstructor
public abstract class WrapperProxy<T, D extends WrapperProxy.WrappingData> {

    protected final WrapperProxyData<T> data;

    protected abstract void appendFieldAccessor(Class<?> proxyClass, FieldAccessorData data, D proxyData);

    protected abstract void appendMethodAccessor(Class<?> proxyClass, MethodAccessorData data, D proxyData);

    protected abstract void appendConstructorAccessor(Class<?> proxyClass, ConstructorAccessorData data, D proxyData);

    protected abstract D createWrappingData(Object inlinedObject);

    protected void prepare(D data) {
        this.data.accessorsList().forEach(accessorData -> {
            if (accessorData instanceof FieldAccessorData fieldAccessorData) {
                appendFieldAccessor(this.data.proxyClass(), fieldAccessorData, data);
            } else if (accessorData instanceof MethodAccessorData methodAccessorData) {
                appendMethodAccessor(this.data.proxyClass(), methodAccessorData, data);
            } else if (accessorData instanceof ConstructorAccessorData constructorAccessorData) {
                appendConstructorAccessor(this.data.proxyClass(), constructorAccessorData, data);
            }
        });
    }

    protected abstract T wrap0(D data);

    public T wrap(Object inlinedObject) {
        var data = createWrappingData(inlinedObject);
        prepare(data);
        return wrap0(data);
    }

    public T wrap() {
        return wrap(null);
    }

    @Getter
    @RequiredArgsConstructor
    public static class WrappingData {

        protected final Object inlinedObject;

        public boolean inlineObject() {
            return inlinedObject != null;
        }

    }

}
