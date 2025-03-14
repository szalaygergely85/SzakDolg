package com.example.szakdolg.activity.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.chat.activity.ChatActivity;
import com.example.szakdolg.activity.contacts.adapter.SelectContactsAdapter;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.contacts.Contact;
import com.example.szakdolg.models.contacts.ContactService;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class SelectContactsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);

        _initView();
        selectContactsAdapter = new SelectContactsAdapter(this, currentUser);
        contactService = new ContactService(this, currentUser);

    }

    @Override
    protected void onStart() {
        super.onStart();

        contactService.getContacts(currentUser.getAuthToken(), null, new ContactService.ContactCallback<List<Contact>>() {
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
                            case R.id.menuNewMain:
                                Intent intent = new Intent(
                                        SelectContactsActivity.this,
                                        ChatActivity.class
                                );
                                intent.putExtra(
                                        SharedPreferencesConstants.CURRENT_USER,
                                        currentUser
                                );
                                startActivity(intent);
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
    private SelectContactsAdapter selectContactsAdapter;
    private RecyclerView selectContactsRecView;
    private MaterialToolbar topAppBar;
}