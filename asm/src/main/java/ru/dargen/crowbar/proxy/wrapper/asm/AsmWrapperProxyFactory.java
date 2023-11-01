package ru.dargen.crowbar.proxy.wrapper.asm;

import ru.dargen.crowbar.proxy.wrapper.WrapperProxy;
import ru.dargen.crowbar.proxy.wrapper.WrapperProxyFactory;
import ru.dargen.crowbar.proxy.wrapper.data.WrapperProxyData;

public class AsmWrapperProxyFactory implements WrapperProxyFactory {

    public static AsmWrapperProxyFactory INSTANCE = new AsmWrapperProxyFactory();

    @Override
    public <T> WrapperProxy<T, ?> create(WrapperProxyData<T> data) {
        return new AsmWrapperProxy<>(data);
    }

}
