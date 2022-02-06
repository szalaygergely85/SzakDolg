package com.example.szakdolg;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FireBaseCon {
    public FirebaseAuth mAuth;

    public FireBaseCon() {
        mAuth = FirebaseAuth.getInstance();
    }
    public Boolean isUserSigned() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return true;
        } else {
            return false;
        }
    }



    public Boolean loginUser(String email, String pass) {
        try {
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("FireBase", "User logged in successful");
                    }
                }
            });
            return true;
        }catch (Exception e){
            Log.d("FireBase", e.toString());
            return false;
        }

    }




}
