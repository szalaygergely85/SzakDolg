package com.zen_vy.chat.util;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil {

   private static final Random random = new Random();
   private static final SecureRandom secureRandom = new SecureRandom();
   private static final String CHARACTERS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

   // Generates a random int
   public static int getRandomInt() {
      return random.nextInt();
   }

   // Generates a random int within a specific range
   public static int getRandomInt(int min, int max) {
      return random.nextInt((max - min) + 1) + min;
   }

   // Generates a random long
   public static long getRandomLong() {
      return random.nextLong();
   }

   // Generates a random float
   public static float getRandomFloat() {
      return random.nextFloat();
   }

   // Generates a random double
   public static double getRandomDouble() {
      return random.nextDouble();
   }

   // Generates a random boolean
   public static boolean getRandomBoolean() {
      return random.nextBoolean();
   }

   // Generates a random String of a specified length
   public static String getRandomString(int length) {
      StringBuilder sb = new StringBuilder(length);
      for (int i = 0; i < length; i++) {
         sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
      }
      return sb.toString();
   }

   // Generates a secure random String of a specified length
   public static String getSecureRandomString(int length) {
      StringBuilder sb = new StringBuilder(length);
      for (int i = 0; i < length; i++) {
         sb.append(
            CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length()))
         );
      }
      return sb.toString();
   }

   // Generates a secure random int
   public static int getSecureRandomInt() {
      return secureRandom.nextInt();
   }

   // Generates a secure random int within a specific range
   public static int getSecureRandomInt(int min, int max) {
      return secureRandom.nextInt((max - min) + 1) + min;
   }

   // Generates a secure random long
   public static long getSecureRandomLong() {
      return secureRandom.nextLong();
   }
}
