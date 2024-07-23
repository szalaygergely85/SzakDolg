package com.example.szakdolg.db.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.szakdolg.db.helper.DatabaseHelper;
import com.example.szakdolg.message.MessageEntry;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {
    private DatabaseHelper dbHelper;

    public DatabaseUtil(Context context) {
        dbHelper = new DatabaseHelper(context);
    }


    public void insertMessageEntry(MessageEntry message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
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

    public List<MessageEntry> getAllMessageEntries() {
        List<MessageEntry> messages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query("MessageEntry", null, null, null, null, null, null);

            while (cursor.moveToNext()) {
                MessageEntry message = new MessageEntry();
                message.setMessageId(cursor.getLong(cursor.getColumnIndexOrThrow("messageId")));
                message.setConversationId(cursor.getLong(cursor.getColumnIndexOrThrow("conversationId")));
                message.setSenderId(cursor.getLong(cursor.getColumnIndexOrThrow("senderId")));
                message.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
                message.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
                message.setRead(cursor.getInt(cursor.getColumnIndexOrThrow("isRead")) == 1);
                message.setType(cursor.getInt(cursor.getColumnIndexOrThrow("type")));
                message.setContentSenderVersion(cursor.getString(cursor.getColumnIndexOrThrow("contentSenderVersion")));
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

    public void updateMessageEntry(MessageEntry message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("conversationId", message.getConversationId());
            values.put("senderId", message.getSenderId());
            values.put("timestamp", message.getTimestamp());
            values.put("content", message.getContent());
            values.put("isRead", message.isRead());
            values.put("type", message.getType());
            values.put("contentSenderVersion", message.getContentSenderVersion());

            db.update("MessageEntry", values, "messageId = ?", new String[]{String.valueOf(message.getMessageId())});
        } finally {
            db.close();
        }
    }

    public void deleteMessageEntry(Long messageId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete("MessageEntry", "messageId = ?", new String[]{String.valueOf(messageId)});
        } finally {
            db.close();
        }
    }
}
