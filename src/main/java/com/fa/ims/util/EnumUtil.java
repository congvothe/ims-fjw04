package com.fa.ims.util;

import java.util.Arrays;

public class EnumUtil {

    public static <T> boolean isValidName (Class<T> clazz, String name) {
        return Arrays.stream(clazz.getEnumConstants()).anyMatch(e -> e.toString().equalsIgnoreCase(name));
    }
}
