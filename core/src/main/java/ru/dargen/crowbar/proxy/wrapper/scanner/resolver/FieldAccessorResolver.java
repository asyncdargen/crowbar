package ru.dargen.crowbar.proxy.wrapper.scanner.resolver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.dargen.crowbar.proxy.wrapper.annotation.FieldAccessor;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.FieldAccessorData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.FieldAccessorData.Type;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.MemberAccessorData;
import ru.dargen.crowbar.proxy.wrapper.scanner.WrapperProxyScanner;
import ru.dargen.crowbar.util.Reflection;

import java.lang.reflect.Method;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldAccessorResolver implements AccessorResolver<FieldAccessor> {

    public static final FieldAccessorResolver INSTANCE = new FieldAccessorResolver();

    @Override
    public FieldAccessorData resolve(Class<?> proxiedClass, Method method, FieldAccessor annotation) {
        var accessorType = Reflection.unwrap(method.getReturnType()) == void.class && method.getParameterCount() >= 1
                ? Type.SETTER : Type.GETTER;

        var owningClass = WrapperProxyScanner.resolveClass(annotation.owner(), proxiedClass);
        var fieldType = WrapperProxyScanner.resolveClass(annotation.type(), () -> accessorType == Type.GETTER
                ? method.getReturnType()
                : method.getParameterTypes()[method.getParameterCount() - 1]);
        var fieldName = MemberAccessorData.getAccessingMemberName(annotation.value(), method);
        var isStatic = annotation.isStatic();
        var inlineOwner = annotation.inlinedOwner() && !isStatic;

        return new FieldAccessorData(
                owningClass, method, isStatic, fieldName, fieldType,
                accessorType, inlineOwner);
    }

}
