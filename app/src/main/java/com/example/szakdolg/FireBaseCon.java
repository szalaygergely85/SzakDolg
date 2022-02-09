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
    public void loginUser(String email, String pass) {
        try {
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("FireBase", "User logged in successful");
                    }
                }
            });
        }catch (Exception e){
            Log.d("FireBase", e.toString());
        }
    }
    public void registerNewUser(String email, String pass){
        try {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Log.d("FireBase", "Log in was success");
                }
            });
        }catch (Exception e){
            Log.e("FireBase", e.toString());
        }
    }
    public void sendMessage(String To, String Message){
        Date date = new Date();
        Long time = date.getTime();

        Map<String, Object> message = new HashMap<>();
        message.put("from", getUserId());
        message.put("to", To);
        message.put("message", Message);
        message.put("time", time.toString());


        db.collection(To).document(time.toString()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}
