package com.example.szakdolg;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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
    private static final String TAG = "FirebaseConnect";
    public FirebaseAuth mAuth;
    public FirebaseFirestore db;
    SQLConnect sqlConnect = SQLConnect.getInstance("sql");
    boolean done = false;
    boolean value = false;
    Contact contact;
    private String pubKey = null;
    private String privKey = null;
    private String name;
    public static FirebaseConnect instance;

    public static synchronized FirebaseConnect getInstance(String name){
        if (instance==null){
            instance = new FirebaseConnect(name);
            return instance;
        }else {
            return instance;
        }
    }
    public FirebaseConnect(String name) {
        this.name = name;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Download all not Downloaded messages from Firebase to SQLite
     */
    public void downloadMessages() {
        if (isUserSigned()) {
            Log.i(TAG, "downloadMessages(): found new message for:" + getUserId());
            Log.i(TAG, "downloadMessages(): found new message for:" + db.getFirestoreSettings());
            db.collection(getUserId()).whereEqualTo("isDownloaded", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            if (!document.get("contact").toString().equals(null) &&
                                    !document.get("message").toString().equals(null) &&
                                    !document.get("time").toString().equals(null)) {
                                Log.i(TAG, "downloadMessages(): found not empty messages " + document.toString());
                                if (sqlConnect.isKey(document.get("contact").toString())){
                                    // Log.d("FireBase", document.get("from").toString());

                                    // if i dont have the sender in Contacts, adding it.
                                    if (!sqlConnect.isInContracts(document.get("contact").toString())) {
                                        addAUser(document.get("contact").toString());
                                    }
                                    // Get priv key from SQL
                                    privKey = sqlConnect.getPrivateKey(document.get("contact").toString());
                                    // Decrypt message here
                                    Log.d("Crypt", privKey);

                                    String decMessage = Crypt.deCrypt(document.get("message").toString(), privKey);
                                    Log.d("Crypt", decMessage);

                                    // Create a CHat class for the message
                                    Chat chat = new Chat(document.get("time").toString(), document.get("contact").toString(), decMessage, 0, false, false);

                                    sqlConnect.addMessageSql(chat);
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

    public String isEmailNotRegistered(String email) {
        String uID = null;

            db.collection("Users").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
    public void loginUser(String email, String pass, Context context) {


        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        Error.GetErrorMessageInToast("e7", context);
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        Error.GetErrorMessageInToast("e8", context);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {

                }
            }
        });

    }

    /**
     * Logout user
     */
    public void logoutUser() {
        mAuth.signOut();
    }



    public void sendForgotPassword(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        } else {

                            try {
                                throw task.getException();

                            }catch (FirebaseAuthInvalidUserException e){
                                Log.d(TAG, "onComplete: we dont have that email registered");
                            } catch (Exception e) {
                                Log.d(TAG, "onComplete: " + e);
                            }
                        }
                    }
                });

    }

    /**
     * Register new user on Firebase
     *
     * @param user
     */
    public void registerNewUser(Map user, Context context) {

        mAuth.createUserWithEmailAndPassword(user.get("email").toString(), user.get("pass").toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(context,"Email already exists",
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    Log.d(TAG, "Register was success for "+ user.get("email").toString());
                    loginUser(user.get("email").toString(), user.get("pass").toString(), context);
                    createUser(user);

                }
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
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Creation was success for "+ user.get("email").toString());
            }
        });

    }

    /**
     * Add a new message in Firebase(send)
     *
     * @param message
     */

    public void sendMessage(Chat message) {


        Log.d("Crypt", message.getContact());

        if (sqlConnect.isPubExtKey(message.getContact())){
            Log.d("Crypt", "found the Key, sending message");
            pubKey = sqlConnect.getPublicExtKey(message.getContact());
            Log.d("Crypt", "found the Key" + sqlConnect.getPublicExtKey(message.getContact()));
            String encMessage = Crypt.enCrypt(message.getMessage(), pubKey);

            Map<String, Object> cont = message.getHashMap();
            cont.remove("isFromMe");
            cont.remove("isRead");
            cont.remove("isUploaded");
            cont.put("message", encMessage);
            cont.put("isDownloaded", false);
            cont.put("isRead", false);
            Log.d("Crypt", cont.toString());
            db.collection(message.getContact()).document(message.getId()).set(cont).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    //Log.d("FireStore", "Sikerult elkuldeni az uzit");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.d("FireStore", "Ajajajaj nem jo az uzenetkuldes");
                }
            });
        }else{

            if(isKeySentToME()){
                handleKeysReq();
            }else{
                sendKeyRequest(message.getContact());
                Log.d("Crypt", "Didnt find the key, sending key request");
            }

        }

    }
    public void sendArrayOfMessages(ArrayList<Chat> messages) {
        Map<String, Object> cont;
        for (int counter = 0; counter < messages.size(); counter++) {
            Chat message = messages.get(counter);
            Log.d("Crypt", "Trying to send not uploaded messages to : " +message.toString());

            if (sqlConnect.isPubExtKey(message.getContact())){
                Log.d("Crypt", "found the Key, sending message");
                pubKey = sqlConnect.getPublicExtKey(message.getContact());
                Log.d("Crypt", "found the Key" + sqlConnect.getPublicExtKey(message.getContact()));
                String encMessage = Crypt.enCrypt(message.getMessage(), pubKey);
                Log.d("Crypt", "message is" + encMessage);

                cont= message.getHashMap();
                cont.put("message", encMessage);
                cont.remove("isFromMe");
                cont.remove("isRead");
                cont.remove("isUploaded");
                cont.put("contact", getUserId());
                cont.put("isDownloaded", false);
                cont.put("isRead", false);

                Log.d("Crypt", "message is" + cont.toString());

                db.collection(message.getContact()).document(message.getId()).set(cont).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                         Log.d("FireStore", "Sikerult elkuldeni az uzit");
                         sqlConnect.setMessageToUploaded(message.getId());

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
                    sendKeyRequest(message.getContact());
                    Log.d("Crypt", "Didnt find the key, sending key request");
                }

            }
        }

    }
}
