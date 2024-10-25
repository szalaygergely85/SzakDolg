package com.example.szakdolg.db.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.szakdolg.db.helper.DatabaseHelper;
import com.example.szakdolg.model.user.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserDatabaseUtil {

   private DatabaseHelper dbHelper;

   public UserDatabaseUtil(Context context, User user) {
      dbHelper = new DatabaseHelper(context, user.getUserId().toString());
   }

   public List<Long> getAllUserIds() {
      List<Long> userIds = new ArrayList<>();
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;
      try {
         cursor =
         db.rawQuery("SELECT userId FROM " + dbHelper.TABLE_USER_ENTRY, null);

         if (cursor.moveToFirst()) {
            do {
               Long userId = cursor.getLong(0);
               userIds.add(userId);
            } while (cursor.moveToNext());
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }
      return userIds;
   }

   public List<User> getAllUsers() {
      List<User> users = new ArrayList<>();
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;

      try {
         cursor =
         db.rawQuery("SELECT * FROM " + dbHelper.TABLE_USER_ENTRY, null);

         while (cursor.moveToNext()) {
            Long userId = cursor.getLong(0);
            String displayName = cursor.getString(1);
            String fullName = cursor.getString(2);
            String publicKey = cursor.getString(3);

            User user = new User(userId, displayName, fullName, publicKey);
            users.add(user);
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }
      return users;
   }

   public User getUserById(Long userId) {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;

      try {
         cursor =
         db.rawQuery(
            "SELECT displayName, fullName, publicKey FROM " +
            dbHelper.TABLE_USER_ENTRY +
            " WHERE userId = ?",
            new String[] { String.valueOf(userId) }
         );
         if (cursor != null) {
            if (cursor.moveToFirst()) {
               String displayName = cursor.getString(0);
               String fullName = cursor.getString(1);
               String publicKey = cursor.getString(2);

               User user = new User(userId, displayName, fullName, publicKey);
               cursor.close();
               return user;
            }
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }

      return null;
   }

   public int getUserCount() {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;
      int count = 0;
      try {
         cursor =
         db.rawQuery("SELECT COUNT(*) FROM " + dbHelper.TABLE_USER_ENTRY, null);
         if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }
      return count;
   }

   public void insertUser(User user) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();

      try {
         ContentValues values = new ContentValues();
         values.put("userId", user.getUserId());
         values.put("displayName", user.getDisplayName());
         values.put("publicKey", user.getPublicKey());

         db.insert(dbHelper.TABLE_USER_ENTRY, null, values);
      } finally {
         db.close();
      }
   }

   public void deleteUser(int userId) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      try {
         db.delete(
            dbHelper.TABLE_USER_ENTRY,
            "userId = ?",
            new String[] { String.valueOf(userId) }
         );
      } finally {
         db.close();
      }
   }
}
