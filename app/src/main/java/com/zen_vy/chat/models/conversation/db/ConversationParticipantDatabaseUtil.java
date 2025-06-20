package com.zen_vy.chat.models.conversation.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zen_vy.chat.database.DatabaseHelper;
import com.zen_vy.chat.models.conversation.entity.ConversationParticipant;
import com.zen_vy.chat.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class ConversationParticipantDatabaseUtil {

   private DatabaseHelper dbHelper;

   public ConversationParticipantDatabaseUtil(Context context, User user) {
      dbHelper = DatabaseHelper.getInstance(context, user.getUuid());
   }

   public void insertConversationParticipant(
      ConversationParticipant participant
   ) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      ContentValues values = new ContentValues();

      values.put("conversationId", participant.getConversationId());
      values.put("userId", participant.getUserId());

      db.insertWithOnConflict(
         dbHelper.TABLE_CONVERSATION_PARTICIPANTS,
         null,
         values,
         SQLiteDatabase.CONFLICT_REPLACE
      );
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
            // Assuming it's the first column
            long userId = cursor.getLong(1);

            ConversationParticipant participant = new ConversationParticipant(
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
