package com.example.szakdolg;

import static com.example.szakdolg.MyJobService.BUNDLE_MY_ID;
import static java.lang.Boolean.TRUE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    final Handler handler = new Handler();
    private FirebaseConnect fireBase = FirebaseConnect.getInstance("firebase");
    private ChatAdapter adapter;
    private ArrayList<Chat> messageList;
    private Timer timer = new Timer();
    private RecyclerView chatRecView;
    private Button btnSend;
    private EditText edtMess;
    private SQLConnect sqlConnect;
    private String uID ;
    private String myID = fireBase.getUserId();
    private static final int JOB_ID = 201;
    private JobScheduler scheduler;


    private void initView() {
        chatRecView = findViewById(R.id.recViewChat);
        btnSend = findViewById(R.id.btnChatSend);
        edtMess = findViewById(R.id.edtChatMes);
    }
        private void initJobScheduler() {
            Log.d(TAG, "initJobScheduler: ");
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
                ComponentName componentName = new ComponentName(this, MyJobService.class);
                PersistableBundle bundle = new PersistableBundle();
                bundle.putString(BUNDLE_MY_ID, myID);
                JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName)
                        //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setExtras(bundle)
                        .setPersisted(true);
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
                    builder.setPeriodic(1*60*1000, 30*60*1000);
                    Log.d(TAG, "initJobScheduler: ");
                }else{
                    builder.setPeriodic(15*60*1000);
                }
                scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.schedule(builder.build());


            }
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();

        uID = (String) this.getIntent().getSerializableExtra("uID");

        Log.d(TAG, "onCreate: " +uID);


        messageList = new ArrayList<>();
        sqlConnect = SQLConnect.getInstance("sql", fireBase.getUserId());;
        messageList = sqlConnect.getMessagesSQL(uID);

        sqlConnect.setMessageRead(uID);

        adapter = new ChatAdapter(this);
        adapter.setChats(messageList);

        chatRecView.setAdapter(adapter);
        chatRecView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        timer = new Timer();
        scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancelAll();

        setRepeatingAsyncTask();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                Long time = date.getTime();


                if (!edtMess.getText().toString().isEmpty()){
                if(uID!=null){
                    Chat chat = new Chat(time.toString(), uID, edtMess.getText().toString(),1, 0, 0);
                    Log.d(TAG, chat.toString());
                    sqlConnect.addMessageSql(chat, uID);
                    fireBase.sendMessage(chat, uID);
                    ListHandleAsyncTask refreshNewSend = new ListHandleAsyncTask();
                    refreshNewSend.execute();

                    edtMess.getText().clear();

                }
                }else {
                    Log.d(TAG, "onClick: Message field is empty");

                }
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
    protected void onPause() {
        super.onPause();
        timer.cancel();
        initJobScheduler();
    }

    public class ChatUpdateAsynctask extends AsyncTask<Void, Void, Void> {
        ArrayList<Chat> chatDownload;

        @Override
        protected Void doInBackground(Void... voids) {
            
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(sqlConnect.isNotUploadedMessage()){
                        Log.d(TAG, "doInBackground: "+ sqlConnect.getMessagesNOTUploaded().toString());

                        fireBase.sendArrayOfMessages(sqlConnect.getMessagesNOTUploaded());
                        Log.d(TAG, "doInBackground: There are some not uploaded messages");

                    }else{
                        Log.d(TAG, "doInBackground: There arent any message to upload");
                    }

                    fireBase.handleKeysReq(null);
                    Log.d(TAG, "run: from " + uID + "my id: " + myID);

                    fireBase.db.collection(myID).whereEqualTo("isDownloaded", false).whereEqualTo("contact", uID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: " +task.getResult().size());
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {
                                        Log.d(TAG, "onComplete: Starting to download new messages");
                                        downloadMessages(document);
                                    } else {
                                        Log.d(TAG, "No new message from " + uID);
                                    }
                                }
                            }

                        }
                    });
                    chatDownload = sqlConnect.getMessagesSQL(uID);
                    if(!messageList.equals(chatDownload)){
                        Log.d(TAG, "run: The size not the same, running adapter.notifyDataSetChanged() ");

                        ListHandleAsyncTask downloadRefresh = new ListHandleAsyncTask();
                        downloadRefresh.execute();
                    }
                }
            });
            return null;
        }

    }
    private void downloadMessages(QueryDocumentSnapshot document){
        String privKey = null;

        if (!document.get("contact").toString().equals(null) &&
                !document.get("message").toString().equals(null) &&
                !document.get("time").toString().equals(null)) {
            Log.i(TAG, "downloadMessages(): found not empty messages " + document.toString());
            if (sqlConnect.isKey(document.get("contact").toString())) {
                Log.d(TAG, document.get("contact").toString());

                // if i dont have the sender in Contacts, adding it.
                if (!sqlConnect.isInContracts(document.get("contact").toString())) {
                    fireBase.addAUser(document.get("contact").toString());
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
                fireBase.db.collection(fireBase.getUserId()).document(document.get("time").toString()).update("isDownloaded", true);
            } else {
                Log.d(TAG, "download Messges: dont have a key from the sender");
            }
        }
    }
    public class ListHandleAsyncTask extends AsyncTask<Void, Void, Void>{
        ArrayList<Chat> chatDownload;
        @Override
        protected Void doInBackground(Void... voids) {
            chatDownload = sqlConnect.getMessagesSQL(uID);
            Log.d(TAG, "doInBackground: the size of array:" + chatDownload.size() );

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(TAG, "onPostExecute: running refresh");
            adapter.setChats(chatDownload);
            if(messageList.size()!=chatDownload.size()) {
                chatRecView.scrollToPosition(chatDownload.size() - 1);
            }
            super.onPostExecute(unused);
        }
    }

}