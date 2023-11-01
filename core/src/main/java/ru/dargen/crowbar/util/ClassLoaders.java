package ru.dargen.crowbar.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassLoaders {

    public ClassLoader classLoader() {
        return of(ClassLoader.class);
    }

    public ClassLoader currentClass() {
        return of(Reflection.getCallerClass());
    }

    public ClassLoader of(Class<?> loadedClass) {
        return loadedClass.getClassLoader();
    }

    public ClassLoader currentThread() {
        return of(Thread.currentThread());
    }

    public ClassLoader of(Thread thread) {
        return thread.getContextClassLoader();
    }

    public ClassLoader system() {
        return ClassLoader.getSystemClassLoader();
    }

    public ClassLoader platform() {
        return ClassLoader.getPlatformClassLoader();
    }

}
