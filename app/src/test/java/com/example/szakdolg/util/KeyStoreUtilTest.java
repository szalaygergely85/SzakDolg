package com.example.szakdolg.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import android.content.Context;
import com.example.szakdolg.model.user.model.User;
import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

public class KeyStoreUtilTest {

   private KeyPairGenerator keyPairGeneratorMock;
   private KeyPair keyPairMock;
   private PublicKey publicKeyMock;
   private PrivateKey privateKeyMock;

   @Mock
   private Context mockContext;

   @Mock
   private User mockUser;

   @BeforeEach
   public void setUp() throws Exception {
      mockContext = mock(Context.class);
      mockUser = mock(User.class);

      keyPairGeneratorMock = mock(KeyPairGenerator.class);
      keyPairMock = mock(KeyPair.class);
      publicKeyMock = mock(PublicKey.class);
      privateKeyMock = mock(PrivateKey.class);

      when(keyPairGeneratorMock.generateKeyPair()).thenReturn(keyPairMock);
      when(keyPairMock.getPublic()).thenReturn(publicKeyMock);
      when(keyPairMock.getPrivate()).thenReturn(privateKeyMock);
   }

   @Test
   public void testGenerateKeyPair() throws Exception {
      // Mock static methods from KeyPairGenerator
      try (
         MockedStatic<KeyPairGenerator> generatorMockedStatic = mockStatic(
            KeyPairGenerator.class
         )
      ) {
         generatorMockedStatic
            .when(() -> KeyPairGenerator.getInstance("RSA"))
            .thenReturn(keyPairGeneratorMock);

         // Mock the encoded keys
         byte[] publicKeyBytes = "publicKey".getBytes();
         byte[] privateKeyBytes = "privateKey".getBytes();

         when(publicKeyMock.getEncoded()).thenReturn(publicKeyBytes);
         when(privateKeyMock.getEncoded()).thenReturn(privateKeyBytes);

         // Call the method under test
         HashMap<String, String> result = KeyStoreUtil.generateKeyPair();

         // Verify the results
         assertNotNull(result);
         assertEquals(2, result.size());
         assertEquals(
            Base64.getEncoder().encodeToString(publicKeyBytes),
            result.get("Public")
         );
         assertEquals(
            Base64.getEncoder().encodeToString(privateKeyBytes),
            result.get("Private")
         );
      }
   }

   @Test
   public void testWritePrivateKeysToFile() {
      String privateKey = "samplePrivateKey";
      String email = "test@example.com";

      File testDir = mockContext.getDir("testDir", Context.MODE_PRIVATE);

      when(mockContext.getCacheDir()).thenReturn(testDir);

      File privateKeyFile = new File(testDir, email + ".dat");
      when(mockUser.getEmail()).thenReturn(email);

      // Mocking static method FileUtil.writeStringToFile
      try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
         // Call the method under test
         KeyStoreUtil.writePrivateKeysToFile(mockContext, privateKey, mockUser);

         // Verify that FileUtil.writeStringToFile was called with the correct arguments
         mockedFileUtil.verify(() ->
            FileUtil.writeStringToFile(privateKey, privateKeyFile)
         );
      }
   }
}
