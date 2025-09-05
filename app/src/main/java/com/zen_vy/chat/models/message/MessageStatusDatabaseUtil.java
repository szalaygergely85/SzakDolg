package com.zen_vy.chat.models.message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zen_vy.chat.database.DatabaseHelper;
import com.zen_vy.chat.models.message.entity.MessageStatus;
import com.zen_vy.chat.models.message.entity.MessageStatusType;
import com.zen_vy.chat.models.user.entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageStatusDatabaseUtil {

   private final DatabaseHelper dbHelper;

   public MessageStatusDatabaseUtil(Context context, User user) {
      dbHelper = DatabaseHelper.getInstance(context, user.getUuid());
   }

   public void insertMessageStatus(MessageStatus messageStatus) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();

      MessageStatus localMessageStatus = getMessageStatus(messageStatus.getUuid());
      long rowId = 0;

      if(localMessageStatus!= null){
         rowId = localMessageStatus.getMessageStatusId();
      }else {
         ContentValues values = new ContentValues();
         values.put("uuid", messageStatus.getUuid());


        rowId = db.insertWithOnConflict(
                 dbHelper.TABLE_MESSAGE_STATUS,
                 null,
                 values,
                 SQLiteDatabase.CONFLICT_REPLACE
         );
      }

      for (Map.Entry<Long, Boolean> entry : messageStatus.getDeliveredStatuses().entrySet()) {

         ContentValues valuesDelivered = new ContentValues();
         valuesDelivered.put("messageStatusId", rowId);
         valuesDelivered.put("userId", entry.getKey());
         valuesDelivered.put("delivered", entry.getValue());
         db.insertWithOnConflict(
                 dbHelper.TABLE_MESSAGE_STATUS_DELIVERED,
                 null,
                 valuesDelivered,
                 SQLiteDatabase.CONFLICT_REPLACE
         );
      }

      for (Map.Entry<Long, MessageStatusType> entry : messageStatus.getUserStatuses().entrySet()) {

         ContentValues valuesUser = new ContentValues();
         valuesUser.put("messageStatusId", rowId);
         valuesUser.put("userId", entry.getKey());
         valuesUser.put("status",  entry.getValue().name());
         db.insertWithOnConflict(
                 dbHelper.TABLE_MESSAGE_STATUS_USER,
                 null,
                 valuesUser,
                 SQLiteDatabase.CONFLICT_REPLACE
         );
      }

   }

   public void updateMessageStatus(MessageStatus messageStatus) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();

      // --- Update base table ---
      ContentValues values = new ContentValues();
      values.put("uuid", messageStatus.getUuid());

      db.update(
              dbHelper.TABLE_MESSAGE_STATUS,
              values,
              "uuid = ?",
              new String[]{messageStatus.getUuid()}
      );

      // --- Remove old delivered statuses ---
      db.delete(
              dbHelper.TABLE_MESSAGE_STATUS_DELIVERED,
              "uuid = ?",
              new String[]{messageStatus.getUuid()}
      );

      // --- Insert new delivered statuses ---
      for (Map.Entry<Long, Boolean> entry : messageStatus.getDeliveredStatuses().entrySet()) {
         ContentValues valuesDelivered = new ContentValues();
         valuesDelivered.put("uuid", messageStatus.getUuid());
         valuesDelivered.put("userId", entry.getKey());
         valuesDelivered.put("delivered", entry.getValue() ? 1 : 0);
         db.insertWithOnConflict(
                 dbHelper.TABLE_MESSAGE_STATUS_DELIVERED,
                 null,
                 valuesDelivered,
                 SQLiteDatabase.CONFLICT_REPLACE
         );
      }

      // --- Remove old user statuses ---
      db.delete(
              dbHelper.TABLE_MESSAGE_STATUS_USER,
              "uuid = ?",
              new String[]{messageStatus.getUuid()}
      );

      // --- Insert new user statuses ---
      for (Map.Entry<Long, MessageStatusType> entry : messageStatus.getUserStatuses().entrySet()) {
         ContentValues valuesUser = new ContentValues();
         valuesUser.put("uuid", messageStatus.getUuid());
         valuesUser.put("userId", entry.getKey());
         valuesUser.put("status", entry.getValue().name());
         db.insertWithOnConflict(
                 dbHelper.TABLE_MESSAGE_STATUS_USER,
                 null,
                 valuesUser,
                 SQLiteDatabase.CONFLICT_REPLACE
         );
      }
   }

   public void deleteMessageStatus(String uuid) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();

      // Delete children first
      db.delete(
              dbHelper.TABLE_MESSAGE_STATUS_DELIVERED,
              "uuid = ?",
              new String[]{uuid}
      );

      db.delete(
              dbHelper.TABLE_MESSAGE_STATUS_USER,
              "uuid = ?",
              new String[]{uuid}
      );

      // Delete parent
      db.delete(
              dbHelper.TABLE_MESSAGE_STATUS,
              "uuid = ?",
              new String[]{uuid}
      );
   }

   public MessageStatus getMessageStatus(String uuid) {
      SQLiteDatabase db = dbHelper.getReadableDatabase();

      // Query base message status
      Cursor cursor = db.query(
              dbHelper.TABLE_MESSAGE_STATUS,
              new String[]{"uuid, messageStatusId"},
              "uuid = ?",
              new String[]{uuid},
              null,
              null,
              null
      );

      MessageStatus messageStatus = null;

      if (cursor != null && cursor.moveToFirst()) {
         messageStatus = new MessageStatus();
         messageStatus.setMessageStatusId(cursor.getLong(cursor.getColumnIndexOrThrow("messageStatusId")));
         messageStatus.setUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));

         // --- Delivered Statuses ---
         Map<Long, Boolean> deliveredStatuses = new HashMap<>();
         Cursor deliveredCursor = db.query(
                 dbHelper.TABLE_MESSAGE_STATUS_DELIVERED,
                 new String[]{"userId", "delivered"},
                 "messageStatusId = ?",
                 new String[]{String.valueOf(messageStatus.getMessageStatusId())},
                 null, null, null
         );
         if (deliveredCursor != null) {
            while (deliveredCursor.moveToNext()) {
               long userId = deliveredCursor.getLong(deliveredCursor.getColumnIndexOrThrow("userId"));
               boolean delivered = deliveredCursor.getInt(deliveredCursor.getColumnIndexOrThrow("delivered")) == 1;
               deliveredStatuses.put(userId, delivered);
            }
            deliveredCursor.close();
         }
         messageStatus.setDeliveredStatuses(deliveredStatuses);

         // --- User Statuses ---
         Map<Long, MessageStatusType> userStatuses = new HashMap<>();
         Cursor userCursor = db.query(
                 dbHelper.TABLE_MESSAGE_STATUS_USER,
                 new String[]{"userId", "status"},
                 "messageStatusId = ?",
                 new String[]{String.valueOf(messageStatus.getMessageStatusId())},
                 null, null, null
         );
         if (userCursor != null) {
            while (userCursor.moveToNext()) {
               long userId = userCursor.getLong(userCursor.getColumnIndexOrThrow("userId"));
               String statusStr = userCursor.getString(userCursor.getColumnIndexOrThrow("status"));
               MessageStatusType status = MessageStatusType.valueOf(statusStr);
               userStatuses.put(userId, status);
            }
            userCursor.close();
         }
         messageStatus.setUserStatuses(userStatuses);
      }

      if (cursor != null) cursor.close();

      return messageStatus;
   }

   public List<MessageStatus> getAllMessageStatuses() {
      List<MessageStatus> messageStatuses = new ArrayList<>();
      SQLiteDatabase db = dbHelper.getReadableDatabase();

      Cursor cursor = db.query(
              dbHelper.TABLE_MESSAGE_STATUS,
              new String[]{"uuid"},
              null, null, null, null, null
      );

      if (cursor != null) {
         while (cursor.moveToNext()) {
            String uuid = cursor.getString(cursor.getColumnIndexOrThrow("uuid"));
            MessageStatus status = getMessageStatus(uuid);
            if (status != null) {
               messageStatuses.add(status);
            }
         }
         cursor.close();
      }

      return messageStatuses;
   }


}
