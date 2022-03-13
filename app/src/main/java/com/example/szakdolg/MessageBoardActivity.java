package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MessageBoardActivity extends AppCompatActivity {
    private FloatingActionButton contactsButton;
    private RecyclerView messageBoardRecView;
    FirebaseConnect firebaseConnect = new FirebaseConnect();
    private SQLConnect sqlConnect = new SQLConnect();
    MessageBoardRecAdapter adapter;
    ArrayList<MessageB> messageB;

    Timer timer;



    private void initView() {
        contactsButton = findViewById(R.id.btnMesBrdNew);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_actvitiy);
        initView();




        messageBoardRecView = findViewById(R.id.messageBoardRecView);
        messageB = new ArrayList<>();
        messageB = sqlConnect.getLastMessageEachPersonSQL(firebaseConnect.getUserId());
        adapter = new MessageBoardRecAdapter(this);
        adapter.setMessageB(messageB);
        messageBoardRecView.setAdapter(adapter);
        messageBoardRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        timer = new Timer();
        setRepeatingAsyncTask();


        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageBoardActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Log.d("test", "onStop");
        timer.cancel();
    }


    public class DownloadAsynctask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


             Log.d("test", "doInBackground: From MessageBoard");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(sqlConnect.isNotUploadedMessage()){
                        Log.d("test", "There are not uploaded messages");

                    }else{
                        Log.d("test", "There arent any message to upload");
                    }

                    firebaseConnect.handleKeysReq();
                    if (firebaseConnect.isNewMessage()) {
                        firebaseConnect.downloadMessages();
                        ArrayList<MessageB> message = sqlConnect.getLastMessageEachPersonSQL(firebaseConnect.getUserId());
                        messageB = message;
                        adapter.setMessageB(messageB);
                    }else{
                        // Log.d("test", "No new message");
                    }
                }
            });
            return null;
        }
    }





    private void setRepeatingAsyncTask() {

        final Handler handler = new Handler();


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            DownloadAsynctask downloadAsynctask = new DownloadAsynctask();
                            downloadAsynctask.execute();
                        } catch (Exception e) {
                            // error, do something
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 30 * 1000);


    }
}
