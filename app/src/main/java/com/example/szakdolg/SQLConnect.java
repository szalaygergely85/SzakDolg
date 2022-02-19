package com.example.szakdolg;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

public class SQLConnect {
    private SQLiteDatabase mydatabase;

    public SQLConnect() {
        try {
            // Create or connect to sql database
            mydatabase = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.szakdolg/databases/szakD.db", null);
            //mydatabase.execSQL("DROP TABLE Contacts");
            //mydatabase.execSQL("DROP TABLE Messages");

            //create tables;
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Contacts(userId VARCHAR, userName VARCHAR, userEmail VARCHAR, userPhone VARCHAR);");
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Messages(Messageid INT, Fr VARCHAR, ToID VARCHAR, Text VARCHAR, Read BOOLEAN);");

        } catch (Exception e) {
            Log.e("SQL", e.toString());
        }
    }
    /**
     * Get Contacts from SQLite
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
     * @param cont
     */
    public void addContactSQLite(Map<String, Object> cont){
        try {
            mydatabase.execSQL("INSERT INTO Contacts VALUES('" + cont.get("uID").toString() + "', '" + cont.get("uName").toString() + "', '" + cont.get("uEmail").toString() + "' , '" + cont.get("uPhone").toString() + "');");
        } catch (SQLException e) {
            Log.e("SQL", e.toString());
        }
    }
    /**
     * Add a contact into SQL contacts table
     * @param uID
     * @param uName
     * @param uEmail
     * @param uPhone
     */
    public void addContactSQLite(String uID, String uName, String uEmail, String uPhone){
        try {
            mydatabase.execSQL("INSERT INTO Contacts VALUES('" + uID + "', '" + uName + "', '" + uEmail + "' , '" + uPhone + "');");
        } catch (SQLException e) {
            Log.e("SQL", e.toString());
        }

    }
    /**
     * Get newes message from each person
     * @return messages
     */
    public ArrayList<MessageB> getLastMessageEachPersonSQL() {
        ArrayList<MessageB> messages = new ArrayList<>();
        try {
            Cursor resultSet = mydatabase.rawQuery("SELECT Text, ToID, Fr, max(Messageid) FROM Messages WHERE Fr=(SELECT DISTINCT(userId) FROM Contacts) OR ToID=(SELECT DISTINCT(userId) FROM Contacts) GROUP BY Fr, ToID", null);
            Log.d("SQL", "" + resultSet.getCount());
            if (resultSet.moveToFirst()) {
                do {
                    String Text = resultSet.getString(0);
                    String To = resultSet.getString(1);
                    messages.add(new MessageB(Text, To, "https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg?cs=srgb&dl=pexels-mohamed-abdelghaffar-771742.jpg"));
                } while (resultSet.moveToNext());
            }
        } catch (SQLException e) {
            Log.e("SQL", e.toString());
        }
        return messages;
    }

    /**
     * Gett all message from UID
     *
     * @param frUiD
     * @return message
     */
    public ArrayList<Chat> getMessgesSQL(String frUiD) {
        ArrayList<Chat> message = new ArrayList<>();
        Cursor result = mydatabase.rawQuery("SELECT Messages.Fr, Messages.ToID, Messages.Text FROM Messages WHERE Messages.Fr='" + frUiD + "' OR Messages.ToID='" + frUiD + "'", null);

        if (result.moveToFirst()) {
            do {
                String fr = result.getString(0);
                String to = result.getString(1);
                String mess = result.getString(2);
                message.add(new Chat(fr, mess));
            } while (result.moveToNext());
        }
        return message;
    }

    /**
     * Instert one message into sql table
     * @param message
     */
    public void sendMessageSql(Map<String, Object> message){
        try {
            mydatabase.execSQL("INSERT INTO Messages VALUES('" + message.get("time") + "', '" + message.get("from") + "', '" + message.get("to") + "' , '" + message.get("message") + "', 'false');");
        } catch (SQLException e) {
            Log.e("SQL", e.toString());
        }

    }


}
