package com.zen_vy.chat.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {

   public static long now() {
      return System.currentTimeMillis();
   }

   // Returns how many full days have passed between now and the given time
   public static long daysFromNow(long pastTimeMillis) {
      long diffMillis = now() - pastTimeMillis;
      return TimeUnit.MILLISECONDS.toDays(diffMillis);
   }

   // Returns how many full hours have passed between now and the given time
   public static long hoursFromNow(long pastTimeMillis) {
      long diffMillis = now() - pastTimeMillis;
      return TimeUnit.MILLISECONDS.toHours(diffMillis);
   }

   // Returns how many minutes have passed between now and the given time
   public static long minutesFromNow(long pastTimeMillis) {
      long diffMillis = now() - pastTimeMillis;
      return TimeUnit.MILLISECONDS.toMinutes(diffMillis);
   }

   public static boolean isNewDay(
      long previousTimestamp,
      long currentTimestamp
   ) {
      Date currentDate = new Date(currentTimestamp);
      Date previousDate = new Date(previousTimestamp);

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      String currentDateStr = dateFormat.format(currentDate);
      String previousDateStr = dateFormat.format(previousDate);

      return !currentDateStr.equals(previousDateStr);
   }
}
