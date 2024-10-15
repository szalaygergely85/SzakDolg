package com.example.szakdolg.activity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import com.example.szakdolg.R;
import com.example.szakdolg.user.api.UserApiHelper;
import com.example.szakdolg.util.RandomUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class RegisterActivityTest {

   public final String displayName = "Test";
   public final String email = "test@gmail.com";
   public final String password = "password";

   private UserApiHelper mockUserApiHelper;

   @Rule
   public ActivityScenarioRule<RegisterActivity> activityRule =
      new ActivityScenarioRule<>(RegisterActivity.class);

   @Before
   public void setUp() {
      Intents.init();
   }

   @After
   public void tearDown() {
      Intents.release();
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

      onView(withId(R.id.txtRegError))
         .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
   }

   @Test
   public void testMissingFieldsError() {
      //ActivityScenario.launch(LoginActivity.class);

      // Initially, the error message should be GONE
      onView(withId(R.id.txtRegError))
         .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

      // Leave the email and password empty and click the login button
      onView(withId(R.id.btnRegReg)).perform(click());

      // After clicking the login button, the error message should become VISIBLE
      onView(withId(R.id.txtRegError))
         .check(matches(isDisplayed()))
         .check(matches(withText("Please fill in all fields.")));
   }

   @Test
   public void testPasswordDoNotMatch() {
      // Initially, the error message should be GONE
      onView(withId(R.id.txtRegError))
         .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

      onView(withId(R.id.edtRegEmail)).perform(ViewActions.replaceText(email));
      onView(withId(R.id.edtRegName))
         .perform(ViewActions.replaceText(displayName));
      onView(withId(R.id.edtRegPass1))
         .perform(ViewActions.replaceText(password));
      onView(withId(R.id.edtRegPass2))
         .perform(ViewActions.replaceText(RandomUtil.getRandomString(6)));

      // Leave the email and password empty and click the login button
      onView(withId(R.id.btnRegReg)).perform(click());

      // After clicking the login button, the error message should become VISIBLE
      onView(withId(R.id.txtRegError))
         .check(matches(isDisplayed()))
         .check(matches(withText("Passwords do not match. Please try again.")));
   }

   @Test
   public void testPasswordTooShort() {
      // Initially, the error message should be GONE
      onView(withId(R.id.txtRegError))
         .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

      onView(withId(R.id.edtRegEmail)).perform(ViewActions.replaceText(email));
      onView(withId(R.id.edtRegName))
         .perform(ViewActions.replaceText(displayName));
      String shortPassword = RandomUtil.getRandomString(3);
      onView(withId(R.id.edtRegPass1))
         .perform(ViewActions.replaceText(shortPassword));
      onView(withId(R.id.edtRegPass2))
         .perform(ViewActions.replaceText(shortPassword));

      // Leave the email and password empty and click the login button
      onView(withId(R.id.btnRegReg)).perform(click());

      // After clicking the login button, the error message should become VISIBLE
      onView(withId(R.id.txtRegError))
         .check(matches(isDisplayed()))
         .check(
            matches(withText("Password must be at least 6 characters long."))
         );
   }

   @Test
   public void testValidEmailAddress() {
      // Initially, the error message should be GONE
      onView(withId(R.id.txtRegError))
         .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

      onView(withId(R.id.edtRegEmail))
         .perform(ViewActions.replaceText(RandomUtil.getRandomString(5)));
      onView(withId(R.id.edtRegName))
         .perform(ViewActions.replaceText(displayName));
      onView(withId(R.id.edtRegPass1))
         .perform(ViewActions.replaceText(password));
      onView(withId(R.id.edtRegPass2))
         .perform(ViewActions.replaceText(password));

      // Leave the email and password empty and click the login button
      onView(withId(R.id.btnRegReg)).perform(click());

      // After clicking the login button, the error message should become VISIBLE
      onView(withId(R.id.txtRegError))
         .check(matches(isDisplayed()))
         .check(matches(withText("Please enter a valid email address.")));
   }
}
