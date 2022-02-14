package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView contsRecView;
    private DataBaseConnector fireBase;
    ArrayList<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        fireBase = new DataBaseConnector();
        contsRecView =findViewById(R.id.recViewContacts);
        contacts = new ArrayList<>();
       contacts = fireBase.getContacts();

        /*
        contacts.add(new Contact("Gege","szalaygergely@gmail.com", "06501061606"));
        contacts.add(new Contact("Anya","szalaygergely@gmail.com", "06501061606"));
        contacts.add(new Contact("Sasha","sz@sz.com", "06501061606"));
        */


    }

    @Override
    protected void onStart() {
        super.onStart();
        ContactsAdapter contactsAdapter = new ContactsAdapter(this);
        contactsAdapter.setContact(contacts);
        contsRecView.setAdapter(contactsAdapter);
        contsRecView.setLayoutManager(new LinearLayoutManager(this));

    }

}