package com.example.szakdolg;

import android.database.sqlite.SQLiteDatabase;

public class SqlConnect {
    public SqlConnect(){
        SQLiteDatabase mydatabase = SQLiteDatabase.openOrCreateDatabase("MESSENGER", null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS MyContacts(Userid VARCHAR, Username VARCHAR);");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Messages(Messageid INT, Fr VARCHAR, ToID VARCHAR, FRName VARCHAR, Text VARCHAR, Read BOOLEAN);");
    }



}
