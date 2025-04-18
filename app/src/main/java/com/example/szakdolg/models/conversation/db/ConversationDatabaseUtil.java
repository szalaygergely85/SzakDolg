package com.example.szakdolg.models.conversation.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.szakdolg.db.DatabaseHelper;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.entity.ConversationParticipant;
import com.example.szakdolg.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class ConversationDatabaseUtil {

   private DatabaseHelper dbHelper;

   public ConversationDatabaseUtil(Context context, User user) {
      dbHelper =
      DatabaseHelper.getInstance(context, user.getUserId().toString());
   }

   public void insertConversation(Conversation conversation) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("conversationId", conversation.getConversationId());
      values.put("conversationName", conversation.getConversationName());
      values.put("timeStamp", conversation.getTimeStamp());
      values.put("creatorUserId", conversation.getCreatorUserId());
      values.put(
         "numberOfParticipants",
         conversation.getNumberOfParticipants()
      );

      db.insertWithOnConflict(
         dbHelper.TABLE_CONVERSATIONS,
         null,
         values,
         SQLiteDatabase.CONFLICT_REPLACE
      );
      db.close();
   }

   public void insertConversationParticipant(
      ConversationParticipant participant
   ) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(
         "conversationParticipantId",
         participant.getConversationParticipantId()
      );
      values.put("conversationId", participant.getConversationId());
      values.put("userId", participant.getUserId());

      db.insert(dbHelper.TABLE_CONVERSATION_PARTICIPANTS, null, values);
      db.close();
   }

   // Method to get all conversations
   public List<Conversation> getAllConversations() {
      List<Conversation> conversations = new ArrayList<>();
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = db.rawQuery(
         "SELECT * FROM " + dbHelper.TABLE_CONVERSATIONS,
         null
      );

      if (cursor.moveToFirst()) {
         do {
            long conversationId = cursor.getLong(0);
            String conversationName = cursor.getString(1);
            long timeStamp = cursor.getLong(2);
            long creatorUserId = cursor.getLong(3);
            int numberOfParticipants = cursor.getInt(4);
            long lastUpdated = cursor.getLong(5);

            Conversation conversation = new Conversation(
               conversationId,
               conversationName,
               timeStamp,
               creatorUserId,
               numberOfParticipants,
                    lastUpdated
            );
            conversations.add(conversation);
         } while (cursor.moveToNext());
      }
      cursor.close();
      return conversations;
   }

   public Conversation getConversationById(Long conversationId) {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      String[] columns = {
         "conversationId",
         "conversationName",
         "timeStamp",
         "creatorUserId",
         "numberOfParticipants",
      };
      String selection = "conversationId = ?";
      String[] selectionArgs = { String.valueOf(conversationId) };

      Cursor cursor = db.query(
         dbHelper.TABLE_CONVERSATIONS, // Table name
         columns, // Columns to return
         selection, // WHERE clause
         selectionArgs, // WHERE clause arguments
         null, // GROUP BY
         null, // HAVING
         null // ORDER BY
      );

      Conversation conversation = null;
      if (cursor != null && cursor.moveToFirst()) {
         conversation = new Conversation();
         conversation.setConversationId(
            cursor.getLong(cursor.getColumnIndexOrThrow("conversationId"))
         );
         conversation.setConversationName(
            cursor.getString(cursor.getColumnIndexOrThrow("conversationName"))
         );
         conversation.setTimeStamp(
            cursor.getLong(cursor.getColumnIndexOrThrow("timeStamp"))
         );
         conversation.setCreatorUserId(
            cursor.getLong(cursor.getColumnIndexOrThrow("creatorUserId"))
         );
         conversation.setNumberOfParticipants(
            cursor.getInt(cursor.getColumnIndexOrThrow("numberOfParticipants"))
         );
         conversation.setLastUpdated(
                 cursor.getInt(cursor.getColumnIndexOrThrow("lastUpdated"))
         );
         cursor.close();
      }

      db.close();
      return conversation;
   }

   public List<ConversationParticipant> getParticipantsByConversationId(
      long conversationId
   ) {
      List<ConversationParticipant> participants = new ArrayList<>();
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = db.rawQuery(
         "SELECT * FROM " +
         dbHelper.TABLE_CONVERSATION_PARTICIPANTS +
         " WHERE conversationId = ?",
         new String[] { String.valueOf(conversationId) }
      );

      if (cursor.moveToFirst()) {
         do {
            long conversationParticipantId = cursor.getLong(0); // Assuming it's the first column
            long userId = cursor.getLong(2);

            ConversationParticipant participant = new ConversationParticipant(
               conversationParticipantId,
               conversationId,
               userId
            );
            participants.add(participant);
         } while (cursor.moveToNext());
      }
      cursor.close();
      return participants;
   }

   public int getConversationCount() {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      int count = 0;
      Cursor cursor = null;
      String countQuery =
         "SELECT COUNT(*) FROM " + dbHelper.TABLE_CONVERSATIONS;
      try {
         cursor = db.rawQuery(countQuery, null);
         if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0); // Retrieve as string
            // Convert to Long
         } else {
            // Log.e("DatabaseError", "Cursor is null or empty");
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }
      return count;
   }

   public Long getConversationParticipantCount() {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      String countQuery =
         "SELECT COUNT(*) FROM " + dbHelper.TABLE_CONVERSATION_PARTICIPANTS;
      Cursor cursor = db.rawQuery(countQuery, null);
      cursor.moveToFirst();
      Long count = cursor.getLong(0);
      cursor.close();
      db.close();
      return count;
   }

   public List<ConversationParticipant> getAllConversationParticipant() {
      List<ConversationParticipant> participants = new ArrayList<>();
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;

      try {
         cursor =
         db.rawQuery(
            "SELECT * FROM " + dbHelper.TABLE_CONVERSATION_PARTICIPANTS,
            null
         );

         if (cursor.moveToFirst()) {
            do {
               long conversationParticipantId = cursor.getLong(0); // Assuming it's the first column
               long conversationId = cursor.getLong(1); // Assuming it's the second column
               long userId = cursor.getLong(2); // Assuming it's the third column

               ConversationParticipant participant =
                  new ConversationParticipant(
                     conversationParticipantId,
                     conversationId,
                     userId
                  );
               participants.add(participant);
            } while (cursor.moveToNext());
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }
      return participants;
   }
}
