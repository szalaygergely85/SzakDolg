package com.example.szakdolg.activity.selectcontacts;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.main.adapter.MainAdapter;

public class SelectContactsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);

        _initView();
        selectContactsAdapter = new SelectContactsAdapter(this, currentUser);
    }

    @Override
    protected void onStart() {
        super.onStart();


        // selectContactsAdapter.setContacts();

                selectContactsRecView.setAdapter(selectContactsAdapter);
        selectContactsRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void _initView() {
        selectContactsRecView = findViewById(R.id.selectContactsRecView);
    }

    private SelectContactsAdapter selectContactsAdapter;
    private RecyclerView selectContactsRecView;
}