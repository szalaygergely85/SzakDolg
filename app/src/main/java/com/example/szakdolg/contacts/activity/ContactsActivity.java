package com.example.szakdolg.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.contacts.adapter.ContactsAdapter;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.model.user.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

   private RecyclerView contsRecView;

   private final String TAG = "ContactsActivity";

   private User currentUser;

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

      currentUser =
      (User) this.getIntent()
         .getSerializableExtra(SharedPreferencesConstants.CURRENT_USER);

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

      ContactsAdapter contactsAdapter = new ContactsAdapter(this, currentUser);
      contactsAdapter.setContact(contactList);

      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
         this,
         currentUser
      );

      List<User> contacts = userDatabaseUtil.getAllUsers();
      contactsAdapter.setContact(contacts);

      contsRecView.setAdapter(contactsAdapter);
      contsRecView.setLayoutManager(new LinearLayoutManager(this));
      btnNewContact.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(
                  ContactsActivity.this,
                  SearchContactsActivity.class
               );
               intent.putExtra(
                  SharedPreferencesConstants.CURRENT_USER,
                  currentUser
               );
               startActivity(intent);
            }
         }
      );
   }
}
