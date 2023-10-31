package ru.dargen.crowbar.proxy.wrapper.data.accessor;

import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class MemberAccessorData extends AccessorData {

    protected final boolean isStatic;
    protected final String memberName;
    protected final boolean requiredInlineOwner;

    public MemberAccessorData(Class<?> proxiedClass, Method method,
                              boolean isStatic, String memberName,
                              boolean requiredWrapInstance) {
        super(proxiedClass, method);

        this.isStatic = isStatic;
        this.memberName = memberName;
        this.requiredInlineOwner = requiredWrapInstance;
    }

    public static String getAccessingMemberName(String name, Method method) {
        if (!name.isBlank()) {
            return name;
        }

        name = method.getName();
        if (name.startsWith("set") || name.startsWith("get") && name.length() > 3) {
            return Character.toLowerCase(name.charAt(3)) + name.substring(4);
        }

        return name;
    }

}
