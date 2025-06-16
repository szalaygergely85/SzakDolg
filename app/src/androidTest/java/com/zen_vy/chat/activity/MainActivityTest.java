package com.zen_vy.chat.activity;



import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.zen_vy.chat.R;
import com.zen_vy.chat.TestUtil;
import com.zen_vy.chat.activity.main.MainActivity;
import com.zen_vy.chat.activity.register.RegisterActivity;
import com.zen_vy.chat.constans.SharedPreferencesConstants;

import com.zen_vy.chat.models.user.service.UserService;
import com.zen_vy.chat.util.SharedPreferencesUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

    private Context context;

    private String email;

    private String uuid;

    @Before
    public void setUp() throws InterruptedException {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        TestUtil.performLogin(context);

        ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void cleanUp(){
        deleteSharedPreferences(context);

    }

    private void deleteSharedPreferences(Context context) {

          SharedPreferencesUtil.deletePreference(context, SharedPreferencesConstants.USERTOKEN);

          SharedPreferencesUtil.deletePreference(context, SharedPreferencesConstants.UUID);

    }



    @Test
    public void testEmptyUiElements(){
        onView(withId(R.id.topAppBar)).check(matches(isDisplayed()));
    }
}
