package com.example.szakdolg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MessageBoardActivity extends AppCompatActivity {
    private FloatingActionButton contactsButton;
    private RecyclerView messageBoardRecView;
    FirebaseConnect firebaseConnect = new FirebaseConnect();
    private SQLConnect sqlConnect = new SQLConnect();
    MessageBoardRecAdapter adapter;
    ArrayList<MessageB> messageB;
    DownloadAsynctask downloadAsynctask = new DownloadAsynctask();;



    private void initView(){
        contactsButton=findViewById(R.id.btnMesBrdNew);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_actvitiy);
        initView();

        downloadAsynctask.execute();
        //handle recview
        messageBoardRecView= findViewById(R.id.messageBoardRecView);
        messageB = new ArrayList<>();
        messageB = sqlConnect.getLastMessageEachPersonSQL(firebaseConnect.getUserId());
        adapter =new MessageBoardRecAdapter(this);
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
    public class DownloadAsynctask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < 100; i++) {
                firebaseConnect.downloadMessages();

                Log.d("test", "doInBackground: From MessageBoard");
                SystemClock.sleep(30000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<MessageB> message = sqlConnect.getLastMessageEachPersonSQL(firebaseConnect.getUserId());
                        messageB = message;
                        adapter.setMessageB(messageB);
                    }
                });
            }
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("test", "onPause");
        downloadAsynctask.cancel(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("test", "onStop");
        downloadAsynctask.cancel(true);
    }
}
