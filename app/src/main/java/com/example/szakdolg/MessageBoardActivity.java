package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MessageBoardActivity extends AppCompatActivity {
    private FloatingActionButton contactsButton;
    private RecyclerView messageBoardRecView;
    private void initView(){
        contactsButton=findViewById(R.id.btnMesBrdNew);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_actvitiy);
        initView();

        messageBoardRecView= findViewById(R.id.messageBoardRecView);

        ArrayList<MessageB> messageB = new ArrayList<>();
        messageB.add(new MessageB("Gege", "ext messaging, or texting, is the act of composing and sending electronic messages, typically consisting ","https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg?cs=srgb&dl=pexels-mohamed-abdelghaffar-771742.jpg"));
        messageB.add(new MessageB("Sasha", "The term originally referred to messages sent using the Short Message Service (SMS). It has grown beyond alphanumeric text to include multimedia messages using the Multimedia Messaging Service (MMS)","https://images.unsplash.com/photo-1532074205216-d0e1f4b87368?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=741&q=80"));
        messageB.add(new MessageB("Anya","Text messages are used for personal, family, business and social purposes" , "https://st2.depositphotos.com/1531183/5770/v/950/depositphotos_57709697-stock-illustration-male-person-silhouette-profile-picture.jpg"));
        messageB.add(new MessageB("Fanny", "In 1933, the German Reichspost (Reich postal service) introduced the first telex service.[2][3]","https://support.hubstaff.com/wp-content/uploads/2019/08/good-pic.png"));
        messageB.add(new MessageB("Andras", "SMS messaging was used for the first time on 3 December 1992,[8] when Neil Papworth, a 22-year-old test engineer for Sema Group in the UK[9] (now Airwide Solutions),[10] used a personal computer to send the text message Merry Christmas via the Vodafone network to the phone of Richard Jarvis,[11][12] who was at a party in Newbury, Berkshire, which had been organized to celebrate the event. Modern SMS text messaging is usually messaging from one mobile phone to another. Finnish Radiolinja became the first network to offer a commercial person-to-person ","https://www.seekpng.com/png/full/506-5061704_cool-profile-avatar-picture-cool-picture-for-profile.png"));

        MessageBoardRecAdapter adapter =new MessageBoardRecAdapter();
        adapter.setMessageB(messageB);
        messageBoardRecView.setAdapter(adapter);
        messageBoardRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageBoardActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });
    }
}