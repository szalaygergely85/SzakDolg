package com.zen_vy.chat.activity;


import static android.provider.Settings.System.getString;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.mockito.ArgumentMatchers.matches;

import android.content.Context;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.platform.tracing.Tracer;


import com.google.android.material.textfield.TextInputLayout;
import com.zen_vy.chat.R;
import com.zen_vy.chat.TestUtil;
import com.zen_vy.chat.activity.register.RegisterActivity;
import com.zen_vy.chat.constans.SharedPreferencesConstants;

import com.zen_vy.chat.util.RandomUtil;
import com.zen_vy.chat.util.SharedPreferencesUtil;

import org.hamcrest.Description;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class RegisterActivityTest {

	private Context context;

	private Long userId;

@Before
public void setUp() {
	context = InstrumentationRegistry.getInstrumentation().getTargetContext();
	ActivityScenario.launch(RegisterActivity.class);
}

@After
public void tearDown() {
}


	@Test
	public void testRegisterUIElements() {
		onView(withId(R.id.edtRegEmail)).check(matches(isDisplayed()));

		// Check if first password field is displayed
		onView(withId(R.id.edtRegPass1)).check(matches(isDisplayed()));

		// Check if second password field is displayed
		onView(withId(R.id.edtRegPass2)).check(matches(isDisplayed()));

		// Check if display name field is displayed
		onView(withId(R.id.edtRegName)).check(matches(isDisplayed()));

		// Check if register button is displayed
		onView(withId(R.id.btnRegReg)).check(matches(isDisplayed()));

		// Check if sign-in text is displayed
		onView(withId(R.id.textSignIn)).check(matches(isDisplayed()));


	}

	@Test
	public void testMissingFieldsError() {

		onView(withId(R.id.btnRegReg)).perform(click());

		onView(withId(R.id.edtRegName))
				.check(matches(hasTextInputLayoutErrorText(context.getString(R.string.display_name_is_required))));

		onView(withId(R.id.edtRegEmail))
				.check(matches(hasTextInputLayoutErrorText(context.getString(R.string.email_is_required))));

		onView(withId(R.id.edtRegPass1))
				.check(matches(hasTextInputLayoutErrorText(context.getString(R.string.password_is_required))));

		onView(withId(R.id.edtRegPass2))
				.check(matches(hasTextInputLayoutErrorText(context.getString(R.string.confirm_password_is_required))));
	}

	@Test
	public void testPasswordDoNotMatch() {


		onView(withId(R.id.editTextRegEmail)).perform(ViewActions.replaceText(TestUtil.createRandomEmail()));
		onView(withId(R.id.editTextRegName))
				.perform(ViewActions.replaceText(RandomUtil.getRandomString(5)));
		onView(withId(R.id.editTextPass1))
				.perform(ViewActions.replaceText(RandomUtil.getRandomString(5)));
		onView(withId(R.id.editTextPass2))
				.perform(ViewActions.replaceText(RandomUtil.getRandomString(6)));

		onView(withId(R.id.btnRegReg)).perform(click());

		onView(withId(R.id.edtRegPass2))
				.check(matches(hasTextInputLayoutErrorText(context.getString(R.string.passwords_dont_match))));
	}

	@Test
	public void testPasswordTooShort() {


		onView(withId(R.id.editTextRegEmail)).perform(ViewActions.replaceText(TestUtil.createRandomEmail()));
		onView(withId(R.id.editTextRegName))
				.perform(ViewActions.replaceText(RandomUtil.getRandomString(10)));
		String shortPassword = RandomUtil.getRandomString(3);
		onView(withId(R.id.editTextPass1))
				.perform(ViewActions.replaceText(shortPassword));
		onView(withId(R.id.editTextPass2))
				.perform(ViewActions.replaceText(shortPassword));

		onView(withId(R.id.btnRegReg)).perform(click());

		onView(withId(R.id.edtRegPass1))
				.check(matches(hasTextInputLayoutErrorText(context.getString(R.string.password_too_short))));
	}

	@Test
	public void testValidEmailAddress() {

		onView(withId(R.id.editTextRegEmail))
				.perform(ViewActions.replaceText(RandomUtil.getRandomString(5)));
		onView(withId(R.id.editTextRegName))
				.perform(ViewActions.replaceText(RandomUtil.getRandomString(10)));
		onView(withId(R.id.editTextPass1))
				.perform(ViewActions.replaceText(RandomUtil.getRandomString(10)));
		onView(withId(R.id.editTextPass2))
				.perform(ViewActions.replaceText(RandomUtil.getRandomString(10)));

		// Leave the email and password empty and click the login button
		onView(withId(R.id.btnRegReg)).perform(click());

		onView(withId(R.id.edtRegEmail))
				.check(matches(hasTextInputLayoutErrorText(context.getString(R.string.email_invalid))));
	}

	@Test
	public void testExistingEmailAddress() {

	String email = TestUtil.createRandomEmail();
	String pass = RandomUtil.getRandomString(10);

		TestUtil.addUser(email, context);

		onView(withId(R.id.editTextRegEmail))
				.perform(ViewActions.replaceText(email));
		onView(withId(R.id.editTextRegName))
				.perform(ViewActions.replaceText(RandomUtil.getRandomString(10)));
		onView(withId(R.id.editTextPass1))
				.perform(ViewActions.replaceText(pass));
		onView(withId(R.id.editTextPass2))
				.perform(ViewActions.replaceText(pass));


		onView(withId(R.id.btnRegReg)).perform(click());

		onView(withId(R.id.edtRegEmail))
				.check(matches(hasTextInputLayoutErrorText(context.getString(R.string.email_is_already_registered))));

TestUtil.deleteUser(email, context);
}


	@Test
	public void registerNewUser_Success_RedirectsToProfile() throws InterruptedException {
		onView(withId(R.id.editTextRegEmail)).perform(replaceText("test"+ RandomUtil.getRandomString(5) +"@zenvy.com"));
		onView(withId(R.id.editTextPass1)).perform(replaceText("123456"));
		onView(withId(R.id.editTextPass2)).perform(replaceText("123456"));
		onView(withId(R.id.editTextRegName)).perform(replaceText("Test"), closeSoftKeyboard());

		onView(withId(R.id.btnRegReg)).perform(click());


		Thread.sleep(5000);



		String token = SharedPreferencesUtil.getStringPreference(context, SharedPreferencesConstants.USERTOKEN);
		userId = SharedPreferencesUtil.getLongPreference(context, SharedPreferencesConstants.USER_ID);
		assert token != null && !token.isEmpty();

		TestUtil.deleteUser(userId, context);
	}


	private Matcher<View> hasTextInputLayoutErrorText(final String expectedError) {
		return new BoundedMatcher<View, TextInputLayout>(TextInputLayout.class) {
			@Override
			public void describeTo(Description description) {
				description.appendText("with error: " + expectedError);
			}

			@Override
			protected boolean matchesSafely(TextInputLayout item) {
				CharSequence error = item.getError();
				return error != null && error.toString().equals(expectedError);
			}
		};
	}


}
