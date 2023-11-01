package ru.dargen.crowbar.asm;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.objectweb.asm.Type;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.util.Unsafe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;
import static ru.dargen.crowbar.asm.Asm.*;

@UtilityClass
@SuppressWarnings("unchecked")
public class AsmFieldAccessor implements Asm {

    public final Map<String, FieldAccessor<?>> ACCESSOR_MAP = new ConcurrentHashMap<>();

    public <T> FieldAccessor<T> create(Class<?> ownerClass, boolean isStatic, String fieldName, Class<?> fieldType) {
        return (FieldAccessor<T>) ACCESSOR_MAP.computeIfAbsent(fieldKey(ownerClass, isStatic, fieldName, fieldType),
                key -> create0(ownerClass, isStatic, fieldName, fieldType));
    }

    @SneakyThrows
    private FieldAccessor<?> create0(Class<?> ownerClass, boolean isStatic, String fieldName, Class<?> fieldType) {
        var writer = newAccessorClass("Field", ownerClass, fieldName);

        putMethod(writer, ACC_PUBLIC, "setValue", "(Ljava/lang/Object;Ljava/lang/Object;)V", method -> {
            if (isStatic) {
                method.visitVarInsn(ALOAD, 2);
                method.visitFieldInsn(PUTSTATIC, getInternalName(ownerClass), fieldName, getDescriptor(fieldType));
            } else {
                method.visitVarInsn(ALOAD, 1);
                method.visitVarInsn(ALOAD, 2);
                method.visitFieldInsn(PUTFIELD, getInternalName(ownerClass), fieldName, getDescriptor(fieldType));
            }

            method.visitInsn(RETURN);
        });

        putMethod(writer, ACC_PUBLIC, "getValue", "(Ljava/lang/Object;)Ljava/lang/Object;", method -> {
            if (isStatic) {
                method.visitFieldInsn(GETSTATIC, getInternalName(ownerClass), fieldName, getDescriptor(fieldType));
            } else {
                method.visitVarInsn(ALOAD, 1);
                method.visitFieldInsn(GETFIELD, getInternalName(ownerClass), fieldName, getDescriptor(fieldType));
            }

            makeReturn(method, Object.class, fieldType);
        });

        putMethod(writer, ACC_PUBLIC, "getDeclaringClass", "()Ljava/lang/Class;", method -> {
            method.visitLdcInsn(Type.getType(ownerClass));
            method.visitInsn(ARETURN);
        });

        putMethod(writer, ACC_PUBLIC, "getField", "()Ljava/lang/reflect/Field;", method -> {
            method.visitLdcInsn(Type.getType(ownerClass));
            method.visitLdcInsn(fieldName);
            method.visitMethodInsn(INVOKESTATIC, "ru/dargen/crowbar/util/Reflection", "getField", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
            method.visitInsn(ARETURN);
        });

        var accessorClass = (Class<FieldAccessor<?>>) Asm.defineClass(null, writer);
        return Unsafe.allocateInstance(accessorClass);
    }

    private String fieldKey(Class<?> ownerClass, boolean isStatic, String fieldName, Class<?> fieldType) {
        return "%s%s %s.%s".formatted(isStatic ? "static " : "", fieldType.getName(), ownerClass.getName(), fieldName);
    }

}
