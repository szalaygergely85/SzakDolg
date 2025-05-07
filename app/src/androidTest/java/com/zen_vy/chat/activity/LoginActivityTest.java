package com.zen_vy.chat.activity;

import androidx.test.filters.MediumTest;

@MediumTest
public class LoginActivityTest {
   /**
private UserApiHelper mockUserApiHelper;

@Rule
public ActivityScenarioRule<LoginActivity> activityRule =
	new ActivityScenarioRule<>(LoginActivity.class);

@Before
public void setUp() {
	Intents.init();
}

@After
public void tearDown() {
	Intents.release();
}

@Test
public void testLoginUIElements() {
	onView(ViewMatchers.withId(R.id.edtLgnEmail))
		.check(matches(isDisplayed()));

	// Check that the password field is displayed
	onView(withId(R.id.edtLgnPass)).check(matches(isDisplayed()));

	// Check that the login button is displayed
	onView(withId(R.id.btnLgnLogin)).check(matches(isDisplayed()));
}

/*
@Test
public void testSuccessfulLoginWithoutError() {
	Context context = ApplicationProvider.getApplicationContext();
	mockUserApiHelper = mock(UserApiHelper.class);
	doNothing()
		.when(mockUserApiHelper)
		.loginUser(
			eq(context),
			eq("hashed_password"), // This is just for simulation
			eq("test@example.com")
		);

	// Launch LoginActivity and inject the mockUserApiHelper
	ActivityScenario.launch(LoginActivity.class);

	// Input valid email and password
	onView(withId(R.id.edtLgnEmail))
		.perform(ViewActions.replaceText("test@example.com"));
	onView(withId(R.id.edtLgnPass))
		.perform(ViewActions.replaceText("password"));

	// Click the login button
	onView(withId(R.id.btnLgnLogin)).perform(click());

	// Check if the error message is hidden
	onView(withId(R.id.txtLgnError))
		.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
}

@Test
public void testMissingFieldsError() {
	ActivityScenario.launch(LoginActivity.class);

	// Initially, the error message should be GONE
	onView(withId(R.id.txtLgnError))
		.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

	// Leave the email and password empty and click the login button
	onView(withId(R.id.btnLgnLogin)).perform(click());

	// After clicking the login button, the error message should become VISIBLE
	onView(withId(R.id.txtLgnError))
		.check(matches(isDisplayed()))
		.check(matches(withText("Email and password are required.")));
}*/
}
