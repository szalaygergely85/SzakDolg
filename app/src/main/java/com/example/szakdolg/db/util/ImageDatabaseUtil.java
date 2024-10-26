package com.example.szakdolg.db.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.szakdolg.db.helper.DatabaseHelper;
import com.example.szakdolg.model.image.entity.ImageEntity;
import com.example.szakdolg.model.user.model.User;

public class ImageDatabaseUtil {
    private DatabaseHelper dbHelper;

    public ImageDatabaseUtil(Context context, User user) {
        dbHelper = new DatabaseHelper(context, user.getUserId().toString());
    }


    public void deleteImageEntityByUuid(String uuid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete("Image", "uuid = ?", new String[]{uuid});
        } finally {
            db.close();
        }
    }

    public ImageEntity getImageEntityByUuid(String uuid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ImageEntity imageEntity = null;

        try (Cursor cursor = db.query(
                "Image", // Table name
                null,    // Select all columns
                "uuid = ?", // WHERE clause
                new String[]{uuid}, // WHERE arguments
                null,    // Group by
                null,    // Having
                null     // Order by
        )) {

            if (cursor != null && cursor.moveToFirst()) {
                imageEntity = new ImageEntity();
                imageEntity.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                imageEntity.setFileName(cursor.getString(cursor.getColumnIndexOrThrow("fileName")));
                imageEntity.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow("userId")));
                imageEntity.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow("imageUri")));
                imageEntity.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow("mimeType")));
                imageEntity.setWidth(cursor.getInt(cursor.getColumnIndexOrThrow("width")));
                imageEntity.setHeight(cursor.getInt(cursor.getColumnIndexOrThrow("height")));
                imageEntity.setSize(cursor.getLong(cursor.getColumnIndexOrThrow("size")));
                imageEntity.setDateAdded(cursor.getLong(cursor.getColumnIndexOrThrow("dateAdded")));
                imageEntity.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                imageEntity.setTags(cursor.getString(cursor.getColumnIndexOrThrow("tags")));
                imageEntity.setUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
            }
        } finally {
            db.close();
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

            db.update("Image", values, "id = ?", new String[]{String.valueOf(image.getId())});
        } finally {
            db.close();
        }
    }


}
