package ru.dargen.crowbar.proxy.wrapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConstructorAccessor {

    ProxiedClass owner() default @ProxiedClass;

    ProxiedClass[] parameterTypes() default {};

}
