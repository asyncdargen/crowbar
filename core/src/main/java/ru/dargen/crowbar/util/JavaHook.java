package ru.dargen.crowbar.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.invoke.MethodHandle;

@UtilityClass
public class JavaHook {

    public final MethodHandle MH_OPEN_MODULE = MethodHandles.findMethod(Module.class,
            "implAddOpens", void.class, String.class);

    @SneakyThrows
    public void openModules(Module module, String... modules) {
        for (String moduleName : modules) {
            MH_OPEN_MODULE.invoke(module, moduleName);
        }
    }

    public void openModules(Class<?> classInModule, String... modules) {
        openModules(Reflection.<Class<?>>getFieldValue(classInModule, "module"), modules);
    }

}
