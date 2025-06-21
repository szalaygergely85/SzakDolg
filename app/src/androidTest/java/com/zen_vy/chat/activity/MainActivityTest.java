package com.zen_vy.chat.activity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.zen_vy.chat.DTO.ConversationDTO;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.main.MainActivity;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.testhelpers.ApiHelper;
import com.zen_vy.chat.testhelpers.TestUtil;
import com.zen_vy.chat.util.DateTimeUtil;
import com.zen_vy.chat.util.RandomUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

   private Context context;

   private User testUser;

   @Rule
   public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
      android.Manifest.permission.CAMERA,
      android.Manifest.permission.RECORD_AUDIO,
      android.Manifest.permission.READ_EXTERNAL_STORAGE,
      android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
      android.Manifest.permission.POST_NOTIFICATIONS // if targeting Android 13+
   );

   @Before
   public void setUp() throws InterruptedException, IOException {
      context = InstrumentationRegistry.getInstrumentation().getTargetContext();

      TestUtil.performLogin(context);

      testUser = ApiHelper.getUserByToken(TestUtil.TEST_TOKEN);
   }

   @After
   public void cleanUp() {
      TestUtil.deleteSharedPreferences(context);
   }

   @Test
   public void testEmptyUiElements() {
      ActivityScenario.launch(MainActivity.class);

      onView(withId(R.id.txtEmptyMain)).check(matches(isDisplayed()));
      onView(withId(R.id.logoImage)).check(matches(isDisplayed()));
      onView(withId(R.id.appBarLayout)).check(matches(isDisplayed()));
      onView(withId(R.id.btnNewConv)).check(matches(isDisplayed()));
      onView(withId(R.id.bottom_nav_main)).check(matches(isDisplayed()));
   }

   @Test
   public void testWithConversations() throws IOException {
      User testUser2 = ApiHelper.addUser(
         new User(
            RandomUtil.getRandomString(5),
            TestUtil.createRandomEmail(),
            RandomUtil.getRandomString(5),
            RandomUtil.getRandomString(5),
            null,
            null,
            RandomUtil.getRandomLong(),
            RandomUtil.getRandomString(5)
         )
      );

      List<Long> userIds = Arrays.asList(
         testUser.getUserId(),
         testUser2.getUserId()
      );
      ConversationDTO conversationDTO = ApiHelper.addConversationByUserIds(
         userIds,
         testUser.getToken()
      );

      MessageEntry lastMessage = ApiHelper.addMessage(
         new MessageEntry(
            conversationDTO.getConversationId(),
            testUser2.getUserId(),
            DateTimeUtil.now(),
            "Test Message",
            MessageTypeConstants.MESSAGE,
            RandomUtil.getRandomString(5)
         ),
         testUser.getToken()
      );

      ActivityScenario.launch(MainActivity.class);

      onView(withId(R.id.messageBoardRecView))
         .perform(scrollTo(hasDescendant(withText("Test Message"))));

      onView(withText("Test Message")).check(matches(isDisplayed()));

      ApiHelper.deleteUser(testUser2.getEmail(), context);
      ApiHelper.deleteMessage(lastMessage, context, testUser);
   }
}
