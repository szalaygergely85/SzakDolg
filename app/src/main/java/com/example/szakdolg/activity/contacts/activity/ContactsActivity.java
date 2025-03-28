package com.example.szakdolg.activity.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.DTO.ContactsDTO;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.contacts.adapter.ContactsAdapter;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.contacts.Contact;
import com.example.szakdolg.models.contacts.ContactService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ContactsActivity extends BaseActivity {

   private RecyclerView contsRecView;

   private FloatingActionButton btnNewContact;

   private ContactService contactService;

   private ContactsAdapter contactsAdapter;

   private void initView() {
      btnNewContact = findViewById(R.id.btnConNew);
      contsRecView = findViewById(R.id.recViewContacts);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contacts);

      contactService = new ContactService(this, currentUser);
      contactsAdapter = new ContactsAdapter(this, currentUser);


      // Toolbar settings
      Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(mToolbar);

      this.setTitle("Contacts");

      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
         actionBar.setDisplayHomeAsUpEnabled(true);
      }

      initView();
   }

   @Override
   public boolean onSupportNavigateUp() {
      getOnBackPressedDispatcher().onBackPressed();
    return true;
   }


   @Override
   protected void onStart() {
      super.onStart();
      contactService.getContacts(currentUser.getToken(), null, new ContactService.ContactCallback<List<ContactsDTO>>() {
         @Override
         public void onSuccess(List<ContactsDTO> data) {
            //contactsAdapter.setContact(data);
         }

         @Override
         public void onError(Throwable t) {

         }
      });

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
