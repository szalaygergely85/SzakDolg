package com.example.szakdolg.activity.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.DTO.ConversationDTO;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.chat.activity.ChatActivity;
import com.example.szakdolg.activity.contacts.adapter.SelectContactsAdapter;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.models.contacts.Contact;
import com.example.szakdolg.models.contacts.ContactService;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.service.ConversationService;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class SelectContactsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);

        _initView();
        selectContactsAdapter = new SelectContactsAdapter(this, currentUser, contacts);
        contactService = new ContactService(this, currentUser);
        conversationService = new ConversationService(this, currentUser);


    }

    @Override
    protected void onStart() {
        super.onStart();

        contactService.getContacts(currentUser.getToken(), null, new ContactService.ContactCallback<List<Contact>>() {
            @Override
            public void onSuccess(List<Contact> data) {
                selectContactsAdapter.setContacts(data);
            }

            @Override
            public void onError(Throwable t) {

            }
        });

        selectContactsRecView.setAdapter(selectContactsAdapter);
        selectContactsRecView.setLayoutManager(new LinearLayoutManager(this));

        topAppBar.setOnMenuItemClickListener(
                new MaterialToolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_start_conversation:
                                contacts.add(currentUser.getUserId());
                                conversationService.addConversationByUserId(contacts, new ConversationService.ConversationCallback<ConversationDTO>() {
                                    @Override
                                    public void onSuccess(ConversationDTO data) {
                                        Intent intent = new Intent(
                                                SelectContactsActivity.this,
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
                                    public void onError(Throwable t) {

                                    }
                                });


                                return true; // Indicate that the click was handled
                            default:
                                return false;
                        }
                    }
                }
        );


    }

    private void _initView() {
        selectContactsRecView = findViewById(R.id.selectContactsRecView);
        topAppBar = findViewById(R.id.select_contacts_Toolbar);
    }
    private ContactService contactService;

    private ConversationService conversationService;
    private SelectContactsAdapter selectContactsAdapter;
    private RecyclerView selectContactsRecView;
    private MaterialToolbar topAppBar;

    private List<Long> contacts = new ArrayList<>();
}