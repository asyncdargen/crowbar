package ru.dargen.crowbar.proxy.wrapper.data;

import ru.dargen.crowbar.proxy.wrapper.data.accessor.AccessorData;

import java.util.List;

public record WrapperProxyData<T>(Class<T> proxyClass, List<AccessorData> accessorsList) {
}
