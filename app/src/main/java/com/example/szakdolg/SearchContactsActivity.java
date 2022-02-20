package com.example.szakdolg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchContactsActivity extends AppCompatActivity {
    private EditText search;
    private RecyclerView contsRecView;
    private FirebaseConnect database;
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
                contacts.clear();
                if(search.getText().toString().length()>2){
                    db.collection("Users").orderBy("email").startAt(search.getText().toString()).endAt(search.getText().toString()+"\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    contacts.add(new Contact(document.get("userID").toString(),document.get("name").toString(),document.get("email").toString(),document.get("phone").toString()));
                                }
                                contactsAdapter.setContact(contacts);
                            }
                        }

                    });

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}