package com.example.szakdolg.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

   private static final int DATABASE_VERSION = 1;

   public static final String TABLE_CONVERSATION_PARTICIPANTS =
      "conversation_participants";
   public static final String TABLE_CONVERSATIONS = "conversations";
   public static final String TABLE_MESSAGE_ENTRY = "MessageEntry";
   public static final String TABLE_PROFILE = "Profile";
   public static final String TABLE_USER_ENTRY = "UserEntry";

   private static final String CREATE_TABLE_CONVERSATION_PARTICIPANTS =
      "CREATE TABLE " +
      TABLE_CONVERSATION_PARTICIPANTS +
      " (" +
      "conversationParticipantId INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "conversationId INTEGER NOT NULL, " +
      "userId INTEGER NOT NULL" +
      ");";

   private static final String CREATE_TABLE_CONVERSATIONS =
      "CREATE TABLE " +
      TABLE_CONVERSATIONS +
      " (" +
      "conversationId INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "conversationName TEXT, " +
      "timeStamp INTEGER, " +
      "creatorUserId INTEGER, " +
      "numberOfParticipants INTEGER" +
      ");";

   private static final String CREATE_TABLE_MESSAGE_ENTRY =
      "CREATE TABLE " +
      TABLE_MESSAGE_ENTRY +
      " (" +
      "messageId INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "conversationId INTEGER NOT NULL, " +
      "senderId INTEGER NOT NULL, " +
      "timestamp INTEGER NOT NULL, " +
      "content TEXT NOT NULL, " +
      "isRead BOOLEAN NOT NULL, " +
      "type INTEGER NOT NULL, " +
      "contentSenderVersion TEXT," +
      "uUId TEXT" +
      ");";

   private static final String CREATE_TABLE_PROFILE =
      "CREATE TABLE " +
      TABLE_PROFILE +
      " (" +
      "userId INTEGER PRIMARY KEY, " +
      "displayName TEXT NOT NULL, " +
      "fullName TEXT NOT NULL, " +
      "email TEXT NOT NULL, " +
      "phoneNumber TEXT NOT NULL," +
      "token TEXT NOT NULL" +
      ");";
   private static final String CREATE_TABLE_USER_ENTRY =
      "CREATE TABLE " +
      TABLE_USER_ENTRY +
      " (" +
      "userId INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "displayName TEXT NOT NULL, " +
      "fullName TEXT NOT NULL, " +
      "publicKey LONGTEXT NOT NULL " +
      ");";

   public DatabaseHelper(Context context, String userId) {
      super(context, userId + ".db", null, DATABASE_VERSION);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      db.execSQL(CREATE_TABLE_MESSAGE_ENTRY);
      db.execSQL(CREATE_TABLE_USER_ENTRY);
      db.execSQL(CREATE_TABLE_CONVERSATION_PARTICIPANTS);
      db.execSQL(CREATE_TABLE_CONVERSATIONS);
      db.execSQL(CREATE_TABLE_PROFILE);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_ENTRY);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ENTRY);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATION_PARTICIPANTS);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
      onCreate(db);
   }
}
