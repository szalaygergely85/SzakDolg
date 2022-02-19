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




    private void initView(){
        contactsButton=findViewById(R.id.btnMesBrdNew);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_actvitiy);
        initView();
        DownloadAsynctask downloadAsynctask = new DownloadAsynctask();
        downloadAsynctask.execute();
        //handle recview
        messageBoardRecView= findViewById(R.id.messageBoardRecView);
        ArrayList<MessageB> messageB = new ArrayList<>();
        messageB = sqlConnect.getLastMessageEachPersonSQL();
        MessageBoardRecAdapter adapter =new MessageBoardRecAdapter(this);
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

           for (int i =0; i<100; i++){
                firebaseConnect.downloadMessages();
                Log.d("test", "doInBackground: " );
                SystemClock.sleep(30000);


            }
            return null;
        }
    }
}