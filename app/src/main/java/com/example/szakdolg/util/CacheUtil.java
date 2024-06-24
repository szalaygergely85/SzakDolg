package com.example.szakdolg.util;

import android.content.Context;
import android.util.Log;

import com.example.szakdolg.user.User;

import java.io.File;
import java.util.HashMap;


public class CacheUtil {

    private static final String TAG = "CacheUtil";

    public static void writePublicKeysCache(Context context, User user) {

        File cacheDir = context.getCacheDir();
        File hashMapFile = new File(cacheDir, "public_cache.dat");
        HashMap<String, String> keys = new HashMap<>();

        keys = FileUtil.readHashMapFromFile(hashMapFile);
        if(keys!=null){
            if(keys.containsKey(user.getEmail())) {
               return;
            }
        }
        keys = new HashMap<>();
        keys.put(user.getEmail(), user.getPublicKey());
        Log.e(TAG, keys.toString());
        // Write HashMap to file
        FileUtil.writeHashMapToFile(keys, hashMapFile);
    }
    public static String getPublicKeyFromCache(Context context, String email){
        File cacheDir = context.getCacheDir();
        File hashMapFile = new File(cacheDir, "public_cache.dat");
        HashMap<String, String> keys = FileUtil.readHashMapFromFile(hashMapFile);

        return keys.get(email);

    }

    public static void writePrivateKeysCache(Context context, String privateKey, User user) {

        File cacheDir = context.getCacheDir();
        File privateKeyFile = new File(cacheDir, user.getEmail() + ".dat");
        FileUtil.writeStringToFile(privateKey, privateKeyFile);
    }

    public static String getPrivateKeyFromCache(Context context, User user){
        File cacheDir = context.getCacheDir();
        File privateKeyFile = new File(cacheDir, user.getEmail()  + ".dat");

        String privateKey = FileUtil.readStringFromFile(privateKeyFile);

        return privateKey;
    }
}
