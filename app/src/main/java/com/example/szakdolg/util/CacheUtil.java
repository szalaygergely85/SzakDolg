package com.example.szakdolg.util;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.db.util.MessageDatabaseUtil;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.message.MessageEntry;
import com.example.szakdolg.user.entity.User;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CacheUtil {

   private static final String TAG = "CacheUtil";

   public static void writePublicKeysCache(Context context, User user) {
      File cacheDir = context.getCacheDir();
      File hashMapFile = new File(cacheDir, "public_cache.dat");
      HashMap<String, String> keys = new HashMap<>();

      keys = FileUtil.readHashMapFromFile(hashMapFile);
      if (keys != null) {
         if (keys.containsKey(user.getEmail())) {
            return;
         }
      }
      keys = new HashMap<>();
      keys.put(user.getEmail(), user.getPublicKey());
      Log.e(TAG, keys.toString());
      // Write HashMap to file
      FileUtil.writeHashMapToFile(keys, hashMapFile);
   }

   public static String getPublicKeyFromCache(Context context, String email) {
      File cacheDir = context.getCacheDir();
      File hashMapFile = new File(cacheDir, "public_cache.dat");
      HashMap<String, String> keys = FileUtil.readHashMapFromFile(hashMapFile);

      return keys.get(email);
   }

   public static void writePrivateKeysCache(
      Context context,
      String privateKey,
      User user
   ) {
      File cacheDir = context.getCacheDir();
      File privateKeyFile = new File(cacheDir, user.getEmail() + ".dat");
      FileUtil.writeStringToFile(privateKey, privateKeyFile);
   }

   public static String getPrivateKeyFromCache(Context context, User user) {
      File cacheDir = context.getCacheDir();
      File privateKeyFile = new File(cacheDir, user.getEmail() + ".dat");

      String privateKey = FileUtil.readStringFromFile(privateKeyFile);

      return privateKey;
   }

   public static void validateMessages(
      ArrayList<MessageEntry> messageEntries,
      Context context
   ) {
      MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(context);
      List<String> localMessageUUIds = messageDatabaseUtil.getAllMessageUuids();

      if (messageEntries == null || messageEntries.isEmpty()) {
         throw new IllegalArgumentException(
            "messageEntries should not be null or empty"
         );
      }

      for (MessageEntry messageEntry : messageEntries) {
         if (!localMessageUUIds.contains(messageEntry.getuUId())) {
            messageDatabaseUtil.insertMessageEntry(messageEntry);
         }
      }
   }

   public static void validateContacts(
      ArrayList<User> userEntries,
      Context context
   ) {
      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(context);
      List<Long> localUserIds = userDatabaseUtil.getAllUserIds();

      if (userEntries == null || userEntries.isEmpty()) {
         throw new IllegalArgumentException(
            "messageEntries should not be null or empty"
         );
      }
      for (User user : userEntries) {
         if (!localUserIds.contains(user.getUserId())) {
            userDatabaseUtil.insertUser(user);
         }
      }
   }
}
