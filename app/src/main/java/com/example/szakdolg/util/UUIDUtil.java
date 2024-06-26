package com.example.szakdolg.util;

import java.util.UUID;

public class UUIDUtil {

    public static String UUIDGenerator() {
            // Generate a UUID
            UUID uuid = UUID.randomUUID();

            return uuid.toString();

    }
}
