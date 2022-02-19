package com.example.szakdolg;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseConnect {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    SQLConnect sqlConnect = new SQLConnect();
    public FirebaseConnect() {

        //FireBase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Download all not Downloaded messages from Firebase to SQLite
     */
    public void downloadMessages(){
        if(isUserSigned()){
            db.collection(getUserId()).whereEqualTo("downloaded", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!document.get("message").toString().equals(null) &&
                                    !document.get("from").toString().equals(null) &&
                                    !document.get("to").toString().equals(null) &&
                                    !document.get("time").toString().equals(null)) {

                                Map<String, Object> message = new HashMap<>();
                                message.put("from", document.get("from").toString());
                                message.put("to", document.get("to").toString());
                                message.put("message",document.get("message").toString() );
                                message.put("time", document.get("time").toString());
                                message.put("isRead", false);
                                Log.d("SQL", document.get("message").toString());
                            sqlConnect.sendMessageSql(message);

                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Download all Contacts form Firebase to SQLite
     */
    public void downloadContacts(){
        db.collection(getUserId()).whereEqualTo("docType", "contacts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String uID = document.get("uID").toString();
                        String uName =document.get("uName").toString();
                        String uEmail =document.get("uEmail").toString();
                        String uPhone =document.get("uPhone").toString();
                        sqlConnect.addContactSQLite(uID, uName, uEmail, uPhone);

                    }
                }
            }
        });
    }


    /**
     * add new contacts to the contacts list on Firebase
     * also call the function to add in SQLite.
     * @param contact
     */
    public void addContactFB(Contact contact){
        Map<String, Object> cont = new HashMap<>();
        cont.put("docType", "contacts");
        cont.put("uID", contact.getID());
        cont.put("uName", contact.getName());
        cont.put("uEmail", contact.getEmail());
        cont.put("uPhone", contact.getPhone());
        db.collection(getUserId()).document(contact.getID()).set(cont).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sqlConnect.addContactSQLite(cont);
            }
        });

    }

    /**
     * Does any user signed in?
     * @return
     */
    public Boolean isUserSigned() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get logged in users ID
     * @return
     */
    public String getUserId(){
        return mAuth.getCurrentUser().getUid();
    }
    public String getUserEmail(){
        return mAuth.getCurrentUser().getEmail();
    }
    public String getContactUID(String email){
        String uID = null;
        db.collection(getUserId()).whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String uID= document.get("userID").toString();
                    }

                }
            }
        });
        return uID;
    }

    /**
     * Login user to firebase
     * @param email
     * @param pass
     */
    public void loginUser(String email, String pass) {
        Log.d("FireBase", "User logged in successful");
        mAuth.signInWithEmailAndPassword(email, pass).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FireBase", "User log in was a failure");
            }
        });
    }

    /**
     * Logout user
     */
    public void logoutUser(){
        mAuth.signOut();
    }

    /**
     * Register new user on Firebase
     * @param user
     */
    public void registerNewUser(Map user){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
          mAuth.createUserWithEmailAndPassword(user.get("email").toString(), user.get("pass").toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                  createUser(user);
              }
          }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("FireBase", "Register Failed");
                }
            });
    }

    /**
     * After register firebase account, create the user in Users collection too
     * @param user
     */

    public void createUser(Map user){
            user.put("userID", getUserId());
            user.remove("pass");
            db.collection("Users").document(getUserId()).set(user).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("FireBase", "User creation failed");
                }
            });

    }

    /**
     * Add a new message in Firebase(send) also calls SQL-s send message function
     * @param To
     * @param Message
     * @return
     */
    public Chat sendMessage(String To, String Message){
        Date date = new Date();
        Long time = date.getTime();

        Map<String, Object> message = new HashMap<>();
        message.put("from", getUserId());
        message.put("to", To);
        message.put("message", Message);
        message.put("time", time.toString());
        message.put("isRead", false);



        db.collection(To).document(time.toString()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("FireStore", "Siker");
                sqlConnect.sendMessageSql(message);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FireStore", "Ajajajaj nem jo az uzenetkuldes");
            }
        });
        return (new Chat(Message, To));
    }
    public void getMessages(String uID){
        Log.d("FireBase", uID);
        Log.d("FireBase", getUserId());
        db.collection(getUserId()).whereEqualTo("to", uID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        String mess = document.get("message").toString();
                        String fr = document.get("from").toString();
                        Log.d("FireBase", mess);
                    }
                }

            }
        });

    }
}
