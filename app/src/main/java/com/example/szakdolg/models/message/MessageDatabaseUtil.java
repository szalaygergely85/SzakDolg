package com.example.szakdolg.models.message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.szakdolg.db.DatabaseHelper;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
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
         values.put("contentEncrypted", message.getContentEncrypted());
         values.put("isRead", message.isRead());
         values.put("type", message.getType());
         values.put("content", message.getContent());
         values.put("uUId", message.getUuId());
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
            Long messageId = cursor.getLong(1); // Assuming messageId is the first column
            Long convId = cursor.getLong(2); // Assuming conversationId is the second column
            Long senderId = cursor.getLong(3); // Assuming senderId is the third column
            Long timestamp = cursor.getLong(4); // Assuming timestamp is the fourth column
            String contentEncrypted = cursor.getString(5); // Assuming content is the fifth column
            boolean isRead = cursor.getInt(6) > 0; // Assuming isRead is the sixth column
            int type = cursor.getInt(7); // Assuming type is the seventh column
            String content = cursor.getString(8); // Assuming contentSenderVersion is the eighth column
            String uuid = cursor.getString(9);
            boolean isUploaded = cursor.getInt(10) > 0;

            // Create a new MessageEntry object
            latestMessage =
            new MessageEntry(
               messageId,
               convId,
               senderId,
               timestamp,
               contentEncrypted,
               isRead,
               type,
               content,
               uuid,
               isUploaded
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
            message.setContentEncrypted(
               cursor.getString(
                  cursor.getColumnIndexOrThrow("contentEncrypted")
               )
            );
            message.setRead(
               cursor.getInt(cursor.getColumnIndexOrThrow("isRead")) == 1
            );
            message.setType(
               cursor.getInt(cursor.getColumnIndexOrThrow("type"))
            );
            message.setContent(
               cursor.getString(cursor.getColumnIndexOrThrow("content"))
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

   public List<MessageEntry> getAllMessageEntriesByConversationId(
      Long conversationId
   ) {
      List<MessageEntry> messages = new ArrayList<>();

      // Try-with-resources ensures that resources are closed automatically
      try (
         SQLiteDatabase db = dbHelper.getReadableDatabase();
         Cursor cursor = db.query(
            "MessageEntry", // Table name
            null, // Columns (null fetches all columns)
            "conversationId = ?", // WHERE clause
            new String[] { String.valueOf(conversationId) }, // WHERE arguments
            null, // GROUP BY
            null, // HAVING
            null // ORDER BY
         )
      ) {
         if (cursor != null) {
            while (cursor.moveToNext()) {
               MessageEntry message = new MessageEntry();

               // Assign values from the cursor to the message object
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
               message.setContentEncrypted(
                  cursor.getString(
                     cursor.getColumnIndexOrThrow("contentEncrypted")
                  )
               );
               message.setRead(
                  cursor.getInt(cursor.getColumnIndexOrThrow("isRead")) == 1
               ); // SQLite BOOLEAN mapped as INTEGER
               message.setType(
                  cursor.getInt(cursor.getColumnIndexOrThrow("type"))
               );
               message.setContent(
                  cursor.getString(cursor.getColumnIndexOrThrow("content"))
               );
               message.setUploaded(
                  cursor.getInt(cursor.getColumnIndexOrThrow("isUploaded")) == 1
               );

               messages.add(message);
            }
         }
      } catch (Exception e) {
         e.printStackTrace(); // Logging exceptions; consider using a logger
      }

      return messages;
   }

   public List<String> getAllMessageUuids() {
      List<String> uuids = new ArrayList<>();
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;

      try {
         cursor =
         db.query(
            "MessageEntry",
            new String[] { "uUId" },
            null,
            null,
            null,
            null,
            null
         );

         while (cursor.moveToNext()) {
            String uuid = cursor.getString(
               cursor.getColumnIndexOrThrow("uUId")
            );
            uuids.add(uuid);
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }

      return uuids;
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
         values.put("contentEncrypted", message.getContentEncrypted());
         values.put("isRead", message.isRead() ? 1 : 0); // Convert boolean to SQLite-compatible integer
         values.put("type", message.getType());
         values.put("content", message.getContent());
         values.put("isUploaded", message.isUploaded() ? 1 : 0); // Handle isUploaded column

         // Perform update using uUId as the unique identifier
         int rowsUpdated = db.update(
            "MessageEntry",
            values,
            "uUId = ?", // WHERE clause
            new String[] { message.getUuId() } // WHERE arguments
         );

         if (rowsUpdated == 0) {
            // Handle the case where no rows were updated (optional)
            Log.w("Database", "No rows updated for uUId: " + message.getUuId());
         }
      } catch (Exception e) {
         Log.e("DatabaseError", "Failed to update message entry", e);
      } finally {
         db.close(); // Ensure the database is closed to avoid leaks
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

   public MessageEntry getMessageByUuid(String uuid) {
      MessageEntry message = null;

      SQLiteDatabase db = dbHelper.getReadableDatabase();

      try (
         Cursor cursor = db.query(
            "MessageEntry", // Table name
            null, // Columns (null fetches all columns)
            "uUId = ?", // WHERE clause
            new String[] { uuid }, // WHERE arguments
            null, // GROUP BY
            null, // HAVING
            null // ORDER BY
         )
      ) {
         if (cursor != null && cursor.moveToFirst()) {
            // Create and populate the MessageEntry object
            message = new MessageEntry();
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
            message.setContentEncrypted(
               cursor.getString(
                  cursor.getColumnIndexOrThrow("contentEncrypted")
               )
            );
            message.setRead(
               cursor.getInt(cursor.getColumnIndexOrThrow("isRead")) == 1
            ); // SQLite BOOLEAN
            message.setType(
               cursor.getInt(cursor.getColumnIndexOrThrow("type"))
            );
            message.setContent(
               cursor.getString(cursor.getColumnIndexOrThrow("content"))
            );
            message.setUploaded(
               cursor.getInt(cursor.getColumnIndexOrThrow("isUploaded")) == 1
            ); // SQLite BOOLEAN
            message.setUuId(
               cursor.getString(cursor.getColumnIndexOrThrow("uUId"))
            );
         }
      } catch (Exception e) {
         Log.e(
            "DatabaseError",
            "Failed to retrieve message by uUId: " + uuid,
            e
         );
      } finally {
         db.close(); // Ensure the database is closed to avoid leaks
      }

      return message;
   }

   public int getUnreadMessageCountByConversationId(Long conversationId) {
      int unreadCount = 0;

      // Try-with-resources ensures resources are closed automatically
      try (
         SQLiteDatabase db = dbHelper.getReadableDatabase();
         Cursor cursor = db.query(
            "MessageEntry", // Table name
            new String[] { "COUNT(*) AS unreadCount" }, // Columns to fetch
            "conversationId = ? AND isRead = 0", // WHERE clause
            new String[] { String.valueOf(conversationId) }, // WHERE arguments
            null, // GROUP BY
            null, // HAVING
            null // ORDER BY
         )
      ) {
         if (cursor != null && cursor.moveToFirst()) {
            unreadCount =
            cursor.getInt(cursor.getColumnIndexOrThrow("unreadCount"));
         }
      } catch (Exception e) {
         e.printStackTrace(); // Logging exceptions; consider using a logger
      }

      return unreadCount;
   }

   public void setMessagesAsReadByConversationId(Long conversationId) {
      // Ensure valid conversationId
      if (conversationId == null) return;

      try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
         ContentValues values = new ContentValues();
         values.put("isRead", 1); // Set isRead to true

         int rowsUpdated = db.update(
            "MessageEntry", // Table name
            values, // Values to update
            "conversationId = ? AND isRead = 0", // WHERE clause
            new String[] { String.valueOf(conversationId) } // WHERE arguments
         );

         System.out.println("Messages marked as read: " + rowsUpdated);
      } catch (Exception e) {
         e.printStackTrace(); // Logging exceptions; consider using a logger
      }
   }
}
