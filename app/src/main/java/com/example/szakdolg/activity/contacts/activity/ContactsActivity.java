package com.example.szakdolg.activity.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.DTO.ContactsDTO;
import com.example.szakdolg.DTO.ConversationDTO;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.chat.activity.ChatActivity;
import com.example.szakdolg.activity.contacts.adapter.ContactsAdapter;
import com.example.szakdolg.activity.contacts.constans.ContactsConstans;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.contacts.ContactService;
import com.example.szakdolg.models.conversation.service.ConversationService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactsActivity extends BaseActivity {

   private String actionId;

   private MaterialToolbar toolbar;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contacts);

      actionId = getIntent().getStringExtra(IntentConstants.CONTACTS_ACTION);

      _initView();
      contactsAdapter = new ContactsAdapter(this, currentUser, actionId);
      contactService = new ContactService(this, currentUser);
      conversationService = new ConversationService(this, currentUser);

      toolbar = findViewById(R.id.select_contacts_Toolbar);

      toolbar.setNavigationOnClickListener(v ->
         getOnBackPressedDispatcher().onBackPressed()
      );
   }

   @Override
   protected void onStart() {
      super.onStart();

      if (actionId.equals(ContactsConstans.ACTION_VIEW)) {
         toolbar.getMenu().clear();
         toolbar.setTitle("Contacts");
      }

      contactService.getContacts(
         currentUser.getToken(),
         null,
         new ContactService.ContactCallback<List<ContactsDTO>>() {
            @Override
            public void onSuccess(List<ContactsDTO> contactsDTOS) {
               Collections.sort(
                  contactsDTOS,
                  new Comparator<ContactsDTO>() {
                     @Override
                     public int compare(
                        ContactsDTO contactsDTO,
                        ContactsDTO t1
                     ) {
                        return contactsDTO
                           .getUser()
                           .getDisplayName()
                           .compareToIgnoreCase(t1.getUser().getDisplayName());
                     }
                  }
               );
               List<Object> sortedList = new ArrayList<>();
               char lastHeader = 0;

               for (ContactsDTO contactsDTO : contactsDTOS) {
                  char firstLetter = contactsDTO
                     .getUser()
                     .getDisplayName()
                     .charAt(0);
                  if (firstLetter != lastHeader) {
                     sortedList.add(String.valueOf(firstLetter)); // Add header
                     lastHeader = firstLetter;
                  }
                  sortedList.add(contactsDTO);
               }

               contactsAdapter.setUsers(sortedList);
            }

            @Override
            public void onError(Throwable t) {}
         }
      );

      selectContactsRecView.setAdapter(contactsAdapter);
      selectContactsRecView.setLayoutManager(new LinearLayoutManager(this));

      topAppBar.setOnMenuItemClickListener(
         new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
               switch (item.getItemId()) {
                  case R.id.menu_start_conversation:
                     conversationService.addConversationByUserId(
                        contactsAdapter.getSelectedUsers(),
                        new ConversationService.ConversationCallback<
                           ConversationDTO
                        >() {
                           @Override
                           public void onSuccess(ConversationDTO data) {
                              Intent intent = new Intent(
                                 ContactsActivity.this,
                                 ChatActivity.class
                              );
                              intent.putExtra(
                                 IntentConstants.CONVERSATION_ID,
                                 data.getConversation().getConversationId()
                              );
                              intent.putExtra(
                                 IntentConstants.CONVERSATION_DTO,
                                 data
                              );
                              startActivity(intent);
                           }

                           @Override
                           public void onError(Throwable t) {}
                        }
                     );

                     return true; // Indicate that the click was handled
                  default:
                     return false;
               }
            }
         }
      );

      if (actionId.equals(ContactsConstans.ACTION_VIEW)) {
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

   private void _initView() {
      selectContactsRecView = findViewById(R.id.selectContactsRecView);
      topAppBar = findViewById(R.id.select_contacts_Toolbar);
      btnNewContact = findViewById(R.id.btnConNew);
   }

   private ContactService contactService;

   private ConversationService conversationService;
   private ContactsAdapter contactsAdapter;
   private RecyclerView selectContactsRecView;
   private MaterialToolbar topAppBar;

   private FloatingActionButton btnNewContact;
   private List<Long> contacts = new ArrayList<>();
}
