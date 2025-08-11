package com.zen_vy.chat.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

   public static String getMessageTime(Long timestamp) {
      // Convert timestamp to Date
      Date date = new Date(timestamp);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);

      // Get current time for comparison
      Calendar now = Calendar.getInstance();

      // Formatter for today (hours and minutes)
      SimpleDateFormat todayFormat = new SimpleDateFormat(
              "HH:mm",
              Locale.getDefault()
      );

      // Formatter for day of the week (e.g., "Monday")
      SimpleDateFormat dayFormat = new SimpleDateFormat(
              "EEEE",
              Locale.getDefault()
      );

      // Formatter for full date (e.g., "MMM dd, yyyy")
      SimpleDateFormat dateFormat = new SimpleDateFormat(
              "MMM dd, yyyy",
              Locale.getDefault()
      );

      // Check if it's today
      if (
              now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                      now.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
      ) {
         // Return hours and minutes if it's today
         return todayFormat.format(date);
      }
      // Check if it's within the last week
      else if (
              now.getTimeInMillis() - calendar.getTimeInMillis() <
                      7 * 24 * 60 * 60 * 1000
      ) {
         // Return day of the week if it's within the last 7 days
         return dayFormat.format(date);
      } else {
         // Otherwise, return full date
         return dateFormat.format(date);
      }
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

   public static String getHHmm(Long time) {
      Date date = new Date(time);

      Format format = new SimpleDateFormat("HH:mm");
      return format.format(date);
   }
}
