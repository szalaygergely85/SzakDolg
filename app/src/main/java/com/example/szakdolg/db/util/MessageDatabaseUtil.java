package com.example.szakdolg.db.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.szakdolg.db.helper.DatabaseHelper;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class MessageDatabaseUtil {

   private DatabaseHelper dbHelper;

   public MessageDatabaseUtil(Context context, User user) {
      dbHelper = new DatabaseHelper(context, user.getUserId().toString());
   }

   public void insertMessageEntry(MessageEntry message) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      try {
         ContentValues values = new ContentValues();
         values.put("messageId", message.getMessageId());
         values.put("conversationId", message.getConversationId());
         values.put("senderId", message.getSenderId());
         values.put("timestamp", message.getTimestamp());
         values.put("content", message.getContent());
         values.put("isRead", message.isRead());
         values.put("type", message.getType());
         values.put("contentSenderVersion", message.getContentSenderVersion());
         db.insert("MessageEntry", null, values);
      } finally {
         db.close();
      }
   }

   public MessageEntry getLatestMessageEntry(long conversationId) {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      MessageEntry latestMessage = null;
      Cursor cursor = null;

      try {
         // Query to select the latest message based on the highest timestamp
         String query =
            "SELECT * FROM MessageEntry WHERE conversationId = ? ORDER BY timestamp DESC LIMIT 1";
         cursor =
         db.rawQuery(query, new String[] { String.valueOf(conversationId) });

         if (cursor != null && cursor.moveToFirst()) {
            // Extract the message details from the cursor using column indices
            Long messageId = cursor.getLong(0); // Assuming messageId is the first column
            Long convId = cursor.getLong(1); // Assuming conversationId is the second column
            Long senderId = cursor.getLong(2); // Assuming senderId is the third column
            Long timestamp = cursor.getLong(3); // Assuming timestamp is the fourth column
            String content = cursor.getString(4); // Assuming content is the fifth column
            boolean isRead = cursor.getInt(5) > 0; // Assuming isRead is the sixth column
            int type = cursor.getInt(6); // Assuming type is the seventh column
            String contentSenderVersion = cursor.getString(7); // Assuming contentSenderVersion is the eighth column

            // Create a new MessageEntry object
            latestMessage =
            new MessageEntry(
               messageId,
               convId,
               senderId,
               timestamp,
               content,
               isRead,
               type,
               contentSenderVersion
            );
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }

      return latestMessage;
   }

   public List<MessageEntry> getAllMessageEntries() {
      List<MessageEntry> messages = new ArrayList<>();
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;

      try {
         cursor = db.query("MessageEntry", null, null, null, null, null, null);

         while (cursor.moveToNext()) {
            MessageEntry message = new MessageEntry();
            message.setMessageId(
               cursor.getLong(cursor.getColumnIndexOrThrow("messageId"))
            );
            message.setConversationId(
               cursor.getLong(cursor.getColumnIndexOrThrow("conversationId"))
            );
            message.setSenderId(
               cursor.getLong(cursor.getColumnIndexOrThrow("senderId"))
            );
            message.setTimestamp(
               cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
            );
            message.setContent(
               cursor.getString(cursor.getColumnIndexOrThrow("content"))
            );
            message.setRead(
               cursor.getInt(cursor.getColumnIndexOrThrow("isRead")) == 1
            );
            message.setType(
               cursor.getInt(cursor.getColumnIndexOrThrow("type"))
            );
            message.setContentSenderVersion(
               cursor.getString(
                  cursor.getColumnIndexOrThrow("contentSenderVersion")
               )
            );
            messages.add(message);
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }

      return messages;
   }

   public List<MessageEntry> getAllMessageEntriesOfConversation(
      Long conversationId
   ) {
      List<MessageEntry> messages = new ArrayList<>();
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;

      try {
         cursor =
         db.query(
            "MessageEntry",
            null,
            "conversationId = ?",
            new String[] { String.valueOf(conversationId) },
            null,
            null,
            null
         );

         while (cursor.moveToNext()) {
            MessageEntry message = new MessageEntry();
            message.setMessageId(
               cursor.getLong(cursor.getColumnIndexOrThrow("messageId"))
            );
            message.setConversationId(
               cursor.getLong(cursor.getColumnIndexOrThrow("conversationId"))
            );
            message.setSenderId(
               cursor.getLong(cursor.getColumnIndexOrThrow("senderId"))
            );
            message.setTimestamp(
               cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
            );
            message.setContent(
               cursor.getString(cursor.getColumnIndexOrThrow("content"))
            );
            message.setRead(
               cursor.getInt(cursor.getColumnIndexOrThrow("isRead")) == 1
            );
            message.setType(
               cursor.getInt(cursor.getColumnIndexOrThrow("type"))
            );
            message.setContentSenderVersion(
               cursor.getString(
                  cursor.getColumnIndexOrThrow("contentSenderVersion")
               )
            );

            messages.add(message);
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }

      return messages;
   }

   public List<Long> getAllMessageIds() {
      List<Long> ids = new ArrayList<>();
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;

      try {
         cursor =
         db.query(
            "MessageEntry",
            new String[] { "messageId" },
            null,
            null,
            null,
            null,
            null
         );

         while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndexOrThrow("messageId"));
            ids.add(id);
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }

      return ids;
   }

   public Long getMessageEntryCount() {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      String countQuery =
         "SELECT COUNT(*) FROM " + dbHelper.TABLE_MESSAGE_ENTRY;
      Cursor cursor = db.rawQuery(countQuery, null);
      cursor.moveToFirst();
      Long count = cursor.getLong(0);
      cursor.close();
      db.close();
      return count;
   }

   public void updateMessageEntry(MessageEntry message) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      try {
         ContentValues values = new ContentValues();
         values.put("messageId", message.getMessageId());
         values.put("conversationId", message.getConversationId());
         values.put("senderId", message.getSenderId());
         values.put("timestamp", message.getTimestamp());
         values.put("content", message.getContent());
         values.put("isRead", message.isRead());
         values.put("type", message.getType());
         values.put("contentSenderVersion", message.getContentSenderVersion());

         db.update(
            "MessageEntry",
            values,
            "messageId = ?",
            new String[] { String.valueOf(message.getMessageId()) }
         );
      } finally {
         db.close();
      }
   }

   public void deleteMessageEntry(Long messageId) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      try {
         db.delete(
            "MessageEntry",
            "messageId = ?",
            new String[] { String.valueOf(messageId) }
         );
      } finally {
         db.close();
      }
   }
}
