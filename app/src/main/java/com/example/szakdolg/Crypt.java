package com.example.szakdolg;


import android.util.Log;


import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.Base64;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Crypt {

    /**
     * I ll need to store the keys in hashmap
     *   HashMap<String, String> keyPair = new HashMap<String, String>();
     *         keyPair = Crypt.createKey();
     * @return HashMap
     */
    public static HashMap createKeys() {
        HashMap<String, String> keyPair = new HashMap<String, String>();
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        keyPair.put("Private", privateKeyString);
        keyPair.put("Public", publicKeyString);

        return keyPair;
    }
    public static PublicKey getPublicKey(String publicK) {
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
    public static String enCrypt(String message, String publicKeyString) {
        PublicKey publicKey = null;
        Cipher encryptCipher = null;
        String encodedMessage = "null";
        byte[] secretMessageBytes = message.getBytes(StandardCharsets.UTF_8);
        try {
            publicKey = getPublicKey(publicKeyString);
            encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
            encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
            ;
            Log.d("fasza", encodedMessage);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return encodedMessage;
    }
    public static byte[] stringTOByte(String text) {
        byte[] message = Base64.getDecoder().decode(text.getBytes());
        return message;
    }
    public static String deCrypt(String message, String privateKeyString) {
        Cipher decryptCipher = null;
        PrivateKey privateKey = null;
        byte[] decryptedMessageBytes = null;
        String decryptedMessage = null;
        byte[] encryptedMessageBytes = stringTOByte(message);
        try {
            privateKey = getPrivateKey(privateKeyString);
            decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
            decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return decryptedMessage;
    }
}
