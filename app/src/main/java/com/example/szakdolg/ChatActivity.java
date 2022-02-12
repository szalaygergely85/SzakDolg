package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecView;
    private Button btnSend;
    private EditText edtMess;
    private FireBaseCon fireBase;
    private String uID;


     private void initView(){
        chatRecView = findViewById(R.id.recViewChat);
        btnSend = findViewById(R.id.btnChatSend);
        edtMess = findViewById(R.id.edtChatMes);
     }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();

        uID = (String) this.getIntent().getSerializableExtra("uID");


        fireBase = new FireBaseCon();
        fireBase.getMessages(uID);



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


    @Override
    protected void onStart() {
        super.onStart();


    btnSend.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            fireBase.sendMessage(uID, edtMess.getText().toString());
        }
    });


    }
}