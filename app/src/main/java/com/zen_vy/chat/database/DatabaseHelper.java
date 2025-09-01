package com.zen_vy.chat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

   private static volatile DatabaseHelper instance;
   private static final int DATABASE_VERSION = 3;

   public static final String TABLE_CONTACT = "Contact";
   public static final String TABLE_CONVERSATION_PARTICIPANTS =
      "Conversation_participants";
   public static final String TABLE_CONVERSATIONS = "Conversations";
   public static final String TABLE_IMAGE = "Image";
   public static final String TABLE_MESSAGE_ENTRY = "MessageEntry";

   public static final String TABLE_MESSAGE_STATUS = "MessageStatus";
   public static final String TABLE_USER_ENTRY = "UserEntry";

   public static final String TABLE_KEYS = "Keys";

   private static final String CREATE_TABLE_MESSAGE_STATUS =
      "CREATE TABLE " +
      TABLE_MESSAGE_STATUS +
      " (" +
      "messageStatusId INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "uuid TEXT NOT NULL," +
      "messageStatusType TEXT NOT NULL" +
      ");";

   private static final String CREATE_TABLE_KEYS =
      "CREATE TABLE " +
      TABLE_KEYS +
      " (" +
      "keyId INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "userId INTEGER NOT NULL, " +
      "keyText TEXT NOT NULL" +
      ");";

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
      "numberOfParticipants INTEGER, " +
      " lastUpdated INTEGER" +
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
      "messageId INTEGER PRIMARY KEY, " +
      "conversationId INTEGER NOT NULL, " +
      "senderId INTEGER NOT NULL, " +
      "timestamp INTEGER NOT NULL, " +
      "content TEXT, " +
      "isEncrypted BOOLEAN NOT NULL, " +
      "isRead BOOLEAN NOT NULL, " +
      "type INTEGER NOT NULL, " +
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
      "authToken TEXT," +
      "lastUpdated INTEGER," +
      "uuid TEXT" +
      ");";

   @Override
   public void onCreate(SQLiteDatabase db) {
      db.execSQL(CREATE_TABLE_CONTACT);
      db.execSQL(CREATE_TABLE_MESSAGE_ENTRY);
      db.execSQL(CREATE_TABLE_MESSAGE_STATUS);
      db.execSQL(CREATE_TABLE_USER_ENTRY);
      db.execSQL(CREATE_TABLE_CONVERSATION_PARTICIPANTS);
      db.execSQL(CREATE_TABLE_CONVERSATIONS);
      db.execSQL(CREATE_TABLE_IMAGE);
      db.execSQL(CREATE_TABLE_KEYS);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      if (oldVersion < 3) {
         // 1. Rename old table
         db.execSQL(
            "ALTER TABLE " +
            TABLE_MESSAGE_ENTRY +
            " RENAME TO MessageEntry_old;"
         );

         // 2. Create the new table with updated schema
         db.execSQL(CREATE_TABLE_MESSAGE_ENTRY);

         // 3. Copy over the data
         db.execSQL(
            "INSERT INTO " +
            TABLE_MESSAGE_ENTRY +
            " (messageId, conversationId, senderId, timestamp, content, isEncrypted, isRead, type, uUId, isUploaded) " +
            "SELECT messageId, conversationId, senderId, timestamp, " +
            "COALESCE(contentEncrypted, content) as content, " + // merge both into content
            "CASE WHEN contentEncrypted IS NOT NULL THEN 1 ELSE 0 END as isEncrypted, " +
            "isRead, type, uUId, isUploaded " +
            "FROM MessageEntry_old;"
         );

         // 4. Drop old table
         db.execSQL("DROP TABLE MessageEntry_old;");
      }
   }

   private DatabaseHelper(Context context, String uuid) {
      super(context, uuid + ".db", null, DATABASE_VERSION);
   }

   public static DatabaseHelper getInstance(Context context, String uuid) {
      if (instance == null) {
         synchronized (DatabaseHelper.class) {
            if (instance == null) {
               instance = new DatabaseHelper(context, uuid);
            }
         }
      }
      return instance;
   }
}
