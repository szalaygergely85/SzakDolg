package com.zen_vy.chat.util;

public class KeyStoreUtilTest {}
/*
private KeyPairGenerator keyPairGeneratorMock;
private KeyPair keyPairMock;
private PublicKey publicKeyMock;
private PrivateKey privateKeyMock;

@Mock
private Context mockContext;

@Mock
private User mockUser;


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
}*/
