package com.example.szakdolg.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

   private static volatile DatabaseHelper instance;
   private static final int DATABASE_VERSION = 1;

   public static final String TABLE_CONTACT = "Contact";
   public static final String TABLE_CONVERSATION_PARTICIPANTS =
      "conversation_participants";
   public static final String TABLE_CONVERSATIONS = "Conversations";
   public static final String TABLE_IMAGE = "Image";
   public static final String TABLE_MESSAGE_ENTRY = "MessageEntry";
   public static final String TABLE_USER_ENTRY = "UserEntry";

   private static final String CREATE_TABLE_CONTACT =
      "CREATE TABLE " +
      TABLE_CONTACT +
      " (" +
      "contactId INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "ownerId INTEGER NOT NULL, " +
      "contactUserId INTEGER NOT NULL" +
      ");";

   private static final String CREATE_TABLE_CONVERSATION_PARTICIPANTS =
      "CREATE TABLE " +
      TABLE_CONVERSATION_PARTICIPANTS +
      " (" +
      "conversationId INTEGER NOT NULL, " +
      "userId INTEGER NOT NULL, " +
      "PRIMARY KEY (conversationId, userId)" +
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

   private static final String CREATE_TABLE_IMAGE =
      "CREATE TABLE " +
      TABLE_IMAGE +
      " (" +
      "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "fileName TEXT NOT NULL, " +
      "userId INTEGER, " +
      "imageUri TEXT, " +
      "mimeType TEXT, " +
      "width INTEGER, " +
      "height INTEGER, " +
      "size INTEGER, " +
      "dateAdded INTEGER, " +
      "status TEXT, " +
      "tags TEXT, " +
      "uuid TEXT" +
      ");";

   private static final String CREATE_TABLE_MESSAGE_ENTRY =
      "CREATE TABLE " +
      TABLE_MESSAGE_ENTRY +
      " (" +
      "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "messageId INTEGER , " +
      "conversationId INTEGER NOT NULL, " +
      "senderId INTEGER NOT NULL, " +
      "timestamp INTEGER NOT NULL, " +
      "contentEncrypted TEXT, " +
      "isRead BOOLEAN NOT NULL, " +
      "type INTEGER NOT NULL, " +
      "content TEXT," +
      "uUId TEXT NOT NULL," +
      "isUploaded BOOLEAN DEFAULT false" +
      ");";

   private static final String CREATE_TABLE_USER_ENTRY =
      "CREATE TABLE " +
      TABLE_USER_ENTRY +
      " (" +
      "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "userId INTEGER UNIQUE, " +
      "displayName TEXT NOT NULL, " +
      "email TEXT NOT NULL, " +
      "publicKey LONGTEXT NOT NULL, " +
      "profilePictureUuid TEXT, " +
      "status TEXT, " +
      "tags TEXT, " +
      "authToken TEXT" +
      ");";

   @Override
   public void onCreate(SQLiteDatabase db) {
      db.execSQL(CREATE_TABLE_CONTACT);
      db.execSQL(CREATE_TABLE_MESSAGE_ENTRY);
      db.execSQL(CREATE_TABLE_USER_ENTRY);
      db.execSQL(CREATE_TABLE_CONVERSATION_PARTICIPANTS);
      db.execSQL(CREATE_TABLE_CONVERSATIONS);
      db.execSQL(CREATE_TABLE_IMAGE);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_ENTRY);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ENTRY);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATION_PARTICIPANTS);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
      onCreate(db);
   }

   private DatabaseHelper(Context context, String userId) {
      super(context, userId + ".db", null, DATABASE_VERSION);
   }

   public static DatabaseHelper getInstance(Context context, String userId) {
      if (instance == null) {
         synchronized (DatabaseHelper.class) {
            if (instance == null) {
               instance = new DatabaseHelper(context, userId);
            }
         }
      }
      return instance;
   }
}
