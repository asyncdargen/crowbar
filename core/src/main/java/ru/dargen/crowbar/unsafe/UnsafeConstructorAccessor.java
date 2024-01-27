package ru.dargen.crowbar.unsafe;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.util.Unsafe;

import java.lang.reflect.Constructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class UnsafeConstructorAccessor<T> implements ConstructorAccessor<T> {

    private final Class<T> declaringClass;
    private Constructor<T> constructor;

    @Override
    public T newInstance(Object... args) {
        return Unsafe.allocateInstance(getDeclaringClass());
    }

    @Override
    public String toString() {
        return "UnsafeConstructorAccessor[%s]".formatted(constructor);
    }

}
