package ru.dargen.crowbar.asm;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dargen.crowbar.accessor.MethodAccessor;
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
public class AsmMethodAccessor implements Asm {

    public final Map<String, MethodAccessor<?>> ACCESSOR_MAP = new ConcurrentHashMap<>();

    public <T> MethodAccessor<T> create(Class<?> ownerClass, boolean isStatic, String methodName,
                                        Class<?> returnType, Class<?>[] parameterTypes) {
        return (MethodAccessor<T>) ACCESSOR_MAP.computeIfAbsent(
                methodKey(ownerClass, isStatic, methodName, returnType, parameterTypes),
                key -> create0(ownerClass, isStatic, methodName, returnType, parameterTypes));
    }

    @SneakyThrows
    private MethodAccessor<?> create0(Class<?> ownerClass, boolean isStatic, String methodName,
                                      Class<?> returnType, Class<?>[] parameterTypes) {
        var writer = newAccessorClass("Method", ownerClass, methodName);

        putMethod(writer, ACC_PUBLIC, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", method -> {
            var descriptor = getMethodDescriptor(getType(returnType), getTypes(parameterTypes));

            if (!isStatic) {
                method.visitVarInsn(ALOAD, 1);
            }

            for (int i = 0; i < parameterTypes.length; i++) {
                method.visitVarInsn(ALOAD, 2);
                method.visitLdcInsn(i);
                method.visitInsn(AALOAD);
            }

            method.visitMethodInsn(
                    isStatic ? INVOKESTATIC : INVOKEVIRTUAL, getInternalName(ownerClass), methodName,
                    descriptor, ownerClass.isInterface()
            );

            if (Reflection.unwrap(returnType) == void.class) {
                method.visitInsn(POP);
                method.visitInsn(ACONST_NULL);
                method.visitInsn(ARETURN);
            }

            makeReturn(method, Object.class, returnType);
        });

        putMethod(writer, ACC_PUBLIC, "getDeclaringClass", "()Ljava/lang/Class;", method -> {
            method.visitLdcInsn(getType(ownerClass));
            method.visitInsn(ARETURN);
        });

        putMethod(writer, ACC_PUBLIC, "getMethod", "()Ljava/lang/reflect/Method;", method -> {
            method.visitLdcInsn(getType(ownerClass));
            method.visitLdcInsn(methodName);

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

            method.visitMethodInsn(INVOKESTATIC, "ru/dargen/crowbar/util/Reflection", "getMethod", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            method.visitInsn(ARETURN);
        });

        var accessorClass = (Class<MethodAccessor<?>>) Asm.defineClass(null, writer);
        return Unsafe.allocateInstance(accessorClass);
    }

    private String methodKey(Class<?> ownerClass, boolean isStatic, String methodName,
                             Class<?> returnType, Class<?>[] parameterTypes) {
        return "%s%s %s.%s(%s)".formatted(isStatic ? "static " : "", returnType.getName(), ownerClass.getName(),
                methodName, stream(parameterTypes)
                        .map(Class::getName)
                        .collect(Collectors.joining(",")));
    }

}
