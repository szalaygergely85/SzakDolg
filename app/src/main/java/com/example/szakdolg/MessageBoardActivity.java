package com.example.szakdolg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MessageBoardActivity extends AppCompatActivity {
    private static final String TAG = "MessageB";
    private FloatingActionButton contactsButton;
    private RecyclerView messageBoardRecView;
    private FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");
    private SQLConnect sqlConnect = SQLConnect.getInstance("sql");
    private MessageBoardRecAdapter adapter;
    private ArrayList<MessageB> messageB;
    private Timer timer;

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
        timer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menuProfile:
                Toast.makeText(MessageBoardActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuAbout:
                intent = new Intent(MessageBoardActivity.this, AboutActivity.class);
                startActivity(intent);
                 break;
            case R.id.menuContacts:
                intent = new Intent(MessageBoardActivity.this, ContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.menuSingOut:
                firebaseConnect.logoutUser();
                break;
            default:
                break;
        }
        return false;

    }

    public class DownloadAsynctask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {



            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(sqlConnect.isNotUploadedMessage()){
                        Log.d(TAG, "doInBackground: "+ sqlConnect.getMessagesNOTUploaded().toString());
                        firebaseConnect.sendArrayOfMessages(sqlConnect.getMessagesNOTUploaded());
                        Log.d(TAG, "doInBackground: There are some not uploaded messages");

                    }else{
                        Log.d(TAG, "doInBackground: There arent any message to upload");
                    }

                    firebaseConnect.handleKeysReq();
                    if (firebaseConnect.isNewMessage()) {
                        Log.i(TAG, "doInBackground: There is a new message");
                        firebaseConnect.downloadMessages();
                        ArrayList<MessageB> message = sqlConnect.getLastMessageEachPersonSQL(firebaseConnect.getUserId());
                        messageB = message;
                        adapter.setMessageB(messageB);
                    }else{
                        Log.i(TAG, "doInBackground: No new message");
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
