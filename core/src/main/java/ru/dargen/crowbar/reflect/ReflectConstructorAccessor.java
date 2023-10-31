package ru.dargen.crowbar.reflect;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.util.Reflection;

import java.lang.reflect.Constructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ReflectConstructorAccessor<T> implements ConstructorAccessor<T> {

    private final Constructor<T> constructor;

    @Override
    public Class<T> getDeclaringClass() {
        return constructor.getDeclaringClass();
    }

    @Override
    public T newInstance(Object... args) {
        return Reflection.newInstance(constructor, args);
    }

    @Override
    public String toString() {
        return "ReflectConstructorAccessor[%s]".formatted(constructor);
    }

}
