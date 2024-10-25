package com.example.szakdolg.db.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.szakdolg.db.helper.DatabaseHelper;
import com.example.szakdolg.model.user.model.User;

public class ProfileDatabaseUtil {

   private DatabaseHelper dbHelper;

   public ProfileDatabaseUtil(Context context, String userId) {
      dbHelper = new DatabaseHelper(context, userId);
   }

   public void insertProfile(User user, String token) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      try {
         ContentValues values = new ContentValues();
         values.put("userId", user.getUserId());
         values.put("displayName", user.getDisplayName());
         values.put("email", user.getEmail());
         values.put("token", token);

         db.insert(dbHelper.TABLE_PROFILE, null, values);
      } finally {
         db.close();
      }
   }

   public User getCurrentUserByToken(String token) {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      User currentUser = null;
      Cursor cursor = null;
      try {
         // Query to select the latest message based on the highest timestamp
         String query =
            "SELECT * FROM " + dbHelper.TABLE_PROFILE + " WHERE token = ?;";
         cursor = db.rawQuery(query, new String[] { token });
         if (cursor != null && cursor.moveToFirst()) {
            Long userID = cursor.getLong(0); // Assuming messageId is the first column
            String displayName = cursor.getString(1); // Assuming conversationId is the second column
            String fullName = cursor.getString(2); // Assuming senderId is the third column
            String email = cursor.getString(3); // Assuming timestamp is the fourth column
            Long phoneNumber = cursor.getLong(4); // Assuming content is the fifth column
            currentUser =
            new User(userID, displayName, fullName, email, phoneNumber);
         }
      } finally {
         if (cursor != null) {
            cursor.close();
         }
         db.close();
      }

      return currentUser;
   }
}
