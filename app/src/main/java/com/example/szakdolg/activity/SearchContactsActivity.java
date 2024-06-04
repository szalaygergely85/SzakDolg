package com.example.szakdolg.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.Contact;
import com.example.szakdolg.R;
import com.example.szakdolg.recviewadapter.SearchContactAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchContactsActivity extends AppCompatActivity {
    private EditText search;
    private RecyclerView contsRecView;
    private ArrayList<Contact> contacts;
    private FirebaseFirestore db;
    private static final String TAG = "SearchContactsActivity";

    private void initViews() {
        search = findViewById(R.id.edtContSearch);
        contsRecView = findViewById(R.id.recvContactSearch);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.contactsearchBoardToolbar);
        setSupportActionBar(mToolbar);
        //toolbar settings

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("People Search");

        initViews();

        db = FirebaseFirestore.getInstance();
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
                Log.d(TAG, "onTextChanged: at start" + contacts.size());
                if (search.getText().toString().length() > 2) {
                    db.collection("Users").orderBy("email").startAt(search.getText().toString()).endAt(search.getText().toString() + "\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    contacts.add(new Contact(document.get("userID").toString(), document.get("name").toString(), document.get("email").toString(), document.get("phone").toString()));
                                    contacts = removeDuplicates(contacts);
                                }
                                Log.d(TAG, "onComplete:orderBy(\"email\")" + contacts.size());
                            }
                        }

                    });
                    db.collection("Users").orderBy("name").startAt(search.getText().toString()).endAt(search.getText().toString() + "\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    contacts.add(new Contact(document.get("userID").toString(), document.get("name").toString(), document.get("email").toString(), document.get("phone").toString()));
                                    contacts = removeDuplicates(contacts);

                                }
                                Log.d(TAG, "onComplete:orderBy(\"name\")" + contacts.size());
                            }
                        }
                    });
                    db.addSnapshotsInSyncListener(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: Snapshot sync is running");
                            contacts = removeDuplicates(contacts);
                            contactsAdapter.setContact(contacts);
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public static ArrayList<Contact> removeDuplicates(ArrayList<Contact> list) {
        boolean isFound;
        ArrayList<Contact> newList = new ArrayList<>();

        for (Contact element : list) {
            isFound = false;
            Log.d(TAG, "element: " + element);
            for (Contact e : newList) {
                if (e.getID().equals(element.getID())) {
                    isFound = true;
                }
            }
            if (!isFound) {
                newList.add(element);
            }
        }
        return newList;
    }

}