package com.example.szakdolg.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MESSAGE_ENTRY = "MessageEntry";

    private static final String CREATE_TABLE_MESSAGE_ENTRY =
            "CREATE TABLE " + TABLE_MESSAGE_ENTRY + " (" +
                    "messageId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "conversationId INTEGER NOT NULL, " +
                    "senderId INTEGER NOT NULL, " +
                    "timestamp INTEGER NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "isRead BOOLEAN NOT NULL, " +
                    "type INTEGER NOT NULL, " +
                    "contentSenderVersion TEXT" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MESSAGE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_ENTRY);
        onCreate(db);
    }
}
