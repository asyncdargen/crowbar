package ru.dargen.crowbar.asm;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.util.Reflection;
import ru.dargen.crowbar.util.Unsafe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.objectweb.asm.Type.*;
import static ru.dargen.crowbar.asm.Asm.*;

@UtilityClass
@SuppressWarnings("unchecked")
public class AsmConstructorAccessor implements Asm {

    public final Map<String, ConstructorAccessor<?>> ACCESSOR_MAP = new ConcurrentHashMap<>();

    public <T> ConstructorAccessor<T> create(Class<?> ownerClass, Class<?>[] parameterTypes) {
        return (ConstructorAccessor<T>) ACCESSOR_MAP.computeIfAbsent(
                constructorKey(ownerClass, parameterTypes),
                key -> create0(ownerClass, parameterTypes));
    }

    @SneakyThrows
    private ConstructorAccessor<?> create0(Class<?> ownerClass, Class<?>[] parameterTypes) {
        var writer = newAccessorClass("Constructor", ownerClass, "init");

        putMethod(writer, ACC_PUBLIC, "newInstance", "([Ljava/lang/Object;)Ljava/lang/Object;", method -> {
            method.visitTypeInsn(NEW, getInternalName(ownerClass));
            method.visitInsn(DUP);

            for (int i = 0; i < parameterTypes.length; i++) {
                method.visitVarInsn(ALOAD, 1);
                method.visitLdcInsn(i);
                method.visitInsn(AALOAD);
            }

            method.visitMethodInsn(
                    INVOKESPECIAL, getInternalName(ownerClass), "<init>",
                    getMethodDescriptor(VOID_TYPE, getTypes(parameterTypes)), false
            );

            makeReturn(method, ownerClass, ownerClass);
        });

        putMethod(writer, ACC_PUBLIC, "getDeclaringClass", "()Ljava/lang/Class;", method -> {
            method.visitLdcInsn(getType(ownerClass));
            method.visitInsn(ARETURN);
        });

        putMethod(writer, ACC_PUBLIC, "getConstructor", "()Ljava/lang/reflect/Constructor;", method -> {
            method.visitLdcInsn(getType(ownerClass));

            method.visitLdcInsn(parameterTypes.length);
            method.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            method.visitVarInsn(ASTORE, 1);

            for (int i = 0; i < parameterTypes.length; i++) {
                method.visitVarInsn(ALOAD, 1);
                method.visitLdcInsn(i);

                var type = parameterTypes[i];
                if (type.isPrimitive()) {
                    type = Reflection.wrap(type);
                    method.visitFieldInsn(GETSTATIC, getInternalName(type), "TYPE", "Ljava/lang/Class;");
                } else method.visitLdcInsn(getType(parameterTypes[i]));

                method.visitInsn(AASTORE);
            }

            method.visitVarInsn(ALOAD, 1);

            method.visitMethodInsn(INVOKESTATIC, "ru/dargen/crowbar/util/Reflection", "getConstructor", "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Constructor;", false);
            method.visitInsn(ARETURN);
        });

        var accessorClass = (Class<ConstructorAccessor<?>>) Asm.defineClass(null, writer);
        return Unsafe.allocateInstance(accessorClass);
    }

    private String constructorKey(Class<?> ownerClass, Class<?>[] parameterTypes) {
        return "%s(%s)".formatted(ownerClass.getName(), stream(parameterTypes)
                .map(Class::getName)
                .collect(Collectors.joining(",")));
    }

}
