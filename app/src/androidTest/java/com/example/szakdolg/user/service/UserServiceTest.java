package com.example.szakdolg.user.service;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.szakdolg.model.user.api.UserApiHelper;
import com.example.szakdolg.model.user.service.UserService;
import com.example.szakdolg.util.HashUtils;
import com.example.szakdolg.util.KeyStoreUtil;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class UserServiceTest {

   @Mock
   private Context mockContext;

   @Mock
   private UserApiHelper mockUserApiHelper;

   @Mock
   private KeyStoreUtil mockKeyStoreUtil;

   @Mock
   private HashUtils mockHashUtils;

   private UserService userService;

   @Before
   public void setUp() {
      MockitoAnnotations.openMocks(this);
      userService = new UserService(mockContext);
   }
   /*
		@Test
		public void testAddUser() {
			// Arrange
			User testUser = new User("TestUser", "test@gmail.com", "password123");
			String expectedHashedPassword = "hashedPassword";
			HashMap<String, String> keyPair = new HashMap<>();
			keyPair.put("Private", "privateKey");
			keyPair.put("Public", "publicKey");

			// Mocking static methods (using PowerMockito if necessary)
			when(HashUtils.hashPassword(testUser.getPassword())).thenReturn(expectedHashedPassword);
			when(KeyStoreUtil.generateKeyPair()).thenReturn(keyPair);

			doNothing().when(KeyStoreUtil.class);
			KeyStoreUtil.writePrivateKeysToFile(mockContext, "privateKey", testUser);

			doNothing().when(mockUserApiHelper).registerUser(any(Context.class), any(User.class));

			// Act
			User newUser = userService.addUser(testUser, mockContext, testUser);

			// Assert
			assertNotNull(newUser);
			assertEquals("TestUser", newUser.getDisplayName());
			assertEquals(expectedHashedPassword, newUser.getPassword());
			assertEquals("publicKey", newUser.getPublicKey());


			verify(mockUserApiHelper).registerUser(mockContext, newUser);
		}*/
}
