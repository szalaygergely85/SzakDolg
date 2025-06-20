package com.zen_vy.chat.models.image.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zen_vy.chat.database.DatabaseHelper;
import com.zen_vy.chat.models.image.entity.ImageEntity;
import com.zen_vy.chat.models.user.entity.User;

public class ImageDatabaseUtil {

   private DatabaseHelper dbHelper;

   public ImageDatabaseUtil(Context context, User user) {
      dbHelper = DatabaseHelper.getInstance(context, user.getUuid());
   }

   public void deleteImageEntityByUuid(String uuid) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      try {
         db.delete("Image", "uuid = ?", new String[] { uuid });
      } finally {
         db.close();
      }
   }

   public ImageEntity getImageEntityByUuid(String uuid) {
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      ImageEntity imageEntity = null;

      try (
         Cursor cursor = db.query(
            "Image", // Table name
            null, // Select all columns
            "uuid = ?", // WHERE clause
            new String[] { uuid }, // WHERE arguments
            null, // Group by
            null, // Having
            null // Order by
         )
      ) {
         if (cursor != null && cursor.moveToFirst()) {
            imageEntity = new ImageEntity();
            imageEntity.setId(
               cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            );
            imageEntity.setFileName(
               cursor.getString(cursor.getColumnIndexOrThrow("fileName"))
            );
            imageEntity.setUserId(
               cursor.getLong(cursor.getColumnIndexOrThrow("userId"))
            );
            imageEntity.setImageUri(
               cursor.getString(cursor.getColumnIndexOrThrow("imageUri"))
            );
            imageEntity.setMimeType(
               cursor.getString(cursor.getColumnIndexOrThrow("mimeType"))
            );
            imageEntity.setWidth(
               cursor.getInt(cursor.getColumnIndexOrThrow("width"))
            );
            imageEntity.setHeight(
               cursor.getInt(cursor.getColumnIndexOrThrow("height"))
            );
            imageEntity.setSize(
               cursor.getLong(cursor.getColumnIndexOrThrow("size"))
            );
            imageEntity.setDateAdded(
               cursor.getLong(cursor.getColumnIndexOrThrow("dateAdded"))
            );
            imageEntity.setStatus(
               cursor.getString(cursor.getColumnIndexOrThrow("status"))
            );
            imageEntity.setTags(
               cursor.getString(cursor.getColumnIndexOrThrow("tags"))
            );
            imageEntity.setUuid(
               cursor.getString(cursor.getColumnIndexOrThrow("uuid"))
            );
         }
      } catch (Exception e) {
         // Handle the exception (e.g., log it)
         e.printStackTrace();
      } finally {
         if (db != null && db.isOpen()) {
            db.close(); // Ensure the database is closed
         }
      }

      return imageEntity;
   }

   public void insertImageEntity(ImageEntity image) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      try {
         ContentValues values = new ContentValues();
         values.put("fileName", image.getFileName());
         values.put("userId", image.getUserId());
         values.put("imageUri", image.getImageUri());
         values.put("mimeType", image.getMimeType());
         values.put("width", image.getWidth());
         values.put("height", image.getHeight());
         values.put("size", image.getSize());
         values.put("dateAdded", image.getDateAdded());
         values.put("status", image.getStatus());
         values.put("tags", image.getTags());
         values.put("uuid", image.getUuid());
         db.insert("Image", null, values);
      } finally {
         db.close();
      }
   }

   public void updateImageEntity(ImageEntity image) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      try {
         ContentValues values = new ContentValues();
         values.put("fileName", image.getFileName());
         values.put("userId", image.getUserId());
         values.put("imageUri", image.getImageUri());
         values.put("mimeType", image.getMimeType());
         values.put("width", image.getWidth());
         values.put("height", image.getHeight());
         values.put("size", image.getSize());
         values.put("dateAdded", image.getDateAdded());
         values.put("status", image.getStatus());
         values.put("tags", image.getTags());

         db.update(
            "Image",
            values,
            "id = ?",
            new String[] { String.valueOf(image.getId()) }
         );
      } finally {
         db.close();
      }
   }
}
