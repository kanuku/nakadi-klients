package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

public class EnumUtil {

    /**
     * This utility can be used to Look for an Enum by its name
     * @param name
     * @param enumType
     * @return
     */
    public static <E extends Enum<E>> Optional<E> withName(String name, Class<E> enumType) {
        if (name != null)
            for (E e : enumType.getEnumConstants()) {
                if (e.name().equals(name.toUpperCase()))
                    return Optional.of(e);
            }
        return Optional.empty();
    }

}
