package com.example.szakdolg;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    boolean done = false;
    boolean value = false;
    Contact contact;
    private String pubKey = null;
    private String privKey = null;
    public FirebaseConnect() {

        //FireBase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Download all not Downloaded messages from Firebase to SQLite
     */
    public void downloadMessages() {
        if (isUserSigned()) {
            db.collection(getUserId()).whereEqualTo("isDownloaded", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!document.get("message").toString().equals(null) &&
                                    !document.get("from").toString().equals(null) &&
                                    !document.get("to").toString().equals(null) &&
                                    !document.get("time").toString().equals(null)) {
                                if (sqlConnect.isKey(document.get("from").toString())){

                                    // Log.d("FireBase", document.get("from").toString());

                                    // if i dont have the sender in Contacts, adding it.
                                    if (!sqlConnect.isInContracts(document.get("from").toString())) {
                                        addAUser(document.get("from").toString());
                                    }
                                    // Get priv key from SQL
                                    privKey = sqlConnect.getPrivateKey(document.get("from").toString());
                                    // Decrypt message here
                                     Log.d("Crypt", privKey);
                                    String decMessage = Crypt.deCrypt(document.get("message").toString(), privKey);
                                    Log.d("Crypt", decMessage);
                                    // Create Map for the message
                                    Map<String, Object> message = new HashMap<>();
                                    message.put("from", document.get("from").toString());
                                    message.put("to", document.get("to").toString());
                                    message.put("message", decMessage);
                                    message.put("time", document.get("time").toString());
                                    message.put("isRead", false);
                                    // Log.d("SQL", document.get("message").toString());
                                    sqlConnect.addMessageSql(message);
                                    db.collection(getUserId()).document(document.get("time").toString()).update("isDownloaded", true);
                                }else{
                                    Log.e("Crypt", "Dont have key form the sender");
                                }
                            }
                        }
                    }
                }
            });
        }

    }

    public boolean isNewMessage() {

        db.collection(getUserId()).whereEqualTo("isDownloaded", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            done = true;
                        } else {
                            done = false;
                        }
                    }
                }

            }
        });
        return done;
    }

    public boolean isNewMessage(String From) {

        db.collection(getUserId()).whereEqualTo("isDownloaded", false).whereEqualTo("from", From).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            done = true;
                        } else {
                            done = false;
                        }
                    }
                }

            }
        });
        return done;
    }

    /**
     * Add a User to the contact
     *
     * @param uID
     * @return
     */
    public Contact addAUser(String uID) {
        // Log.d("FireBase", "We are in addAuser with : " + uID);
        db.collection("Users").document(uID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        contact = new Contact(document.get("userID").toString(), document.get("name").toString(), document.get("email").toString(), document.get("phone").toString());
                        addContactFB(contact);
                        Map<String, Object> cont = new HashMap<>();
                        cont.put("docType", "contacts");
                        cont.put("uID", contact.getID());
                        cont.put("uName", contact.getName());
                        cont.put("uEmail", contact.getEmail());
                        cont.put("uPhone", contact.getPhone());
                        sqlConnect.addContactSQLite(cont);
                        // Log.d("FireBase", document.get("name").toString());
                    } else {
                        // Log.d("FireBase", "No such document");
                    }
                } else {
                    // Log.d("FireBase", "get failed with ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Log.d("FireBase", e.toString());
            }
        });

        return contact;
    }

    /**
     * Download all Contacts form Firebase to SQLite
     */
    public void downloadContacts() {
        db.collection(getUserId()).whereEqualTo("docType", "contacts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String uID = document.get("uID").toString();
                        String uName = document.get("uName").toString();
                        String uEmail = document.get("uEmail").toString();
                        String uPhone = document.get("uPhone").toString();
                        sqlConnect.addContactSQLite(uID, uName, uEmail, uPhone);

                    }
                }
            }
        });
    }

    public void sendKeyRequest(String uID) {

        if (!sqlConnect.isKey(uID)) {
            sqlConnect.generateKeys(uID);
            // Log.d("Crypt", "Generatig keys");
        }

        Map<String, Object> req = new HashMap<>();
        req.put("uID", getUserId());
        // Log.d("Crypt", sqlConnect.getPublicKey(uID));
        req.put("pubKey", sqlConnect.getPublicKey(uID));
        req.put("docType", "reqPubKey");
        db.collection(uID).document("reqPubKey:" + getUserId()).set(req).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Crypt", "Key send with success" + sqlConnect.getPublicKey(uID));
            }
        });
    }

    public void sendKeyReturn(String uID){
        Map<String, Object> req = new HashMap<>();
        req.put("uID", getUserId());
         // Log.d("Crypt", sqlConnect.getPublicKey(uID));
        req.put("pubKey", sqlConnect.getPublicKey(uID));
        req.put("docType", "sentPubKey");
        db.collection(uID).document("sentPubKey:" + getUserId()).set(req).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                db.collection(getUserId()).document("reqPubKey:" + uID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                         Log.d("Crypt", "Req key deleted");
                    }
                });
            }
        });

    }
    public boolean isKeyRequest() {

        db.collection(getUserId()).whereEqualTo("docType", "reqPubKey").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (!document.equals(null)) {
                            value = true;
                        }
                    }
                }
            }
        });

        return value;
    }
    public boolean isKeySentToME() {

        db.collection(getUserId()).whereEqualTo("docType", "sentPubKey").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (!document.equals(null)) {
                            value = true;
                        }
                    }
                }
            }
        });

        return value;
    }

    public void handleKeysReq(){
        if(isKeyRequest()) {
            if (isUserSigned()) {
                db.collection(getUserId()).whereEqualTo("docType", "reqPubKey").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Crypt", "A req Key Found, Downloading it");
                                sqlConnect.updatePublicExtKey(document.get("uID").toString(), document.get("pubKey").toString());
                                sendKeyReturn(document.get("uID").toString());
                            }
                        }
                    }
                });
            }
        }
        if(isKeySentToME()){
            if (isUserSigned()) {
                db.collection(getUserId()).whereEqualTo("docType", "sentPubKey").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Crypt", "A sentKey Found, Downloading it");
                                sqlConnect.updatePublicExtKey(document.get("uID").toString(), document.get("pubKey").toString());
                                db.collection(getUserId()).document("sentPubKey:" + document.get("uID").toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                         Log.d("Crypt", "Sent key deleted");
                                    }
                                });
                            }
                        }
                    }
                });
            }

        }

    }

    /**
     * add new contact to the contacts list on Firebase
     * also call the function to add in SQLite.
     *
     * @param contact
     */
    public void addContactFB(Contact contact) {
        // Log.d("FireBase", contact.toString());
        Map<String, Object> cont = new HashMap<>();
        cont.put("docType", "contacts");
        cont.put("uID", contact.getID());
        cont.put("uName", contact.getName());
        cont.put("uEmail", contact.getEmail());
        cont.put("uPhone", contact.getPhone());
        db.collection(getUserId()).document(contact.getID()).set(cont).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Log.d("FireBase", "Contact added");
            }
        });

    }

    /**
     * Does any user signed in?
     *
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
     * check if we really created the user on FireBase
     *
     * @return
     */
    public boolean isUserCreated(String userID) {

        db.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                done = true;

            }
        });
        return done;
    }

    /**
     * Get logged in users ID
     *
     * @return
     */
    public String getUserId() {
        return mAuth.getCurrentUser().getUid();
    }

    public String getUserEmail() {
        return mAuth.getCurrentUser().getEmail();
    }

    public String getContactUID(String email) {
        String uID = null;
        db.collection(getUserId()).whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String uID = document.get("userID").toString();
                    }

                }
            }
        });
        return uID;
    }

    /**
     * Login user to firebase
     *
     * @param email
     * @param pass
     */
    public boolean loginUser(String email, String pass) {

        // Log.d("FireBase", "User logged in successful");
        mAuth.signInWithEmailAndPassword(email, pass).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FireBase", "User log in was a failure");
            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Log.d("async", "login completed");
                done = true;
            }
        });
        return done;
    }

    /**
     * Logout user
     */
    public void logoutUser() {
        mAuth.signOut();
    }

    /**
     * Register new user on Firebase
     *
     * @param user
     */
    public void registerNewUser(Map user) {

        mAuth.createUserWithEmailAndPassword(user.get("email").toString(), user.get("pass").toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                createUser(user);
                Log.e("FireBase", "Register was success");
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
     *
     * @param user
     */

    public void createUser(Map user) {
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
     * Add a new message in Firebase(send)
     *
     * @param message
     */

    public void sendMessage(Map<String, Object> message) {


        Log.d("Crypt", message.get("to").toString());
        if (sqlConnect.isPubExtKey(message.get("to").toString())){
            Log.d("Crypt", "found the Key, sending message");
            pubKey = sqlConnect.getPublicExtKey(message.get("to").toString());
            Log.d("Crypt", "found the Key" + sqlConnect.getPublicExtKey(message.get("to").toString()));
            String encMessage = Crypt.enCrypt(message.get("message").toString(), pubKey);
            message.put("message", encMessage);

            db.collection(message.get("to").toString()).document(message.get("time").toString()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Log.d("FireStore", "Sikerult elkuldeni az uzit");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Log.d("FireStore", "Ajajajaj nem jo az uzenetkuldes");
                }
            });
        }else{

            if(isKeySentToME()){
                handleKeysReq();
            }else{
                sendKeyRequest(message.get("to").toString());
                Log.d("Crypt", "Didnt find the key, sending key request");
            }

        }

    }
    public void sendArrayOfMessages(ArrayList<Chat> messages) {
        for (int counter = 0; counter < messages.size(); counter++) {
            Chat chat = messages.get(counter);
            Map<String, Object> message = new HashMap<>();
            message.put("from", fireBase.getUserId());
            message.put("to", uID);
            message.put("message", edtMess.getText().toString());
            message.put("time", time.toString());
            message.put("isRead", false);
            message.put("isDownloaded", false);
        }

        Log.d("Crypt", message.get("to").toString());
        if (sqlConnect.isPubExtKey(message.get("to").toString())){
            Log.d("Crypt", "found the Key, sending message");
            pubKey = sqlConnect.getPublicExtKey(message.get("to").toString());
            Log.d("Crypt", "found the Key" + sqlConnect.getPublicExtKey(message.get("to").toString()));
            String encMessage = Crypt.enCrypt(message.get("message").toString(), pubKey);
            message.put("message", encMessage);

            db.collection(message.get("to").toString()).document(message.get("time").toString()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                     Log.d("FireStore", "Sikerult elkuldeni az uzit");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                     Log.d("FireStore", "Ajajajaj nem jo az uzenetkuldes");
                }
            });
        }else{

            if(isKeySentToME()){
                handleKeysReq();
            }else{
                sendKeyRequest(message.get("to").toString());
                Log.d("Crypt", "Didnt find the key, sending key request");
            }

        }

    }
}
