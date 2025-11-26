package com.shoes.fitness.common.util;

import java.util.UUID;

public class UuidUtil {

    public static String generateShortUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
