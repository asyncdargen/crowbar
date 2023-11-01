package ru.dargen.crowbar.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dargen.crowbar.Accessors;
import ru.dargen.crowbar.accessor.FieldAccessor;
import ru.dargen.crowbar.accessor.MethodAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@UtilityClass
public class JavaHooks {

    public final MethodAccessor<Void> OPEN_MODULE_ACCESSOR = Accessors.invoke()
            .openMethod(Module.class, "implAddOpens", void.class, String.class);
    public final FieldAccessor<Integer> FIELD_MODIFIERS_ACCESSOR = Accessors.invoke()
            .openField(Field.class, "modifiers", int.class);

    static {
        openModules(JavaHooks.class.getModule(), "java.lang.invoke", "jdk.internal.misc");
    }

    public void init() {

    }

    @SneakyThrows
    public void openModules(Module module, String... modules) {
        for (String moduleName : modules) {
            OPEN_MODULE_ACCESSOR.invoke(module, moduleName);
        }
    }

    public void openModules(Class<?> classInModule, String... modules) {
        openModules(classInModule.getModule(), modules);
    }

    public void openField(Field field) {
        FIELD_MODIFIERS_ACCESSOR.setValue(field, Modifier.PUBLIC);
    }

}
