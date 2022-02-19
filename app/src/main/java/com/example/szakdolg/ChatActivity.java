package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecView;
    private Button btnSend;
    private EditText edtMess;
    private FirebaseConnect fireBase = new FirebaseConnect();;
    private SQLConnect sqlConnect = new SQLConnect();
    private String uID;
    ArrayList<Chat> chat;


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


        chat = new ArrayList<>();

        chat= sqlConnect.getMessgesSQL(uID);
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
            chat.add(fireBase.sendMessage(uID, edtMess.getText().toString()));
        }
    });


    }
}