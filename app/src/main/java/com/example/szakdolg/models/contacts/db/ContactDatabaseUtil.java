package com.example.szakdolg.models.contacts.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.szakdolg.db.DatabaseHelper;
import com.example.szakdolg.models.contacts.Contact;
import com.example.szakdolg.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class ContactDatabaseUtil {

   private final DatabaseHelper dbHelper; // To fetch User details

   public ContactDatabaseUtil(Context context, User currentUser) {
      this.dbHelper =
      DatabaseHelper.getInstance(context, currentUser.getUserId().toString());
   }

   public void insertContact(Contact contact) {
      try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
         ContentValues values = new ContentValues();
         values.put("contactId", contact.getContactId());
         values.put("ownerId", contact.getOwnerId());
         values.put("contactUserId", contact.getContactUserId());

         db.insertWithOnConflict(
            dbHelper.TABLE_CONTACT,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
         );
      }
   }

   public void deleteContact(Long contactId) {
      try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
         db.delete(
            "Contact",
            "contactId = ?",
            new String[] { String.valueOf(contactId) }
         );
      }
   }

   public void updateContact(Contact contact) {
      try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
         ContentValues values = new ContentValues();
         values.put("ownerId", contact.getOwnerId());
         values.put("contactUserId", contact.getContactUserId());

         db.update(
            "Contact",
            values,
            "contactId = ?",
            new String[] { String.valueOf(contact.getContactId()) }
         );
      }
   }

   public Contact getContact(Long contactId) {
      Contact contact = null;
      String query = "SELECT * FROM Contact WHERE contactId = ?";
      String[] args = { String.valueOf(contactId) };

      try (
         SQLiteDatabase db = dbHelper.getReadableDatabase();
         Cursor cursor = db.rawQuery(query, args)
      ) {
         if (cursor.moveToFirst()) { // Only fetch one contact
            Long ownerId = cursor.getLong(
               cursor.getColumnIndexOrThrow("ownerId")
            );
            Long contactUserId = cursor.getLong(
               cursor.getColumnIndexOrThrow("contactUserId")
            );

            contact = new Contact(contactId, ownerId, contactUserId);
         }
      }

      return contact; // Return the found contact or null if not found
   }

   public List<Contact> getContacts(String searchQuery) {
      List<Contact> contacts = new ArrayList<>();
      String query = "SELECT * FROM Contact";
      String[] args = null;

      if (searchQuery != null && !searchQuery.isEmpty()) {
         query += " WHERE contactUserId LIKE ?";
         args = new String[] { "%" + searchQuery + "%" };
      }

      try (
         SQLiteDatabase db = dbHelper.getReadableDatabase();
         Cursor cursor = db.rawQuery(query, args)
      ) {
         while (cursor.moveToNext()) {
            Long contactId = cursor.getLong(
               cursor.getColumnIndexOrThrow("contactId")
            );
            Long ownerId = cursor.getLong(
               cursor.getColumnIndexOrThrow("ownerId")
            );
            Long contactUserId = cursor.getLong(
               cursor.getColumnIndexOrThrow("contactUserId")
            );

            contacts.add(new Contact(contactId, ownerId, contactUserId));
         }
      }

      return contacts;
   }

   public void insertContacts(List<Contact> contacts) {
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      try {
         db.beginTransaction();
         for (Contact contact : contacts) {
            ContentValues values = new ContentValues();
            values.put("contactId", contact.getContactId());
            values.put("ownerId", contact.getOwnerId());
            values.put("contactUserId", contact.getContactUserId());
            db.insertWithOnConflict(
               "Contact",
               null,
               values,
               SQLiteDatabase.CONFLICT_REPLACE
            );
         }
         db.setTransactionSuccessful();
      } finally {
         db.endTransaction();
         db.close();
      }
   }
}
