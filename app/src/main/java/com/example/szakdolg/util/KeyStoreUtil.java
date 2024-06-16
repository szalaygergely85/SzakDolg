package com.example.szakdolg.util;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.Base64;

public class KeyStoreUtil {

    private static final String TAG = "KeyStoreUtil";
    private static final String KEYSTORE_PROVIDER = "PKCS12";


    public static void savePublicKey(String publicKeyString, String keyId) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
        keyStore.load(null);

        if (keyStore.containsAlias(keyId)) {
            keyStore.deleteEntry(keyId);
        }
        X509Certificate certificate = generateCertificate(publicKey);

        keyStore.setCertificateEntry(keyId, certificate);
    }

    public static void savePrivateKey(String privateKeyString, String keyId) throws Exception {

        PrivateKey privateKey = stringToPrivateKey(privateKeyString);

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
        keyStore.load(null);

        if (keyStore.containsAlias(keyId)) {
            keyStore.deleteEntry(keyId);
        }
        keyStore.setKeyEntry(keyId, privateKey, null, null);
    }

    public static PublicKey getPublicKey(String keyId) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
        keyStore.load(null);
        Certificate cert = keyStore.getCertificate(keyId);
        if (cert != null) {
            return cert.getPublicKey();
        }
        return null;
    }
    
    public static PrivateKey getPrivateKey(String keyId) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
        keyStore.load(null);
        return (PrivateKey) keyStore.getKey(keyId, null);
    }

    public static PublicKey stringToPublicKey(String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    public static PrivateKey stringToPrivateKey(String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private static X509Certificate generateCertificate(PublicKey publicKey) throws Exception {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        InputStream in = new ByteArrayInputStream(publicKey.getEncoded());
        return (X509Certificate) certFactory.generateCertificate(in);
    }

}
