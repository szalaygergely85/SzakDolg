package com.zen_vy.chat.activity.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.zen_vy.chat.DTO.ContactsDTO;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.base.BaseActivity;
import com.zen_vy.chat.activity.chat.activity.ChatActivity;
import com.zen_vy.chat.activity.contacts.adapter.ContactsAdapter;
import com.zen_vy.chat.activity.contacts.constans.ContactsConstans;
import com.zen_vy.chat.activity.main.MainActivity;
import com.zen_vy.chat.constans.IntentConstants;
import com.zen_vy.chat.constans.SharedPreferencesConstants;
import com.zen_vy.chat.models.contacts.ContactService;
import com.zen_vy.chat.models.contacts.dto.ConversationDTO;
import com.zen_vy.chat.models.conversation.service.ConversationService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ContactsActivity extends BaseActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contacts);

      actionId = getIntent().getStringExtra(IntentConstants.CONTACTS_ACTION);

      _initView();
      contactsAdapter = new ContactsAdapter(this, currentUser, actionId);
      contactService = new ContactService(this, currentUser);
      conversationService = new ConversationService(this, currentUser);

      toolbar.setNavigationOnClickListener(v ->
         getOnBackPressedDispatcher().onBackPressed()
      );
      RelativeLayout.LayoutParams params =
         (RelativeLayout.LayoutParams) btnNewContact.getLayoutParams();
      if (Objects.equals(actionId, ContactsConstans.ACTION_SELECT)) {
         bottomNav.setVisibility(View.GONE);

         params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
         params.removeRule(RelativeLayout.ABOVE);
      } else {
         params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
         params.addRule(RelativeLayout.ABOVE, R.id.bottom_nav_contacts);

         bottomNav.setSelectedItemId(R.id.nav_contact_main);

         bottomNav.setOnItemSelectedListener(
            new NavigationBarView.OnItemSelectedListener() {
               @Override
               public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                  Intent intent;
                  switch (item.getItemId()) {
                     case R.id.nav_messages_main:
                        intent =
                        new Intent(ContactsActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                  }
                  return false;
               }
            }
         );
      }

      btnNewContact.setLayoutParams(params);
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

               runOnUiThread(() -> {
                  contactsAdapter.setUsers(sortedList);
               });
            }

            @Override
            public void onError(Throwable t) {}
         }
      );

      selectContactsRecView.setAdapter(contactsAdapter);
      selectContactsRecView.setLayoutManager(new LinearLayoutManager(this));

      toolbar.setOnMenuItemClickListener(
         new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
               switch (item.getItemId()) {
                  case R.id.menu_start_conversation:
                     if (!contactsAdapter.getSelectedUsers().isEmpty()) {
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
                     }
                     return true; // Indicate that the click was handled
                  default:
                     return false;
               }
            }
         }
      );

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

   private void _initView() {
      selectContactsRecView = findViewById(R.id.selectContactsRecView);
      btnNewContact = findViewById(R.id.btnConNew);

      bottomNav = findViewById(R.id.bottom_nav_contacts);

      toolbar = findViewById(R.id.select_contacts_Toolbar);
   }

   private BottomNavigationView bottomNav;
   private ContactService contactService;

   private ConversationService conversationService;
   private ContactsAdapter contactsAdapter;
   private RecyclerView selectContactsRecView;

   private String actionId;

   private MaterialToolbar toolbar;

   private FloatingActionButton btnNewContact;

   private List<Long> contacts = new ArrayList<>();
}
