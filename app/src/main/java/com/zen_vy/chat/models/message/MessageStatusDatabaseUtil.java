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
import java.util.List;

public class MessageStatusDatabaseUtil {

   private final DatabaseHelper dbHelper;

   public MessageStatusDatabaseUtil(Context context, User user) {
      dbHelper = DatabaseHelper.getInstance(context, user.getUuid());
   }

   public void insertMessageStatus(MessageStatus messageStatus) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();

      ContentValues values = new ContentValues();
      values.put("uuid", messageStatus.getUuid());
      MessageStatusType messageStatusType =
         messageStatus.getMessageStatusType();
      values.put("messageStatusType", messageStatusType.name());

      db.insertWithOnConflict(
         dbHelper.TABLE_MESSAGE_STATUS,
         null,
         values,
         SQLiteDatabase.CONFLICT_REPLACE
      );
   }

   // Update
   public int update(MessageStatus status) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("uuid", status.getUuid());
      MessageStatusType messageStatusType = status.getMessageStatusType();
      values.put("messageStatusType", messageStatusType.name());

      return db.update(
         "message_status",
         values,
         "uuid = ?",
         new String[] { String.valueOf(status.getUuid()) }
      );
   }

   // Delete
   public int delete(String uuid) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      return db.delete(
         "message_status",
         "uuid = ?",
         new String[] { uuid }
      );
   }

   // Get single
   public MessageStatus get(String uuid) {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = db.query(
         "message_status",
         new String[] { "messageStatusId", "uuid", "messageStatusType" },
         "uuid = ?",
         new String[] { String.valueOf(uuid) },
         null,
         null,
         null
      );

      if (cursor.moveToFirst()) {
         MessageStatus status = new MessageStatus();
         status.setMessageStatusId(
            cursor.getLong(cursor.getColumnIndexOrThrow("messageStatusId"))
         );
         status.setUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
         status.setMessageStatusType(
            MessageStatusType.valueOf(
               cursor.getString(
                  cursor.getColumnIndexOrThrow("messageStatusType")
               )
            )
         );
         cursor.close();
         return status;
      }

       cursor.close();
      return null;
   }

   // Get all
   public List<MessageStatus> getAll() {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      List<MessageStatus> list = new ArrayList<>();
      Cursor cursor = db.rawQuery("SELECT * FROM message_status", null);

      if (cursor.moveToFirst()) {
         do {
            MessageStatus status = new MessageStatus();
            status.setMessageStatusId(
               cursor.getLong(cursor.getColumnIndexOrThrow("messageStatusId"))
            );
            status.setUuid(
               cursor.getString(cursor.getColumnIndexOrThrow("uuid"))
            );
            status.setMessageStatusType(
               MessageStatusType.valueOf(
                  cursor.getString(
                     cursor.getColumnIndexOrThrow("messageStatusType")
                  )
               )
            );
            list.add(status);
         } while (cursor.moveToNext());
      }

      cursor.close();
      return list;
   }
}
