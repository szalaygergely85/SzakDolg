package com.zen_vy.chat.models.message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import com.zen_vy.chat.database.DatabaseHelper;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.message.entity.MessageStatusType;
import com.zen_vy.chat.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class MessageDatabaseUtil {

   private final DatabaseHelper dbHelper;

   public MessageDatabaseUtil(Context context, User user) {
      dbHelper = DatabaseHelper.getInstance(context, user.getUuid());
   }

   public void insertMessageEntry(MessageEntry message) {
      if (!isExistingEntry(message)) {
         SQLiteDatabase db = dbHelper.getWritableDatabase();

         ContentValues values = new ContentValues();
         values.put("messageId", message.getMessageId());
         values.put("conversationId", message.getConversationId());
         values.put("senderId", message.getSenderId());
         values.put("timestamp", message.getTimestamp());
         values.put("content", message.getContent());
         values.put("isEncrypted", message.isEncrypted());
         values.put("type", message.getType());
         values.put("uUId", message.getUuid());
         db.insertWithOnConflict(
            dbHelper.TABLE_MESSAGE_ENTRY,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
         );
      }
   }

   private boolean isExistingEntry(MessageEntry message) {
      MessageEntry messageEntry = getMessageByUuid(message.getUuid());

      if (messageEntry == null) {
         return false;
      }
      return true;
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
            String content = cursor.getString(5); // Assuming content is the fifth column
            boolean isEncrypted = cursor.getInt(6) > 0; // Assuming isRead is the sixth column
            int type = cursor.getInt(7); // Assuming type is the seventh column

            String uuid = cursor.getString(8);
            boolean isUploaded = cursor.getInt(9) > 0;

            // Create a new MessageEntry object
            latestMessage =
            new MessageEntry(
               messageId,
               convId,
               senderId,
               timestamp,
               type,
               content,
               isEncrypted,
               uuid,
               isUploaded
            );
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
      }

      return latestMessage;
   }

   public List<MessageEntry> getAllMessageEntries() {
      List<MessageEntry> messages = new ArrayList<>();
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;

      try {
         cursor = db.query(dbHelper.TABLE_MESSAGE_ENTRY, null, null, null, null, null, null);

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
               cursor.getString(
                  cursor.getColumnIndexOrThrow("content")
               )
            );
            message.setEncrypted(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("isEncrypted")) == 1
            );

            message.setType(
               cursor.getInt(cursor.getColumnIndexOrThrow("type"))
            );

            messages.add(message);
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
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
                 dbHelper.TABLE_MESSAGE_ENTRY, // Table name
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
               message.setContent(
                  cursor.getString(
                     cursor.getColumnIndexOrThrow("content")
                  )
               );
               message.setEncrypted(
                       cursor.getInt(cursor.getColumnIndexOrThrow("isEncrypted")) == 1
               );


               message.setType(
                  cursor.getInt(cursor.getColumnIndexOrThrow("type"))
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
                 dbHelper.TABLE_MESSAGE_ENTRY,
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
         values.put("isEncrypted", message.isEncrypted() ? 1 : 0); // Convert boolean to SQLite-compatible integer
// Convert boolean to SQLite-compatible integer
         values.put("type", message.getType());
         values.put("isUploaded", message.isUploaded() ? 1 : 0); // Handle isUploaded column

         // Perform update using uUId as the unique identifier
         int rowsUpdated = db.update(
                 dbHelper.TABLE_MESSAGE_ENTRY,
            values,
            "uUId = ?", // WHERE clause
            new String[] { message.getUuid() } // WHERE arguments
         );

         if (rowsUpdated == 0) {
            // Handle the case where no rows were updated (optional)
            Log.w("Database", "No rows updated for uUId: " + message.getUuid());
         }
      } catch (Exception e) {
         Log.e("DatabaseError", "Failed to update message entry", e);
      }
   }

   public void deleteMessageEntry(Long messageId) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      try {
         db.delete(
                 dbHelper.TABLE_MESSAGE_ENTRY,
            "messageId = ?",
            new String[] { String.valueOf(messageId) }
         );
      } catch (Exception e) {
         Timber.e(e);
      }
   }

   public MessageEntry getMessageByUuid(String uuid) {
      MessageEntry message = null;

      SQLiteDatabase db = dbHelper.getReadableDatabase();

      try (
         Cursor cursor = db.query(
                 dbHelper.TABLE_MESSAGE_ENTRY, // Table name
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
            message.setContent(
               cursor.getString(
                  cursor.getColumnIndexOrThrow("content")
               )
            );
            message.setEncrypted(
                    cursor.getInt(cursor.getColumnIndexOrThrow("isEncrypted")) == 1
            ); // SQLite BOOLEAN

            message.setType(
               cursor.getInt(cursor.getColumnIndexOrThrow("type"))
            );

            message.setUploaded(
               cursor.getInt(cursor.getColumnIndexOrThrow("isUploaded")) == 1
            ); // SQLite BOOLEAN
            message.setUuid(
               cursor.getString(cursor.getColumnIndexOrThrow("uUId"))
            );
         }
      } catch (Exception e) {
         Timber
            .tag("DatabaseError")
            .e(e, "Failed to retrieve message by uUId: %s", uuid);
      }

      return message;
   }

   public int getUnreadMessageCountByConversationId(
      long conversationId,
      long userId
   ) {
      int unreadCount = 0;
try {



      SQLiteDatabase db = dbHelper.getReadableDatabase();

      String sql = "SELECT COUNT(DISTINCT m.uuid)  AS unreadCount " +
              "FROM " + dbHelper.TABLE_MESSAGE_ENTRY + " m " +
              "LEFT JOIN " + dbHelper.TABLE_MESSAGE_STATUS + " s " +
              "ON m.uuid = s.uuid " +
              "LEFT JOIN " + dbHelper.TABLE_MESSAGE_STATUS_USER + " u " +
              "ON u.messageStatusId = s.messageStatusId " +

              "WHERE m.conversationId = ? AND (u.status IS NULL OR u.status != ?)  AND m.senderId != ?";

     Cursor cursor = db.rawQuery(sql, new String[] {
              String.valueOf(conversationId),
             MessageStatusType.READ.name(),
              String.valueOf(userId),
      });


         if (cursor != null && cursor.moveToFirst()) {
            unreadCount =
                    cursor.getInt(cursor.getColumnIndexOrThrow("unreadCount"));
         }
      } catch (Exception e) {
         Timber.tag("DatabaseError").e(e, "Failed to retrieve messages");
      }
      return unreadCount;
   }

   public String getLastReadMessageInConversation(
           long conversationId,
           long userId
   ) {
      String uuid = "";
      try {



         SQLiteDatabase db = dbHelper.getReadableDatabase();
         String sql = "SELECT m.uuid  AS uuid " +
                 "FROM " + dbHelper.TABLE_MESSAGE_ENTRY + " m " +
                 "LEFT JOIN " + dbHelper.TABLE_MESSAGE_STATUS + " s " +
                 "ON m.uuid = s.uuid " +
                 "LEFT JOIN " + dbHelper.TABLE_MESSAGE_STATUS_USER + " u " +
                 "ON u.messageStatusId = s.messageStatusId " +
         "WHERE m.conversationId = ? AND (u.status IS NULL OR u.status = ?)  AND m.senderId = ? AND u.userId != ? " +
         "ORDER BY m.timestamp DESC "+
         "LIMIT 1;";

         Cursor cursor = db.rawQuery(sql, new String[] {
                 String.valueOf(conversationId),
                 MessageStatusType.READ.name(),
                 String.valueOf(userId),
                 String.valueOf(userId),
         });


         if (cursor != null && cursor.moveToFirst()) {
            uuid =
                    cursor.getString(cursor.getColumnIndexOrThrow("uuid"));
         }
      } catch (Exception e) {
         Timber.tag("DatabaseError").e(e, "Failed to retrieve messages");
      }
      return uuid;
   }


   public void setMessagesAsReadByConversationId(Long conversationId, long userId) {
      // Ensure valid conversationId
      if (conversationId == null) return;

      try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {


         String sql = "UPDATE " + dbHelper.TABLE_MESSAGE_STATUS_USER + " u " +
                 "JOIN " + dbHelper.TABLE_MESSAGE_STATUS + " s ON u.messageStatusId = s.messageStatusId " +
                 "JOIN " + dbHelper.TABLE_MESSAGE_ENTRY + " m ON m.uuid = s.uuid " +
                 "SET u.status = ? " +
                 "WHERE m.conversationId = ? " +
                 "AND (u.status IS NULL OR u.status != ?) " +
                 "AND m.senderId != ? " +
                 "AND u.userId != ?;";

         db.execSQL(sql, new Object[] {
                 MessageStatusType.READ.name(),
                 conversationId,
                 MessageStatusType.READ.name(),
                 userId,
                 userId
         });

      } catch (Exception e) {
         Timber.e(e); // Logging exceptions; consider using a logger
      }
   }
}
