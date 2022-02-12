package com.example.szakdolg;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FireBaseCon {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public FireBaseCon() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }


    public Boolean isUserSigned() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return true;
        } else {
            return false;
        }
    }
    public String getUserId(){
        return mAuth.getCurrentUser().getUid();
    }
    public String getUserEmail(){
        return mAuth.getCurrentUser().getEmail();
    }
    public void loginUser(String email, String pass) {
        Log.d("FireBase", "User logged in successful");
        mAuth.signInWithEmailAndPassword(email, pass).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FireBase", "User log in was a failure");
            }
        });
    }
    public void logoutUser(){
        mAuth.signOut();
    }
    public void registerNewUser(Map user){

          mAuth.createUserWithEmailAndPassword(user.get("email").toString(), user.get("pass").toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                  createUser();
              }
          }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("FireBase", "Register Failed");
                }
            });
    }
    public void createUser(){


            Map<String, Object> user = new HashMap<>();
            user.put("userID", getUserId());
            user.put("userEmail", getUserEmail());
            db.collection("Users").document(getUserId()).set(user).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("FireBase", "User creation failed");
                }
            });

    }
    public void sendMessage(String To, String Message){
        Date date = new Date();
        Long time = date.getTime();

        Map<String, Object> message = new HashMap<>();
        message.put("from", getUserId());
        message.put("to", To);
        message.put("message", Message);
        message.put("time", time.toString());


        db.collection(getUserId()).document("Messages").collection(To).document(time.toString()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("FireStore", "Siker");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FireStore", "Ajajajaj nem jo az uzenetkuldes");
            }
        });
    }
    public void getAllMessageList(){


    }
}
