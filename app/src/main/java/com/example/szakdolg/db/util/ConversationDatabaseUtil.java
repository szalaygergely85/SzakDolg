package com.example.szakdolg.db.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.szakdolg.conversation.entity.Conversation;
import com.example.szakdolg.conversation.entity.ConversationParticipant;
import com.example.szakdolg.db.helper.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class ConversationDatabaseUtil {

   private DatabaseHelper dbHelper;

   public ConversationDatabaseUtil(Context context) {
      dbHelper = new DatabaseHelper(context);
   }

   public long insertConversation(Conversation conversation) {
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

      return db.insert(dbHelper.TABLE_CONVERSATIONS, null, values);
   }

   public long insertConversationParticipant(
      ConversationParticipant participant
   ) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("conversationId", participant.getConversationId());
      values.put("userId", participant.getUserId());

      return db.insert(dbHelper.TABLE_CONVERSATION_PARTICIPANTS, null, values);
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

            Conversation conversation = new Conversation(
               conversationId,
               conversationName,
               timeStamp,
               creatorUserId,
               numberOfParticipants
            );
            conversations.add(conversation);
         } while (cursor.moveToNext());
      }
      cursor.close();
      return conversations;
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

   public Long getConversationCount() {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      String countQuery =
         "SELECT COUNT(*) FROM " + dbHelper.TABLE_CONVERSATIONS;
      Cursor cursor = db.rawQuery(countQuery, null);
      cursor.moveToFirst();
      Long count = cursor.getLong(0);
      cursor.close();
      db.close();
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
