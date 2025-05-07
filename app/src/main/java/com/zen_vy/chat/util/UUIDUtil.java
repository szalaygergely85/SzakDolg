package com.zen_vy.chat.util;

import java.util.UUID;

public class UUIDUtil {

   public static String UUIDGenerator() {
      // Generate a UUID
      UUID uuid = UUID.randomUUID();

      return uuid.toString();
   }
}
