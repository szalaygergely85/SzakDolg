package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {
    public static final int REQUEST_READ_CONTACTS = 79;
    private RecyclerView contsRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contsRecView =findViewById(R.id.recViewContacts);


        ArrayList<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("Gege","szalaygergely@gmail.com", "06501061606"));
        contacts.add(new Contact("Anya","szalaygergely@gmail.com", "06501061606"));
        contacts.add(new Contact("Sasha","szalaygergely@gmail.com", "06501061606"));


        ContactsAdapter contactsAdapter = new ContactsAdapter(this);
        contactsAdapter.setContact(contacts);
        contsRecView.setAdapter(contactsAdapter);
        contsRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

}