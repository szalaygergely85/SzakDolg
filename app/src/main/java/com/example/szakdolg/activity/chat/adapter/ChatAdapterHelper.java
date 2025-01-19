package com.example.szakdolg.activity.chat.adapter;

import com.example.szakdolg.models.message.entity.MessageEntry;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapterHelper {

   private List<MessageEntry> messageEntries;

   public ChatAdapterHelper(List<MessageEntry> messageEntries) {
      this.messageEntries = messageEntries;
   }

   public String getTime(Long time) {
      Date date = new Date(time);

      Format format = new SimpleDateFormat("HH:mm");
      return format.format(date);
   }

   public boolean isNewDay(int position) {
      if (position > 0) {
         // Get the timestamps of the current and previous messages
         long currentTimestamp = messageEntries.get(position).getTimestamp();
         long previousTimestamp = messageEntries
            .get(position - 1)
            .getTimestamp();

         // Convert timestamps to Date objects
         Date currentDate = new Date(currentTimestamp);
         Date previousDate = new Date(previousTimestamp);

         // Format to get only the date (year, month, and day)
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

         String currentDateStr = dateFormat.format(currentDate);
         String previousDateStr = dateFormat.format(previousDate);

         // If the dates are different, return true (new day)
         return !currentDateStr.equals(previousDateStr);
      } else {
         // If it's the first message, it's a new day
         return true;
      }
   }

   public boolean isNextMessageNewDay(int position) {
      if (position < messageEntries.size() - 1) {
         // Get the timestamps of the current and previous messages
         long currentTimestamp = messageEntries.get(position).getTimestamp();
         long previousTimestamp = messageEntries
            .get(position + 1)
            .getTimestamp();

         // Convert timestamps to Date objects
         Date currentDate = new Date(currentTimestamp);
         Date previousDate = new Date(previousTimestamp);

         // Format to get only the date (year, month, and day)
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

         String currentDateStr = dateFormat.format(currentDate);
         String previousDateStr = dateFormat.format(previousDate);

         // If the dates are different, return true (new day)
         return !currentDateStr.equals(previousDateStr);
      } else {
         // If it's the first message, it's a new day
         return true;
      }
   }

   public boolean shouldShowTime(int position) {
      if (position == 0) return true; // Always show time for the first message
      long currentTime = messageEntries.get(position).getTimestamp();
      long previousTime = messageEntries.get(position - 1).getTimestamp();
      if (
         !isTheSameSender(position) ||
         isNewDay(position) ||
         (isTheSameSender(position) &&
            (currentTime - previousTime) > (10 * 60 * 1000))
      ) {
         return true;
      } else {
         return false; // 10 minutes in milliseconds
      }
   }

   public boolean shouldShowProfilePicture(int position) {
      if (!isTheSameSender(position) || isNextMessageNewDay(position)) {
         return true;
      } else {
         return false;
      }
   }

   private boolean isTheSameSender(int position) {
      if (position == messageEntries.size() - 1) return true; // Always show picture for the first message
      Long currentSender = messageEntries.get(position).getSenderId();
      Long nextSender = messageEntries.get(position + 1).getSenderId();
      return currentSender.equals(nextSender);
   }
}
