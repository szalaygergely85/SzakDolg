package com.zen_vy.chat.models.keys.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zen_vy.chat.database.DatabaseHelper;
import com.zen_vy.chat.models.keys.Key;
import com.zen_vy.chat.models.user.entity.User;

public class KeyDatabaseUtil {

   private DatabaseHelper dbHelper;

   public KeyDatabaseUtil(Context context, User user) {
      this(context, user.getUuid());
   }

   public KeyDatabaseUtil(Context context, String userUuid) {
      dbHelper = DatabaseHelper.getInstance(context, userUuid);
   }


   public Key getKeyById(long userId) {
   SQLiteDatabase db = dbHelper.getReadableDatabase();

       try (Cursor cursor = db.rawQuery(
               "SELECT userId, keyText FROM " +
                       DatabaseHelper.TABLE_KEYS +
                       " WHERE userId = ?",
               new String[]{String.valueOf(userId)}
       )) {

           if (cursor.moveToFirst()) {
               long id = cursor.getLong(0);
               String keyString = cursor.getString(1);
               Key key = new Key(id, keyString);

               return key;
           }
       }

      return null;

}

   public void insertUser(Key key) {

         SQLiteDatabase db = dbHelper.getWritableDatabase();

         try {
            ContentValues values = new ContentValues();
            values.put("userId", key.getUserId());
            values.put("keyText", key.getKey());


            db.insertWithOnConflict(
                    DatabaseHelper.TABLE_KEYS,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
         } finally {
            db.close();
         }

   }
}
