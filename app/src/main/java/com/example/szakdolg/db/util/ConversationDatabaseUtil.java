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

    public long insertConversation(String conversationName, long timeStamp, long creatorUserId, int numberOfParticipants) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("conversationName", conversationName);
        values.put("timeStamp", timeStamp);
        values.put("creatorUserId", creatorUserId);
        values.put("numberOfParticipants", numberOfParticipants);

        return db.insert(dbHelper.TABLE_CONVERSATIONS, null, values);
    }

    // Method to insert a new conversation participant
    public long insertConversationParticipant(long conversationId, long userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("conversationId", conversationId);
        values.put("userId", userId);

        return db.insert(dbHelper.TABLE_CONVERSATION_PARTICIPANTS, null, values);
    }

    // Method to get all conversations
    public List<Conversation> getAllConversations() {
        List<Conversation> conversations = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + dbHelper.TABLE_CONVERSATIONS, null);

        if (cursor.moveToFirst()) {
            do {
                long conversationId = cursor.getLong(0);
                String conversationName = cursor.getString(1);
                long timeStamp = cursor.getLong(2);
                long creatorUserId = cursor.getLong(3);
                int numberOfParticipants = cursor.getInt(4);

                Conversation conversation = new Conversation(conversationId, conversationName, timeStamp, creatorUserId, numberOfParticipants);
                conversations.add(conversation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return conversations;
    }

    public List<ConversationParticipant> getParticipantsByConversationId(long conversationId) {
        List<ConversationParticipant> participants = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + dbHelper.TABLE_CONVERSATION_PARTICIPANTS + " WHERE conversationId = ?", new String[]{String.valueOf(conversationId)});

        if (cursor.moveToFirst()) {
            do {
                long conversationParticipantId = cursor.getLong(1);
                long userId = cursor.getLong(2);

                ConversationParticipant participant = new ConversationParticipant(conversationParticipantId, conversationId, userId);
                participants.add(participant);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return participants;
    }


}
