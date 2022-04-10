package com.example.szakdolg;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView contsRecView;
    private FirebaseConnect firebaseConnect= FirebaseConnect.getInstance("firebase");
    private SQLConnect sqlConnect = SQLConnect.getInstance("sql", firebaseConnect.getUserId());
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

        // Toolbar settings
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        this.setTitle("Contacts");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        initView();




    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        contacts = new ArrayList<>();
        contacts = sqlConnect.getContacts();
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