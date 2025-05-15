package com.zen_vy.chat.models.user.dbutil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.zen_vy.chat.database.DatabaseHelper;
import com.zen_vy.chat.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class UserDatabaseUtil {

   private DatabaseHelper dbHelper;

   public UserDatabaseUtil(Context context, User user) {
      this(context, user.getUserId().toString());
   }

   public UserDatabaseUtil(Context context, String userId) {
      dbHelper = DatabaseHelper.getInstance(context, userId);
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
            Long id = cursor.getLong(0);
            Long userId = cursor.getLong(1);
            String displayName = cursor.getString(2);
            String email = cursor.getString(3);
            String publicKey = cursor.getString(4);
            String profilePictureUuid = cursor.getString(5);
            String status = cursor.getString(6);
            String tags = cursor.getString(7);
            String authToken = cursor.getString(8);
            Long lastUpdated = cursor.getLong(9);

            User user = new User(
               userId,
               displayName,
               email,
               publicKey,
               profilePictureUuid,
               status,
               tags,
               authToken,
                    lastUpdated
            );
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

   public User getCurrentUserByToken(String token) {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;

      try {
         cursor =
         db.rawQuery(
            "SELECT id, userId, displayName, email, publicKey, profilePictureUuid, status, tags, authToken, lastUpdated FROM " +
            dbHelper.TABLE_USER_ENTRY +
            " WHERE authToken = ?",
            new String[] { token }
         );

         if (cursor != null && cursor.moveToFirst()) {
            Long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            Long userId = cursor.getLong(
               cursor.getColumnIndexOrThrow("userId")
            );
            String displayName = cursor.getString(
               cursor.getColumnIndexOrThrow("displayName")
            );
            String email = cursor.getString(
               cursor.getColumnIndexOrThrow("email")
            );
            String publicKey = cursor.getString(
               cursor.getColumnIndexOrThrow("publicKey")
            );
            String profilePictureUuid = cursor.getString(
               cursor.getColumnIndexOrThrow("profilePictureUuid")
            );
            String status = cursor.getString(
               cursor.getColumnIndexOrThrow("status")
            );
            String tags = cursor.getString(
               cursor.getColumnIndexOrThrow("tags")
            );
            String authToken = cursor.getString(
               cursor.getColumnIndexOrThrow("authToken")
            );
            Long lastUpdated = cursor.getLong(
                    cursor.getColumnIndexOrThrow("lastUpdated")
            );

            return new User(
               userId,
               displayName,
               email,
               publicKey,
               profilePictureUuid,
               status,
               tags,
               authToken,
                    lastUpdated
            );
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }

      return null; // Return null if no user found with the given token
   }

   public User getUserById(Long userId) {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      Cursor cursor = null;

      try {
         cursor =
         db.rawQuery(
            "SELECT id, displayName, email, publicKey, profilePictureUuid, status, tags, authToken, lastUpdated FROM " +
            dbHelper.TABLE_USER_ENTRY +
            " WHERE userId = ?",
            new String[] { String.valueOf(userId) }
         );

         if (cursor != null && cursor.moveToFirst()) {
            Long id = cursor.getLong(0);
            String displayName = cursor.getString(1);
            String email = cursor.getString(2);
            String publicKey = cursor.getString(3);
            String profilePictureUuid = cursor.getString(4);
            String status = cursor.getString(5);
            String tags = cursor.getString(6);
            String authToken = cursor.getString(7);
            Long lastUpdated = cursor.getLong(8);
            // Initialize user object with all retrieved fields
            User user = new User(
               userId,
               displayName,
               email,
               publicKey,
               profilePictureUuid,
               status,
               tags,
               authToken,
                    lastUpdated
            );
            return user;
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
      if(_isInsertable(user)) {
         SQLiteDatabase db = dbHelper.getWritableDatabase();

         try {
            ContentValues values = new ContentValues();
            values.put("userId", user.getUserId());
            values.put("displayName", user.getDisplayName());
            values.put("email", user.getEmail());
            values.put("publicKey", user.getPublicKey());
            values.put("profilePictureUuid", user.getProfilePictureUuid());
            values.put("status", user.getStatus());
            values.put("tags", user.getTags());
            values.put("authToken", user.getToken());

            db.insertWithOnConflict(
                    dbHelper.TABLE_USER_ENTRY,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
         } finally {
            db.close();
         }
      }
   }

   private boolean _isInsertable(User user) {
      User localUser = getUserById(user.getUserId());

         if(localUser == null || user.getLastUpdated()==null || localUser.getLastUpdated()< user.getLastUpdated()){
            return true;
         }

      return false;
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

   public void updateUser(User user) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();

      try {
         ContentValues values = new ContentValues();
         values.put("displayName", user.getDisplayName());
         values.put("email", user.getEmail());
         values.put("publicKey", user.getPublicKey());
         values.put("profilePictureUuid", user.getProfilePictureUuid());
         values.put("status", user.getStatus());
         values.put("tags", user.getTags());
         values.put("authToken", user.getToken());

         // Update the user record where userId matches
         int rowsAffected = db.update(
            dbHelper.TABLE_USER_ENTRY,
            values,
            "userId = ?",
            new String[] { user.getUserId().toString() }
         );

         if (rowsAffected == 0) {
            Log.e(
               "UserDatabaseUtil",
               "No user found with ID: " + user.getUserId()
            );
         } else {
            Log.i(
               "UserDatabaseUtil",
               "User updated successfully: " + user.getUserId()
            );
         }
      } finally {
         db.close();
      }
   }
}
