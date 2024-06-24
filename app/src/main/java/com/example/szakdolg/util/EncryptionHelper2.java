package com.example.szakdolg.util;


import static com.example.szakdolg.util.KeyStoreUtil2.getPrivateKey;


import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EncryptionHelper2 {

    public static String encrypt(String message, String publicKeyString) {
        PublicKey publicKey = null;
        Cipher encryptCipher = null;
        String encodedMessage = "null";
        byte[] secretMessageBytes = message.getBytes(StandardCharsets.UTF_8);
        try {
            publicKey = KeyStoreUtil2.getPublicKeyFromString(publicKeyString);
            encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
            encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return encodedMessage;
    }

    public static byte[] stringTOByte(String text) {
        byte[] message = Base64.getDecoder().decode(text.getBytes());
        return message;
    }

    public static String decrypt(String message, String privateKeyString) {
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
