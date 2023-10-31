package ru.dargen.crowbar.proxy.wrapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ProxiedClass {

    Class<?> value() default void.class;

    String className() default "";

}
