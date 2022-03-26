package com.example.szakdolg;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SQLConnect {
    private SQLiteDatabase mydatabase;
    private boolean privBoolean = false;
    private String name;
    private static SQLConnect instance;

    //Singleton
    public static synchronized SQLConnect getInstance(String name){
        if(instance==null){
            instance = new SQLConnect(name);
            return instance;
        }else{
            return instance;
        }
    }

    private SQLConnect(String name) {
        this.name = name;

        try {

            File dbpath = (new File("/data/data/com.example.szakdolg/databases/data.db"));
            if (!(new File(dbpath.getParent()).exists())) {
                new File(dbpath.getParent()).mkdirs();
            }
            mydatabase = SQLiteDatabase.openOrCreateDatabase(dbpath, null);
            // Create or connect to sql database
            // mydatabase.execSQL("DROP TABLE Keys");
            // mydatabase.execSQL("DROP TABLE Contacts");
            // mydatabase.execSQL("DROP TABLE Messages");
            //create tables;
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Contacts(userId VARCHAR, userName VARCHAR, userEmail VARCHAR, userPhone VARCHAR);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Messages(Messageid INT, Contact VARCHAR, Text VARCHAR, IsFrMe INT, Read BOOLEAN, Uploaded BOOLEAN);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Keys(userId VARCHAR, Private VARCHAR, Public VARCHAR, PublicExt VARCHAR);");

        } catch (Exception e) {
            Log.e("SQL", e.toString());
        }
    }


    public boolean isNotUploadedMessage() {
        Cursor result = mydatabase.rawQuery("Select * from Messages Where Uploaded='false'", null);

        if (result.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public void setMessageToUploaded(String mID) {
        mydatabase.execSQL("UPDATE Messages SET Uploaded='true' WHERE Messageid = '" + mID + "';");

    }

    public void updatePublicExtKey(String uID, String pubExtKey) {
        if (!isKey(uID)) {
            Log.d("Crypt", "Didnt find keys, generating one ");
            generateKeys(uID);
        }
        mydatabase.execSQL("UPDATE Keys SET PublicExt='" + pubExtKey + "' Where userId='" + uID + "';");
    }

    public void generateKeys(String uID) {
        HashMap<String, String> keys = Crypt.createKeys();
        try {
            mydatabase.execSQL("INSERT INTO Keys VALUES('" + uID + "', '" + keys.get("Private") + "', '" + keys.get("Public") + "', NULL);");
            Log.d("Crypt", "generated: " + keys.get("Public"));
        } catch (SQLException e) {
            Log.e("SQL", e.toString());
        }
    }

    public boolean isKey(String uID) {
        Cursor result = mydatabase.rawQuery("Select * from Keys Where userId='" + uID + "'", null);

        if (result.moveToFirst()) {
            return true;
        } else {
            return false;

        }
    }

    public boolean isPubExtKey(String uID) {
        Cursor result = mydatabase.rawQuery("Select PublicExt from Keys Where userId='" + uID + "'", null);
        if (result.moveToFirst()) {
            privBoolean = true;
            Log.d("Crypt", "true");
        } else {
            Log.d("Crypt", "Didnt find the key");
        }
        return privBoolean;
    }

    public String getPrivateKey(String uID) {
        String search = null;
        Cursor result = mydatabase.rawQuery("Select Keys.Private from Keys Where userId='" + uID + "'", null);

        if (result.moveToFirst()) {
            search = result.getString(0);
        }
        return search;
    }

    public String getPublicKey(String uID) {
        String search = null;
        Cursor result = mydatabase.rawQuery("Select Keys.Public from Keys Where userId='" + uID + "'", null);

        if (result.moveToFirst()) {
            search = result.getString(0);
        }
        return search;
    }

    public String getPublicExtKey(String uID) {
        String search = null;
        Cursor result = mydatabase.rawQuery("Select Keys.PublicExt from Keys Where userId='" + uID + "'", null);

        if (result.moveToFirst()) {
            search = result.getString(0);
        }
        return search;
    }

    /**
     * Get Contacts from SQLite
     *
     * @return ArrayList<Contact>
     */
    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor result = mydatabase.rawQuery("Select Contacts.userID, Contacts.userName, Contacts.userEmail, Contacts.userPhone from Contacts", null);

        if (result.moveToFirst()) {
            do {
                String id = result.getString(0);
                String name = result.getString(1);
                String email = result.getString(2);
                String phone = result.getString(3);
                contacts.add(new Contact(id, name, email, phone));
            } while (result.moveToNext());
        }
        return contacts;
    }

    /**
     * Add a contact to SQLite
     *
     * @param cont
     */
    public void addContactSQLite(Map<String, Object> cont) {
        try {
            mydatabase.execSQL("INSERT INTO Contacts VALUES('" + cont.get("uID").toString() + "', '" + cont.get("uName").toString() + "', '" + cont.get("uEmail").toString() + "' , '" + cont.get("uPhone").toString() + "');");
        } catch (SQLException e) {
            Log.e("SQL", e.toString());
        }
    }

    /**
     * Add a contact into SQL contacts table
     *
     * @param uID
     * @param uName
     * @param uEmail
     * @param uPhone
     */
    public void addContactSQLite(String uID, String uName, String uEmail, String uPhone) {
        try {
            mydatabase.execSQL("INSERT INTO Contacts VALUES('" + uID + "', '" + uName + "', '" + uEmail + "' , '" + uPhone + "');");
        } catch (SQLException e) {
            Log.e("SQL", e.toString());
        }

    }

    /**
     * Get newest message from each person
     *
     * @return messages
     */
    public ArrayList<MessageB> getLastMessageEachPersonSQL(String userID) {

        ArrayList<MessageB> messages = new ArrayList<>();
        try {
            Cursor resultSet = mydatabase.rawQuery("SELECT Messages.Messageid, Messages.Text, Contacts.userId, Messages.Read, Contacts.userName FROM Contacts LEFT JOIN Messages ON Messages.Contact=Contacts.userId GROUP BY Contacts.userName", null);
            // Log.d("SQL", "" + resultSet.getCount());
            if (resultSet.moveToFirst()) {
                do {
                    String messageId = resultSet.getString(0);
                    String text = resultSet.getString(1);
                    String contact = resultSet.getString(2);
                    boolean read;
                    if (1 == resultSet.getInt(3)) {
                        read = true;
                    } else {
                        read = false;
                    }

                    String username = resultSet.getString(4);

                    messages.add(new MessageB(messageId, contact, username, text, read, "https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg?cs=srgb&dl=pexels-mohamed-abdelghaffar-771742.jpg"));
                } while (resultSet.moveToNext());
            }
        } catch (SQLException e) {
            Log.e("SQL", e.toString());
        }
        return messages;
    }

    /**
     * Get all message from UID
     *
     * @param frUiD
     * @return ArrayList<Chat>
     */
    public ArrayList<Chat> getMessagesSQL(String frUiD) {
        ArrayList<Chat> message = new ArrayList<>();
        Cursor result = mydatabase.rawQuery("SELECT Messages.Messageid, Messages.Contact, Messages.Text, Messages.IsFrMe FROM Messages WHERE Messages.Contact='" + frUiD + "' ORDER BY Messages.Messageid", null);
        Log.d("Chat", " " + result.getCount());
        if (result.moveToFirst()) {
            int i = 0;
            do {
                String id = result.getString(0);
                String contact = result.getString(1);
                String mess = result.getString(2);
                Log.d("Chat", " " + result.getInt(3));
                int isFrMe = result.getInt(3);

                message.add(new Chat(id, contact, mess, isFrMe, false, false));
                Log.d("Chat", message.get(i).toString());
                i++;
            } while (result.moveToNext());
        }
        return message;
    }

    public ArrayList<Chat> getMessagesNOTUploaded() {
        ArrayList<Chat> message = new ArrayList<>();
        Cursor result = mydatabase.rawQuery("SELECT Messages.Messageid, Messages.Contact, Messages.Text, Messages.IsFrMe FROM Messages Where Uploaded='false'", null);

        if (result.moveToFirst()) {

            int i = 0;
            do {
                String id = result.getString(0);
                String contact = result.getString(1);
                String mess = result.getString(2);

                int isFrMe = result.getInt(3);

                message.add(new Chat(id, contact, mess, isFrMe, false, false));
            } while (result.moveToNext());
        }
        return message;
    }

    /**
     * Instert one message into sql table
     *
     * @param message
     */
    public void addMessageSql(Chat message) {

        try {

            mydatabase.execSQL("INSERT INTO Messages VALUES('" + message.getId() + "', '" + message.getContact() + "', '" + message.getMessage() + "' , '" + message.isFromMe() + "', 'false', 'false');");
            Log.e("SQL", message.getMessage());
        } catch (SQLException e) {
            Log.e("SQL", e.toString());
        }

    }

    public String getNameFrContact(String uID) {
        String fr = new String();
        Cursor result = mydatabase.rawQuery("SELECT userName FROM Contacts WHERE userId='" + uID + "'", null);

        if (result.moveToFirst()) {
            fr = result.getString(0);
        }
        return fr;
    }

    public boolean isInContracts(String uID) {
        Cursor result = mydatabase.rawQuery("SELECT * FROM Contacts WHERE userId='" + uID + "'", null);
        if (result.moveToFirst()) {
            return true;

        } else {
            return false;
        }

    }


}
