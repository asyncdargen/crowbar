package ru.dargen.crowbar.asm;

import lombok.experimental.UtilityClass;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import ru.dargen.crowbar.util.ClassLoaders;
import ru.dargen.crowbar.util.Reflection;
import ru.dargen.crowbar.util.Unsafe;

import java.util.Arrays;
import java.util.function.Consumer;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Type.*;

public interface Asm extends Opcodes {

    String ACCESSORS_PACKAGE = "ru/dargen/crowbar/asm/generated/";

    static ClassWriter newAccessorClass(String type, Class<?> ownerType, String memberName) {
        var writer = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
        writer.visit(V17, ACC_FINAL | ACC_SYNTHETIC,
                ACCESSORS_PACKAGE + accessorName(type, ownerType, memberName),
                null, AccessorBridge.NAME,
                new String[]{"ru/dargen/crowbar/accessor/%sAccessor".formatted(type)});
        return writer;
    }

    static String accessorName(String type, Class<?> ownerClass, String fieldName) {
        return "Asm%sAccessor$%s$%s$%s"
                .formatted(type, ownerClass.getSimpleName(), fieldName, current().nextInt(Integer.MAX_VALUE));
    }

    static Class<?> defineClass(String name, ClassWriter writer, ClassLoader classLoader) {
        return Unsafe.defineClass(name, writer.toByteArray(), classLoader);
    }

    static Class<?> defineClass(String name, ClassWriter writer) {
        return defineClass(name, writer, ClassLoaders.classLoader());
    }

    static void putMethod(ClassWriter writer,
                          int modifiers, String name, String descriptor,
                          Consumer<MethodVisitor> code) {
        var method = writer.visitMethod(modifiers, name, descriptor, null, null);
        method.visitCode();
        code.accept(method);
        method.visitEnd();
        method.visitMaxs(0, 0);
    }

    static Type[] getTypes(Class<?>... classes) {
        return Arrays.stream(classes).map(Type::getType).toArray(Type[]::new);
    }

    static void makeReturn(MethodVisitor method, Class<?> logicalReturnType, Class<?> returnType) {
        if (returnType.isPrimitive() && !logicalReturnType.isPrimitive()) {
            var wrapper = Reflection.wrap(returnType);
            method.visitMethodInsn(INVOKESTATIC, getInternalName(wrapper), "valueOf", getMethodDescriptor(getType(wrapper), getType(returnType)), false);
        }

        method.visitInsn(Type.getType(logicalReturnType).getOpcode(IRETURN));
    }

    @UtilityClass
    class AccessorBridge implements Opcodes {

        public String NAME = "ru/dargen/crowbar/asm/MagicAccessorBridge";
        public String MAGIC_ACCESSOR = "jdk/internal/reflect/MagicAccessorImpl";

        static {
            var writer = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
            writer.visit(V17, ACC_SYNTHETIC, NAME, null, MAGIC_ACCESSOR, new String[0]);
            defineClass(NAME, writer, null);
        }

    }

}
