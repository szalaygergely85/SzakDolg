package com.example.szakdolg;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.szakdolg.util.CryptUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    private SQLConnect sqlConnect;
    private static final String TAG = "MyJobService";
    public static final String BUNDLE_MY_ID = "myID";
    private SyncAsyncTask asyncTask;
    private JobParameters parameters;
    private final FirebaseConnect firebaseConnect = FirebaseConnect.getInstance("firebase");
    private static final String CHANNEL_ID = "MessageChannel";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        parameters = jobParameters;
        PersistableBundle bundle = jobParameters.getExtras();
        String myID = bundle.getString(BUNDLE_MY_ID, null);
        asyncTask = new SyncAsyncTask();
        if (myID != null) {
            asyncTask.execute(myID);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (null != asyncTask) {
            if (!asyncTask.isCancelled()) {
                asyncTask.cancel(true);
            }
        }
        return false;
    }

    private class SyncAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            sqlConnect = SQLConnect.getInstance("sql", strings[0]);
            syncTask(strings[0]);
            return "Job Finished";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "onProgressUpdate:number " + values[0]);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: " + s);
            jobFinished(parameters, true);
        }
    }

    private void syncTask(String myID) {
        if (firebaseConnect.isUserSigned()) {
            if (sqlConnect.isNotUploadedMessage()) {
                Log.d(TAG, "doInBackground: " + sqlConnect.getMessagesNOTUploaded().toString());
                firebaseConnect.sendArrayOfMessages(sqlConnect.getMessagesNOTUploaded());
                Log.d(TAG, "doInBackground: There are some not uploaded messages");
            } else {
                Log.d(TAG, "doInBackground: There arent any message to upload");
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
                                display(1, CHANNEL_ID, "You have a new message!", "New Message", MyJobService.this);
                                downloadMessages(document);
                            } else {
                                Log.d(TAG, "No new message");
                            }
                        }
                    }
                }
            });
        }
    }

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

                String decMessage = CryptUtil.deCrypt(document.get("message").toString(), privKey);
                Log.d(TAG, decMessage);

                // Create a Chat class for the message
                Chat chat = new Chat(document.get("time").toString(), document.get("contact").toString(), decMessage, 0, 0, 0);

                sqlConnect.addMessageSql(chat, document.get("contact").toString());
                firebaseConnect.db.collection(firebaseConnect.getUserId()).document(document.get("time").toString()).update("isDownloaded", true);
            } else {
                Log.d(TAG, "download Messges: dont have a key from the sender");
            }
        }
    }

    private void createChannel(String id, CharSequence name, int imp) {
        NotificationChannel channel = new NotificationChannel(id, name, imp);
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(channel);
    }

    private void display(int id, CharSequence channel_id, String content, String title, Context ctx) {
        createChannel((String) channel_id, channel_id, NotificationManager.IMPORTANCE_HIGH);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(ctx, (String) channel_id)
                .setSmallIcon(R.drawable.ic_menu_notifications)
                .setContentText(content)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat nmc = NotificationManagerCompat.from(ctx);
        nmc.notify(id, nb.build());
    }
}
