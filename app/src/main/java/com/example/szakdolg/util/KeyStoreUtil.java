package com.example.szakdolg.util;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.X509EncodedKeySpec;

public class KeyStoreUtil {


    private static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String TAG = "KeyStoreUtil";
    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final String KEY_ALIAS = "my_key_alias";

    // Generate and store a new key pair in the Keystore
    public static void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER);
            keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Log.d(TAG, "Key pair generated and stored in Keystore");
        } catch (Exception e) {
            Log.e(TAG, "Exception generating key pair", e);
        }
    }

    public static String getPublicKeyAsString() {
        try {
            PublicKey publicKey = getPublicKey();
            return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Exception retrieving public key", e);
            return null;
        }
    }

    public static PublicKey getPublicKey() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
        keyStore.load(null);
        return  keyStore.getCertificate(KEY_ALIAS).getPublicKey();
    }

    public static PrivateKey getPrivateKey() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry(KEY_ALIAS, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            Log.e(TAG, "Not an instance of a PrivateKeyEntry");
            return null;
        }
        return  ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
    }

    public static PublicKey getPublicKeyFromString(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA);
        return keyFactory.generatePublic(spec);
    }

}
