package com.example.szakdolg.util;


import java.security.PrivateKey;
import java.security.PublicKey;
import android.util.Base64;


import javax.crypto.Cipher;

public class EncryptionHelper {

    private static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String ENCRYPTED_PREFIX = "ENC:";

    public static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return ENCRYPTED_PREFIX +  Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public static String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
        if (!isEncrypted(encryptedData)) {
            return encryptedData; // If not encrypted, return as is
        }

        String base64Data = encryptedData.substring(ENCRYPTED_PREFIX.length()); // Remove prefix
        byte[] encryptedBytes = Base64.decode(base64Data, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, "UTF-8");
    }

    public static String decrypt(String encryptedData) throws Exception {
        PrivateKey privateKey = KeyStoreUtil.getPrivateKey();
        return decrypt(encryptedData, privateKey);
    }

    public static boolean isEncrypted(String data) {
        if (!data.startsWith(ENCRYPTED_PREFIX)) {
            return false;
        }
        try {
            Base64.decode(data.substring(ENCRYPTED_PREFIX.length()), Base64.DEFAULT);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
