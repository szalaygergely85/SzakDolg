package com.example.szakdolg.user.service;

import android.content.Context;

import com.example.szakdolg.user.api.UserApiHelper;
import com.example.szakdolg.user.model.User;
import com.example.szakdolg.util.HashUtils;
import com.example.szakdolg.util.KeyStoreUtil;

import java.util.HashMap;

public class UserService {
    public User addUser(User user, Context context){
        String hashPass = HashUtils.hashPassword(user.getPassword());

        HashMap<String, String> keyPair = KeyStoreUtil.generateKeyPair();


        KeyStoreUtil.writePrivateKeysToFile(context, keyPair.get("Private"), user);

        User newUser = new User( user.getDisplayName(), hashPass, keyPair.get("Public"));
        UserApiHelper userApiHelper =new UserApiHelper();
        userApiHelper.registerUser(context, newUser);

        return newUser;
    }
}
