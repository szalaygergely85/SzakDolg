package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {

    final Handler handler = new Handler();
    FirebaseConnect fireBase = new FirebaseConnect();
    ChatAdapter adapter;
    ArrayList<Chat> chat;
    Timer timer = new Timer();
    private RecyclerView chatRecView;
    private Button btnSend;
    private EditText edtMess;
    private SQLConnect sqlConnect = new SQLConnect();
    private String uID;

    private void initView() {
        chatRecView = findViewById(R.id.recViewChat);
        btnSend = findViewById(R.id.btnChatSend);
        edtMess = findViewById(R.id.edtChatMes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        setRepeatingAsyncTask();

        uID = (String) this.getIntent().getSerializableExtra("uID");
        // Log.d("test", uID);


        chat = new ArrayList<>();

        chat = sqlConnect.getMessagesSQL(uID);
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
                Date date = new Date();
                Long time = date.getTime();

                Map<String, Object> message = new HashMap<>();
                message.put("from", fireBase.getUserId());
                message.put("to", uID);
                message.put("message", edtMess.getText().toString());
                message.put("time", time.toString());
                message.put("isRead", false);
                message.put("isDownloaded", false);


                sqlConnect.addMessageSql(message);
                fireBase.sendMessage(message);

                chat.add(new Chat(edtMess.getText().toString(), fireBase.getUserId(), time.toString()));
                adapter.notifyDataSetChanged();
                edtMess.getText().clear();
            }
        });


    }

    private void setRepeatingAsyncTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            ChatUpdateAsynctask chatUpdateAsynctask = new ChatUpdateAsynctask();
                            chatUpdateAsynctask.execute();
                        } catch (Exception e) {
                            // error, do something
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 30 * 1000);  // interval of one minute
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Log.d("test", "ONDESTROY");
        timer.cancel();
    }

    public class ChatUpdateAsynctask extends AsyncTask<Void, Void, Void> {
        ArrayList<Chat> chatDownload;

        @Override
        protected Void doInBackground(Void... voids) {
            // Log.d("test", "doInBackground: Chat act");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fireBase.handleKeysReq();
                    if (fireBase.isNewMessage(uID)) {
                        fireBase.downloadMessages();
                        chatDownload = sqlConnect.getMessagesSQL(uID);
                        chat = chatDownload;
                        adapter.setChats(chat);
                        //chatRecView.scrollToPosition(adapter.getItemCount()-1);
                    }else{
                        // Log.d("test", "No new message from " + uID);
                    }
                }
            });
            return null;
        }
    }
}