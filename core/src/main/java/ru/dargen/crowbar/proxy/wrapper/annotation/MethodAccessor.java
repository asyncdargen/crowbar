package ru.dargen.crowbar.proxy.wrapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodAccessor {

    String value() default "";

    boolean isStatic() default false;

    boolean inlinedOwner() default false;

    ProxiedClass owner() default @ProxiedClass;

    ProxiedClass returnType() default @ProxiedClass;

    ProxiedClass[] parameterTypes() default {};


}
