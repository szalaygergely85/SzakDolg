package com.example.szakdolg.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.szakdolg.Contact;
import com.example.szakdolg.DTO.MessageBoard;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.contacts.ContactsApiService;
import com.example.szakdolg.message.MessageApiService;
import com.example.szakdolg.recviewadapter.ContactsAdapter;
import com.example.szakdolg.FirebaseConnect;
import com.example.szakdolg.R;
import com.example.szakdolg.SQLConnect;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.User;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView contsRecView;

    private final String TAG = "ContactsActivity";

    private User user;

    List<User> contactList;
    private FloatingActionButton btnNewContact;

    private void initView() {
        btnNewContact = findViewById(R.id.btnConNew);
        contsRecView = findViewById(R.id.recViewContacts);
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
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        user = (User) this.getIntent().getSerializableExtra("logged_user");


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
        contactList = new ArrayList<>();

        ContactsAdapter contactsAdapter = new ContactsAdapter(this, user);
        contactsAdapter.setContact(contactList);

        ContactsApiService contactsApiService = RetrofitClient.getRetrofitInstance().create(ContactsApiService.class);

        Call<List<User>> contactsCall= contactsApiService.getConversation(SharedPreferencesUtil.getStringPreference(this, SharedPreferencesConstans.USERTOKEN));

        contactsCall.enqueue(new Callback<List<User>>(){
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, ""+response.code());

                    List<User> contactList = response.body();
                    contactsAdapter.setContact(contactList);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e(TAG, ""+t.getMessage());
            }
        });




        contsRecView.setAdapter(contactsAdapter);
        contsRecView.setLayoutManager(new LinearLayoutManager(this));
        btnNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsActivity.this, SearchContactsActivity.class);
                intent.putExtra("logged_user", user);
                startActivity(intent);
            }
        });
    }
}