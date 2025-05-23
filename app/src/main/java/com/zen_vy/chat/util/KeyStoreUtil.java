package com.zen_vy.chat.util;

import android.content.Context;
import android.util.Log;
import com.zen_vy.chat.models.user.entity.User;
import java.io.File;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

public class KeyStoreUtil {

   private static final String TAG = "Crypt";

   public static HashMap generateKeyPair() {
      HashMap<String, String> keyPair = new HashMap<>();
      KeyPairGenerator generator = null;
      try {
         generator = KeyPairGenerator.getInstance("RSA");
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
      }
      if (generator != null) {
         generator.initialize(2048);
         KeyPair pair = generator.generateKeyPair();
         PrivateKey privateKey = pair.getPrivate();
         PublicKey publicKey = pair.getPublic();
         String publicKeyString = Base64
            .getEncoder()
            .encodeToString(publicKey.getEncoded());
         String privateKeyString = Base64
            .getEncoder()
            .encodeToString(privateKey.getEncoded());
         keyPair.put("Private", privateKeyString);
         keyPair.put("Public", publicKeyString);
      }
      return keyPair;
   }

   public static PublicKey getPublicKeyFromString(String publicK) {
      Log.d(TAG, "getPublicKey: " + publicK);
      PublicKey pubKey = null;
      try {
         byte[] publicBytes = Base64.getDecoder().decode(publicK);
         X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         pubKey = keyFactory.generatePublic(keySpec);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return pubKey;
   }

   public static PrivateKey getPrivateKey(String privateK) {
      PrivateKey prvKey = null;
      try {
         byte[] privateBytes = Base64.getDecoder().decode(privateK);
         PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         prvKey = keyFactory.generatePrivate(keySpec);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return prvKey;
   }

   public static String getPrivateKeyFromFile(Context context, User user) {
      File cacheDir = context.getCacheDir();
      File privateKeyFile = new File(cacheDir, user.getUserId() + ".dat");

      String privateKey = FileUtil.readStringFromFile(privateKeyFile);

      return privateKey;
   }

   public static void writePrivateKeysToFile(
      Context context,
      String privateKey,
      User user
   ) {
      File cacheDir = context.getCacheDir();
      File privateKeyFile = new File(cacheDir, user.getUserId() + ".dat");
      FileUtil.writeStringToFile(privateKey, privateKeyFile);
   }
}
