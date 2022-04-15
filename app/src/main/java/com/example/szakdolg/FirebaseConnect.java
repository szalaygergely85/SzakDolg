package com.example.szakdolg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseConnect {
    private static final String TAG = "FirebaseConnect";
    public FirebaseAuth mAuth;
    public FirebaseFirestore db;
    private SQLConnect sqlConnect;
    boolean done = false;
    boolean value = false;
    private Contact contact;
    private String pubKey = null;
    private String privKey = null;
    private String name;
    public static FirebaseConnect instance;
    private Uri picUri;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();




    public static synchronized FirebaseConnect getInstance(String name) {
        if (instance == null) {
            instance = new FirebaseConnect(name);
            return instance;
        } else {
            return instance;
        }
    }

    public FirebaseConnect(String name) {
        this.name = name;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if(isUserSigned()){

        }
    }


    public void changePassword(String pass, String oldPass) {
        sqlConnect = SQLConnect.getInstance("sql", getUserId());
        FirebaseUser user = mAuth.getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPass);


        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated.");
                        user.updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: Password change is success");
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (Exception e) {
                                        Log.d(TAG, " Change password Failed: " + e);
                                    }
                                }
                            }
                        });
                    }
                });


    }

    public void deleteAccount(String uid, Context context) {
        String myID = getUserId();
        db.collection("Users").document(uid).delete();
        if (uid.equals(myID)) {
            Log.d(TAG, "deleteAccount: Deleteing from auth too");
            mAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                }
            });
        } else {
            Intent intent = new Intent(context, MessageBoardActivity.class);
            context.startActivity(intent);

        }

    }


    public void uploadPic(Uri uri) {
        StorageReference imageRef = storageRef.child(getUserId() + ".jpg");
        UploadTask uploadTask = imageRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

    }

    /**
     * Download all not Downloaded messages from Firebase to SQLite
     */
    public void downloadMessages() {
        if (isUserSigned()) {
            sqlConnect = SQLConnect.getInstance("sql", getUserId());
            Log.d(TAG, "downloadMessages(): found new message for:" + getUserId());
            db.collection(getUserId()).whereEqualTo("isDownloaded", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            if (!document.get("contact").toString().equals(null) &&
                                    !document.get("message").toString().equals(null) &&
                                    !document.get("time").toString().equals(null)) {
                                Log.i(TAG, "downloadMessages(): found not empty messages " + document.toString());
                                if (sqlConnect.isKey(document.get("contact").toString())) {
                                    Log.d(TAG, document.get("contact").toString());

                                    // if i dont have the sender in Contacts, adding it.
                                    if (!sqlConnect.isInContracts(document.get("contact").toString())) {
                                        addAUser(document.get("contact").toString());
                                    }
                                    // Get priv key from SQL
                                    privKey = sqlConnect.getPrivateKey(document.get("contact").toString());
                                    // Decrypt message here
                                    Log.d(TAG, privKey);

                                    String decMessage = Crypt.deCrypt(document.get("message").toString(), privKey);
                                    Log.d(TAG, decMessage);

                                    // Create a CHat class for the message
                                    Chat chat = new Chat(document.get("time").toString(), document.get("contact").toString(), decMessage, 0, false, false);

                                    sqlConnect.addMessageSql(chat, document.get("contact").toString());
                                    db.collection(getUserId()).document(document.get("time").toString()).update("isDownloaded", true);
                                } else {
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
     * Download all Contacts form Firebase to SQLite
     */
    public void downloadContacts() {
        sqlConnect = SQLConnect.getInstance("sql", getUserId());
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
        sqlConnect = SQLConnect.getInstance("sql", getUserId());

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
                Log.d(TAG, "Key send with success" + sqlConnect.getPublicKey(uID));
            }
        });
    }

    public void sendKeyReturn(String uID) {
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

    public void handleKeysReq(String uID) {
        String myID = getUserId();
        Log.d(TAG, "handleKeysReq: my id " + myID );
        Log.d(TAG, "handleKeysReq: sending to "+ uID);
        Log.d(TAG, "handleKeysReq: starting it");
        sqlConnect = SQLConnect.getInstance("sql", myID);
        db.collection(myID).whereEqualTo("docType", "reqPubKey").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "onComplete: found +" + task.getResult().size());
                if (task.getResult().size()>0) {

                    Log.d(TAG, "A req Key Found, Downloading it");
                    for (QueryDocumentSnapshot document : task.getResult()) {

                            if (document.exists()){
                                sqlConnect.updatePublicExtKey(document.get("uID").toString(), document.get("pubKey").toString());
                                sendKeyReturn(document.get("uID").toString());
                            }else{
                                Log.d(TAG, "Maybe this one? ");
                            }

                    }
                }else{
                    Log.d(TAG, "Didnt find req key, checking if anybody sent");
                    db.collection(getUserId()).whereEqualTo("docType", "sentPubKey").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().size()>0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "onComplete: A sentKey Found, Downloading it");

                                    sqlConnect.updatePublicExtKey(document.get("uID").toString(), document.get("pubKey").toString());
                                    db.collection(getUserId()).document("sentPubKey:" + document.get("uID").toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "onSuccess: Sent key deleted");

                                        }
                                    });
                                }
                            } else {
                                Log.d(TAG, "onComplete: Didnt find Sent or Req key");
                                if (uID!=null) {
                                    sendKeyRequest(uID);
                                    Log.d(TAG, "sendMessage: Didnt find the key, sending key request");
                                }
                            }
                        }
                    });
                }
            }
        });





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
                    } catch (FirebaseAuthInvalidUserException e) {
                        Error.GetErrorMessageInToast("e7", context);
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Error.GetErrorMessageInToast("e8", context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });

    }


    public void getPicture() {


    }

    // MESSAGE HANDLING

    /**
     * Add a new message in Firebase(send)
     *
     * @param message
     */
    public void sendMessage(Chat message, String uID) {
        sqlConnect = SQLConnect.getInstance("sql", getUserId());


        Log.d(TAG, uID +" and my ID " + message.getContact());

        if (sqlConnect.isPubExtKey(uID)) {
            Log.d(TAG, "sendMessage: found the Key, sending message" + sqlConnect.getPublicExtKey(uID));

            pubKey = sqlConnect.getPublicExtKey(message.getContact());
            String encMessage = Crypt.enCrypt(message.getMessage(), pubKey);
            Log.d(TAG, "sendMessage: found the Key, sending message" + sqlConnect.getPublicExtKey(uID));
            Map<String, Object> cont = message.getHashMap();
            cont.remove("isFromMe");
            cont.remove("isRead");
            cont.remove("isUploaded");
            cont.put("contact", getUserId());
            cont.put("message", encMessage);
            cont.put("isDownloaded", false);
            cont.put("isRead", false);
            Log.d(TAG, "sendMessage: " + cont);
            db.collection(uID).document(message.getId()).set(cont).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    sqlConnect.setMessageToUploaded(message.getId());
                    //Log.d("FireStore", "Sikerult elkuldeni az uzit");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.d("FireStore", "Ajajajaj nem jo az uzenetkuldes");
                }
            });
        } else {
            Log.d(TAG, "sendMessage: Didnt find the key, starting handleKeys()");
            handleKeysReq(uID);
        }

    }

    public void sendArrayOfMessages(ArrayList<Chat> messages) {
        sqlConnect = SQLConnect.getInstance("sql", getUserId());
        Map<String, Object> cont;
        for (int counter = 0; counter < messages.size(); counter++) {
            Chat message = messages.get(counter);
            Log.d("Crypt", "Trying to send not uploaded messages to : " + message.toString());

            if (sqlConnect.isPubExtKey(message.getContact())) {
                String senderID = message.getContact();
                Log.d("Crypt", "found the Key, sending message");
                pubKey = sqlConnect.getPublicExtKey(message.getContact());
                Log.d("Crypt", "found the Key" + sqlConnect.getPublicExtKey(message.getContact()));
                String encMessage = Crypt.enCrypt(message.getMessage(), pubKey);
                Log.d("Crypt", "message is" + encMessage);

                cont = message.getHashMap();
                cont.put("message", encMessage);
                cont.remove("isFromMe");
                cont.remove("isRead");
                cont.remove("isUploaded");
                cont.put("contact", getUserId());
                cont.put("isDownloaded", false);
                cont.put("isRead", false);

                Log.d("Crypt", "message is" + cont.toString());

                db.collection(senderID).document(message.getId()).set(cont).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            } else {


                Log.d(TAG, "sendMessage: Didnt find the key, starting handleKeys()");
                handleKeysReq(message.getContact());



            }
        }

    }


    // USER HANDLING

    public Contact getContactFrUID(String uID) {
        db.collection("Users").document(uID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        contact = new Contact(document.get("userID").toString(), document.get("name").toString(), document.get("email").toString(), document.get("phone").toString());
                        Log.d(TAG, document.get("name").toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FireBase", e.toString());
            }
        });
        return contact;
    }

    /**
     * Add a User to the contact
     *
     * @param uID
     * @return
     */
    public Contact addAUser(String uID) {
        sqlConnect = SQLConnect.getInstance("sql", getUserId());
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
                        Log.d(TAG, document.get("name").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FireBase", e.toString());
            }
        });

        return contact;
    }

    /**
     * Logout user
     */
    public void logoutUser() {
        mAuth.signOut();
    }

    public void sendForgotPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        } else {

                            try {
                                throw task.getException();

                            } catch (FirebaseAuthInvalidUserException e) {
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
                        Toast.makeText(context, "Email already exists",
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d(TAG, "Register was success for " + user.get("email").toString());
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
                Log.d(TAG, "Creation was success for " + user.get("email").toString());
            }
        });

    }


}
