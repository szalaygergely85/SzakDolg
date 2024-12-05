package com.example.szakdolg.db.util;

import static org.junit.Assert.*;

public class ProfileDatabaseUtilTest {
   /*
	private ProfileDatabaseUtil profileDatabaseUtil;
	private SQLiteDatabase db;
	private SQLiteOpenHelper dbHelper;

	@Before
	public void setUp() {
		// Create an in-memory database, which is faster and cleaner
		Context context = ApplicationProvider.getApplicationContext();
		dbHelper = new DatabaseHelper(context, "test_user"); // You can pass any ID here
		db = dbHelper.getWritableDatabase();

		// Create an instance of ProfileDatabaseUtil with the in-memory database
		profileDatabaseUtil = new ProfileDatabaseUtil(context, "test_user");
	}

	@Test
	public void testInsertAndRetrieveUser() {
		// Create a test user
		User testUser = new User(1L, "TestDisplayName", "TestFullName", "test@example.com", 987654321L);
		String testToken = "test_token";

		// Insert the user into the database
		profileDatabaseUtil.insertProfile(testUser, testToken);

		// Retrieve the user using the token
		User retrievedUser = profileDatabaseUtil.getCurrentUserByToken(testToken);

		// Assert that the inserted and retrieved users match
		assertNotNull(retrievedUser);
		assertEquals(testUser.getUserId(), retrievedUser.getUserId());
		assertEquals(testUser.getDisplayName(), retrievedUser.getDisplayName());
		assertEquals(testUser.getEmail(), retrievedUser.getEmail());

	}

	@After
	public void tearDown() {
		// Clean up the database by closing it
		db.close();
		dbHelper.close();
	}*/
}
