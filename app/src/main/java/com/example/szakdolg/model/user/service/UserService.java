package com.example.szakdolg.model.user.service;

import android.content.Context;

import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.model.user.api.UserApiHelper;
import com.example.szakdolg.model.user.constans.UserConstans;
import com.example.szakdolg.model.user.model.User;
import com.example.szakdolg.util.HashUtils;
import com.example.szakdolg.util.KeyStoreUtil;

import java.util.HashMap;

public class UserService {

    public User addUser(User user, Context context, User currentUser){
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setUserId(user.getUserId());
        newUser.setDisplayName(user.getDisplayName());
        newUser.setEmail(user.getEmail());
        newUser.setPublicKey(user.getPublicKey());
        newUser.setProfilePictureUuid(user.getProfilePictureUuid());
        newUser.setStatus(user.getStatus());
        newUser.setTags(user.getTags());
        newUser.setAuthToken(user.getAuthToken());

        UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(context, currentUser);
        userDatabaseUtil.insertUser(newUser);
        return newUser;
    }


    public void registerUser(User user, Context context){
        String pass = user.getPassword();
        String hashPass = HashUtils.hashPassword(pass);

        User newUser = new User(user.getDisplayName(), user.getEmail(), hashPass);

        newUser.setStatus(UserConstans.STATUS_ACTIVE);
        newUser.setTags(UserConstans.TAG_PENDING);

        HashMap<String, String> keyPair = KeyStoreUtil.generateKeyPair();
        KeyStoreUtil.writePrivateKeysToFile(context, keyPair.get("Private"), user);
        newUser.setPublicKey( keyPair.get("Public"));

        UserApiHelper userApiHelper =new UserApiHelper();
        userApiHelper.registerUser(context, newUser);

        addUser(newUser, context, null);
    }
}
