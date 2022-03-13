package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView contsRecView;
    private FirebaseConnect firebaseConnect;
    private SQLConnect sqlConnect;
    ArrayList<Contact> contacts;
    private FloatingActionButton btnNewContact;

    private void initView(){
        btnNewContact = findViewById(R.id.btnConNew);
        contsRecView =findViewById(R.id.recViewContacts);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        initView();
        firebaseConnect = new FirebaseConnect();
        sqlConnect = new SQLConnect(firebaseConnect.getUserId());

        contacts = new ArrayList<>();
        contacts = sqlConnect.getContacts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ContactsAdapter contactsAdapter = new ContactsAdapter(this);
        contactsAdapter.setContact(contacts);
        contsRecView.setAdapter(contactsAdapter);
        contsRecView.setLayoutManager(new LinearLayoutManager(this));
        btnNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsActivity.this, SearchContactsActivity.class);
                startActivity(intent);
            }
        });
    }

}