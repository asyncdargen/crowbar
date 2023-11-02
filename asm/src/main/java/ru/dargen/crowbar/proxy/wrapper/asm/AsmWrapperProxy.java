package ru.dargen.crowbar.proxy.wrapper.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import ru.dargen.crowbar.Accessors;
import ru.dargen.crowbar.accessor.ConstructorAccessor;
import ru.dargen.crowbar.asm.Asm;
import ru.dargen.crowbar.proxy.wrapper.WrapperProxy;
import ru.dargen.crowbar.proxy.wrapper.data.WrapperProxyData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.ConstructorAccessorData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.FieldAccessorData;
import ru.dargen.crowbar.proxy.wrapper.data.accessor.MethodAccessorData;
import ru.dargen.crowbar.util.Reflection;

import java.util.concurrent.ThreadLocalRandom;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;
import static ru.dargen.crowbar.asm.Asm.getTypes;
import static ru.dargen.crowbar.asm.Asm.makeReturn;

public class AsmWrapperProxy<T> extends WrapperProxy<T, WrapperProxy.WrappingData> {

    private final String proxyName;

    private ConstructorAccessor<T> proxyConstructor;
    private ClassWriter proxyBuilder;

    public AsmWrapperProxy(WrapperProxyData<T> data) {
        super(data);
        proxyName = proxyName(data.proxyClass());
    }

    @Override
    protected void appendFieldAccessor(Class<?> proxyClass, FieldAccessorData data, WrappingData proxyData) {
        var origin = data.getMethod();
        var ownerClass = data.getProxiedClass();

        Asm.putMethod(proxyBuilder, ACC_PUBLIC, origin.getName(), getMethodDescriptor(origin), method -> {
            if (data.getAccessorType() == FieldAccessorData.Type.GETTER) {
                if (data.isStatic()) {
                    method.visitFieldInsn(GETSTATIC, getInternalName(ownerClass), data.getMemberName(), getDescriptor(data.getFieldType()));
                } else {
                    if (data.isRequiredInlinedOwner()) {
                        getInlinedOwner(method, proxyName);
                    } else method.visitVarInsn(ALOAD, 1);

                    method.visitFieldInsn(data.isStatic() ? GETSTATIC : GETFIELD, getInternalName(ownerClass), data.getMemberName(), getDescriptor(data.getFieldType()));
                }

                makeReturn(method, origin.getReturnType(), data.getFieldType());
            } else {
                if (data.isStatic()) {
                    method.visitVarInsn(ALOAD, 1);
                    method.visitFieldInsn(GETSTATIC, getInternalName(ownerClass), data.getMemberName(), getDescriptor(data.getFieldType()));
                } else {
                    if (data.isRequiredInlinedOwner()) {
                        getInlinedOwner(method, proxyName);
                    } else method.visitVarInsn(ALOAD, 1);

                    method.visitVarInsn(ALOAD, 2);

                    method.visitFieldInsn(data.isStatic() ? PUTSTATIC : PUTFIELD, getInternalName(ownerClass), data.getMemberName(), getDescriptor(data.getFieldType()));
                }

                method.visitInsn(RETURN);
            }
        });
    }

    @Override
    protected void appendMethodAccessor(Class<?> proxyClass, MethodAccessorData data, WrappingData proxyData) {
        var origin = data.getMethod();
        var ownerClass = data.getProxiedClass();

        Asm.putMethod(proxyBuilder, ACC_PUBLIC, origin.getName(), getMethodDescriptor(origin), method -> {
            var descriptor = getMethodDescriptor(getType(data.getReturnType()), getTypes(data.getParametersTypes()));

            if (!data.isStatic()) {
                if (data.isRequiredInlinedOwner()) {
                    getInlinedOwner(method, proxyName);
                } else method.visitVarInsn(ALOAD, 1);
            }

            for (int i = 0; i < data.getParametersCount(); i++) {
                method.visitVarInsn(ALOAD, (data.isStatic() || data.isRequiredInlinedOwner() ? 1 : 2) + i);
            }

            method.visitMethodInsn(
                    data.isStatic() ? INVOKESTATIC : INVOKEVIRTUAL, getInternalName(ownerClass), data.getMemberName(),
                    descriptor, ownerClass.isInterface()
            );

            if (Reflection.unwrap(data.getReturnType()) == void.class) {
                method.visitInsn(POP);

                if (Reflection.unwrap(origin.getReturnType()) != void.class) {
                    method.visitInsn(ACONST_NULL);
                    method.visitInsn(ARETURN);
                } else method.visitInsn(RETURN);
            }

            makeReturn(method, origin.getReturnType(), data.getReturnType());
        });
    }

    @Override
    protected void appendConstructorAccessor(Class<?> proxyClass, ConstructorAccessorData data, WrappingData proxyData) {
        var origin = data.getMethod();
        var ownerClass = data.getProxiedClass();

        Asm.putMethod(proxyBuilder, ACC_PUBLIC, origin.getName(), getMethodDescriptor(origin), method -> {
            method.visitTypeInsn(NEW, getInternalName(ownerClass));
            method.visitInsn(DUP);

            for (int i = 0; i < data.getParametersCount(); i++) {
                method.visitVarInsn(ALOAD, i + 1);
            }

            method.visitMethodInsn(
                    INVOKESPECIAL, getInternalName(ownerClass), "<init>",
                    getMethodDescriptor(VOID_TYPE, getTypes(data.getParametersTypes())), false
            );

            makeReturn(method, origin.getReturnType(), ownerClass);
        });
    }

    @Override
    protected void prepare(WrappingData data) {
        if (proxyConstructor == null) {
            proxyBuilder = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
            proxyBuilder.visit(V17, ACC_SYNTHETIC | ACC_FINAL,
                    proxyName, null,
                    Asm.AccessorBridge.NAME, new String[]{getInternalName(this.data.proxyClass())});

            proxyBuilder.visitField(ACC_FINAL, "inlinedOwner", "Ljava/lang/Object;", null, null);
            Asm.putMethod(proxyBuilder, ACC_PRIVATE, "<init>", "(Ljava/lang/Object;)V", method -> {
                method.visitVarInsn(ALOAD, 0);
                method.visitVarInsn(ALOAD, 1);

                method.visitFieldInsn(PUTFIELD, proxyName, "inlinedOwner", "Ljava/lang/Object;");

                method.visitInsn(RETURN);
            });
            super.prepare(data);

            var proxyClass = Asm.defineClass(null, proxyBuilder);
            proxyConstructor = Accessors.invoke().openConstructor(proxyClass, Object.class);
            proxyBuilder = null;
        }
    }

    @Override
    protected WrappingData createWrappingData(Object inlinedObject) {
        return new WrappingData(inlinedObject);
    }

    @Override
    protected T wrap0(WrappingData data) {
        return proxyConstructor.newInstance(data.getInlinedObject());
    }

    private static void getInlinedOwner(MethodVisitor method, String proxyName) {
        method.visitVarInsn(ALOAD, 0);
        method.visitFieldInsn(GETFIELD, proxyName, "inlinedOwner", "Ljava/lang/Object;");
    }

    private static String proxyName(Class<?> proxyClass) {
        return "ru/dargen/crowbar/proxy/wrapper/asm/generated/Proxy$%s$%s".formatted(
                proxyClass.getSimpleName(), ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
    }

}
