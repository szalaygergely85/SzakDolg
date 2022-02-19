package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecView;
    private Button btnSend;
    private EditText edtMess;
     FirebaseConnect fireBase = new FirebaseConnect();;
    private SQLConnect sqlConnect = new SQLConnect();
    private String uID;
    ChatAdapter adapter;
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
        //DownloadAsynctask downloadAsynctask = new DownloadAsynctask();

        uID = (String) this.getIntent().getSerializableExtra("uID");
        Log.d("test", uID);


        chat = new ArrayList<>();

        chat= sqlConnect.getMessgesSQL(uID);
        adapter = new ChatAdapter();
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
                edtMess.getText().clear();
                adapter.notifyDataSetChanged();
            }
        });


    }
    public class DownloadAsynctask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            for (int i =0; i<100; i++){
                fireBase.downloadMessages();
                Log.d("test", "doInBackground: " );
                SystemClock.sleep(30000);


            }
            return null;
        }
    }

}