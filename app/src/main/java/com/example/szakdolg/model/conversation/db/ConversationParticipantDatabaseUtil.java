package com.example.szakdolg.model.conversation.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.szakdolg.db.helper.DatabaseHelper;
import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class ConversationParticipantDatabaseUtil {

   private DatabaseHelper dbHelper;

   public ConversationParticipantDatabaseUtil(Context context, User user) {
      dbHelper = new DatabaseHelper(context, user.getUserId().toString());
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
}
