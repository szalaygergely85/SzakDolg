package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EdgeEffect;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchContactsActivity extends AppCompatActivity {
    private EditText search;
    private RecyclerView contsRecView;
    private DataBaseConnector database;
    ArrayList<Contact> contacts;
    private FirebaseFirestore db;

    private void initViews(){
        search = findViewById(R.id.edtContSearch);
        contsRecView =findViewById(R.id.recvContactSearch);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);
        initViews();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        contacts = new ArrayList<>();
        SearchContactAdapter contactsAdapter = new SearchContactAdapter(this);
        contactsAdapter.setContact(contacts);
        contsRecView.setAdapter(contactsAdapter);
        contsRecView.setLayoutManager(new LinearLayoutManager(this));
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(search.getText().toString().length()>3){
                    //TODO search continue

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}