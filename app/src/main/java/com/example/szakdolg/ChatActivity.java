package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecView = findViewById(R.id.chatRecView);

        ArrayList<Chat> chat = new ArrayList<>();
        chat.add(new Chat("Hello",new Date(),"Sasha"));
        chat.add(new Chat("Hello",new Date(),"Sasha"));
        chat.add(new Chat("Hello",new Date(), "Gege"));
        chat.add(new Chat("Hello",new Date(), "Gege"));
        chat.add(new Chat("How are you?", new Date(),"Sasha"));
        chat.add(new Chat("I am fine",new Date(), "Gege"));
        chat.add(new Chat("And you?",new Date(), "Gege"));
        chat.add(new Chat("I am fine",new Date(), "Gege"));
        chat.add(new Chat("And you?",new Date(), "Gege"));
        chat.add(new Chat("Thanks, me too",new Date(), "Gege"));
        chat.add(new Chat("How are you?", new Date(),"Sasha"));
        chat.add(new Chat("How are you?", new Date(),"Sasha"));
        chat.add(new Chat("How are you?", new Date(),"Sasha"));
        chat.add(new Chat("How are you?", new Date(),"Sasha"));
        chat.add(new Chat("How are you?", new Date(),"Sasha"));


        ChatAdapter adapter = new ChatAdapter();
        adapter.setChats(chat);

        chatRecView.setAdapter(adapter);
        chatRecView.setLayoutManager(new LinearLayoutManager(this));
    }
}