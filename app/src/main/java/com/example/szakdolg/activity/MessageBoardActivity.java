package com.example.szakdolg.activity;

import android.app.job.JobScheduler;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.MessageB;
import com.example.szakdolg.recviewadapter.MessageBoardRecAdapter;
import com.example.szakdolg.R;
import com.example.szakdolg.SQLConnect;
import com.example.szakdolg.message.MessageApiService;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageBoardActivity extends AppCompatActivity {
    private static final String TAG = "MessageB";
    private FloatingActionButton contactsButton;
    private RecyclerView messageBoardRecView;
    //private final FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");
    private SQLConnect sqlConnect;
    private MessageBoardRecAdapter adapter;
    private ArrayList<MessageB> messageB;
    private Timer timer;
    private MaterialToolbar mToolbar;
   // private final String myID = firebaseConnect.getUserId();
    private static final int JOB_ID = 201;
    private JobScheduler scheduler;

    private void initView() {
        contactsButton = findViewById(R.id.btnMesBrdNew);
    }

    private void initJobScheduler() {
        /*
        Log.d(TAG, "initJobScheduler: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ComponentName componentName = new ComponentName(this, MyJobService.class);
            PersistableBundle bundle = new PersistableBundle();
            bundle.putString(BUNDLE_MY_ID, myID);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .setExtras(bundle)
                    .setPersisted(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setPeriodic(1 * 60 * 1000, 30 * 60 * 1000);
                Log.d(TAG, "initJobScheduler: ");
            } else {
                builder.setPeriodic(15 * 60 * 1000);
            }
            scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.schedule(builder.build());

        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_actvitiy);

        initView();

        //sqlConnect = SQLConnect.getInstance("sql", myID);
        //Log.d(TAG, "onCreate: " + firebaseConnect.getUserId());

        mToolbar = (MaterialToolbar) findViewById(R.id.messageBoardToolbar);
        setSupportActionBar(mToolbar);

        messageBoardRecView = findViewById(R.id.messageBoardRecView);
        messageB = new ArrayList<>();
        adapter = new MessageBoardRecAdapter(this);
        adapter.setMessageB(messageB);
        messageBoardRecView.setAdapter(adapter);
        messageBoardRecView.setLayoutManager(new LinearLayoutManager(this));


        MessageApiService messageApiService = RetrofitClient.getRetrofitInstance().create(MessageApiService.class);

        Call<ArrayList<MessageB>> messagesCall= messageApiService.getLatestMessages(SharedPreferencesUtil.getStringPreference(this, "auth_token"));

        messagesCall.enqueue(new Callback<ArrayList<MessageB>>(){
            @Override
            public void onResponse(Call<ArrayList<MessageB>> call, Response<ArrayList<MessageB>> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, ""+response.code());
                    ArrayList<MessageB> messageBoard = response.body();
                    adapter.setMessageB(messageB);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MessageB>> call, Throwable t) {
                Log.e(TAG, ""+t.getMessage());
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        // timer = new Timer();
       // scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
       // scheduler.cancelAll();

        //setRepeatingAsyncTask();

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageBoardActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  timer.cancel();
     //   initJobScheduler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuProfile:
                intent = new Intent(MessageBoardActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.menuContacts:
                intent = new Intent(MessageBoardActivity.this, ContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.menuSingOut:
              /*  firebaseConnect.logoutUser();
                firebaseConnect.mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        Intent intent = new Intent(MessageBoardActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                break;*/
            default:
                break;
        }
        return false;
    }
/*
    public class DownloadAsynctask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sqlConnect.isNotUploadedMessage()) {
                        Log.d(TAG, "doInBackground: " + sqlConnect.getMessagesNOTUploaded().toString());
                        firebaseConnect.sendArrayOfMessages(sqlConnect.getMessagesNOTUploaded());
                        Log.d(TAG, "doInBackground: There are some messages to upload");
                    } else {
                        Log.d(TAG, "doInBackground: There is message to upload");
                    }
                    firebaseConnect.handleKeysReq(null);
                   firebaseConnect.db.collection(myID).whereEqualTo("isDownloaded", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: " + task.getResult().size());
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {
                                        Log.d(TAG, "onComplete: Starting to download new messages");
                                        downloadMessages(document);
                                    } else {
                                        Log.d(TAG, "No new message");
                                    }
                                }
                            }
                        }
                    });
*
                    ArrayList<MessageB> message = sqlConnect.getLastMessageEachPersonSQL(myID);
                    if (!messageB.equals(message)) {
                        Log.d(TAG, "run: The size not the same, running adapter.notifyDataSetChanged() ");
                        Log.d(TAG, "run: " + messageB.size());
                        adapter.setMessageB(message);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            return null;
        }
    }
*/
    /*
    private void setRepeatingAsyncTask() {
        final Handler handler = new Handler();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            //DownloadAsynctask downloadAsynctask = new DownloadAsynctask();
                            //downloadAsynctask.execute();
                        } catch (Exception e) {
                            Log.d(TAG, "run: " + e);
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 5 * 1000);
    }
    */
 /*
    private void downloadMessages(QueryDocumentSnapshot document) {

        String privKey = null;
        if (!document.get("contact").toString().equals(null) &&
                !document.get("message").toString().equals(null) &&
                !document.get("time").toString().equals(null)) {
            Log.i(TAG, "downloadMessages(): found not empty messages " + document);
            if (sqlConnect.isKey(document.get("contact").toString())) {
                Log.d(TAG, document.get("contact").toString());
                // if i dont have the sender in Contacts, adding it.
                if (!sqlConnect.isInContracts(document.get("contact").toString())) {
                    firebaseConnect.addAUser(document.get("contact").toString());
                }
                // Get priv key from SQL
                privKey = sqlConnect.getPrivateKey(document.get("contact").toString());
                // Decrypt message here
                Log.d(TAG, privKey);

                String decMessage = Crypt.deCrypt(document.get("message").toString(), privKey);
                Log.d(TAG, decMessage);

                // Create a Chat class for the message
                Chat chat = new Chat(document.get("time").toString(), document.get("contact").toString(), decMessage, 0, 0, 0);

                sqlConnect.addMessageSql(chat, document.get("contact").toString());
                firebaseConnect.db.collection(firebaseConnect.getUserId()).document(document.get("time").toString()).update("isDownloaded", true);
            } else {
                Log.d(TAG, "download Messages: dont have the key from the sender");
            }
        }


    }
*/

}
