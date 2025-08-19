package com.zen_vy.chat.activity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.main.MainActivity;
import com.zen_vy.chat.models.contacts.dto.ConversationDTO;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.testhelpers.ApiHelper;
import com.zen_vy.chat.testhelpers.TestUtil;
import com.zen_vy.chat.util.DateTimeUtil;
import com.zen_vy.chat.websocket.WebSocketService;
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

   private User testUser2;
   private User testUser3;

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

      testUser = TestUtil.createRandomUserAndLogIn(context);

      testUser2 = ApiHelper.addUser(TestUtil.getRandomUser());
      testUser3 = ApiHelper.addUser(TestUtil.getRandomUser());
   }

   @After
   public void cleanUp() {
      TestUtil.deleteSharedPreferences(context);
      ApiHelper.deleteUser(testUser.getEmail(), context);
      ApiHelper.deleteUser(testUser2.getEmail(), context);
      ApiHelper.deleteUser(testUser3.getEmail(), context);
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
   public void testConversationWithTwoParticipant() throws IOException {
      ConversationDTO conversationDTO = new ConversationDTO();

      MessageEntry lastMessage = new MessageEntry();
      try {
         List<Long> userIds = Arrays.asList(
            testUser.getUserId(),
            testUser2.getUserId()
         );
         conversationDTO =
         ApiHelper.addConversationByUserIds(userIds, testUser.getToken());

         lastMessage =
         ApiHelper.addMessage(
            TestUtil.getRandomMessage(
               conversationDTO.getConversationId(),
               testUser2.getUserId()
            ),
            testUser.getToken()
         );

         ActivityScenario.launch(MainActivity.class);

         onView(withId(R.id.messageBoardRecView))
            .perform(
               scrollTo(
                  hasDescendant(withText(lastMessage.getContentEncrypted()))
               )
            );

         onView(withText(lastMessage.getContentEncrypted()))
            .check(matches(isDisplayed()));
         onView(withId(R.id.main_item_not_read))
            .check(matches(withText(equalTo("1"))));
         onView(withId(R.id.main_item_last_msg_time))
            .check(
               matches(
                  withText(
                     equalTo(
                        DateTimeUtil.getMessageTime(lastMessage.getTimestamp())
                     )
                  )
               )
            );
         onView(withId(R.id.main_item_disp_name))
            .check(matches(withText(equalTo(testUser2.getDisplayName()))));
      } finally {
         ApiHelper.deleteMessage(lastMessage, context, testUser);
         ApiHelper.deleteConversation(
            conversationDTO.getConversationId(),
            testUser,
            context
         );
      }
   }

   @Test
   public void testConversationWithThreeParticipant() throws IOException {
      List<Long> userIds = Arrays.asList(
         testUser.getUserId(),
         testUser2.getUserId(),
         testUser3.getUserId()
      );
      ConversationDTO conversationDTO = ApiHelper.addConversationByUserIds(
         userIds,
         testUser.getToken()
      );

      MessageEntry secondMessage = ApiHelper.addMessage(
         TestUtil.getRandomMessage(
            conversationDTO.getConversationId(),
            testUser3.getUserId()
         ),
         testUser.getToken()
      );

      MessageEntry lastMessage = ApiHelper.addMessage(
         TestUtil.getRandomMessage(
            conversationDTO.getConversationId(),
            testUser2.getUserId()
         ),
         testUser.getToken()
      );
      try {
         ActivityScenario.launch(MainActivity.class);

         onView(withId(R.id.messageBoardRecView))
            .perform(
               scrollTo(
                  hasDescendant(withText(lastMessage.getContentEncrypted()))
               )
            );

         onView(withText(lastMessage.getContentEncrypted()))
            .check(matches(isDisplayed()));

         onView(withId(R.id.main_item_not_read))
            .check(matches(withText(equalTo("2"))));
         onView(withId(R.id.main_item_last_msg_time))
            .check(
               matches(
                  withText(
                     equalTo(
                        DateTimeUtil.getMessageTime(lastMessage.getTimestamp())
                     )
                  )
               )
            );
      } finally {

         ApiHelper.deleteMessage(secondMessage, context, testUser);
         ApiHelper.deleteMessage(lastMessage, context, testUser);
         ApiHelper.deleteConversation(
            conversationDTO.getConversationId(),
            testUser,
            context
         );
      }
   }

   @Test
   public void testConversationWithImageReceived() throws IOException {
      List<Long> userIds = Arrays.asList(
         testUser.getUserId(),
         testUser2.getUserId()
      );
      ConversationDTO conversationDTO = ApiHelper.addConversationByUserIds(
         userIds,
         testUser.getToken()
      );

      MessageEntry lastMessage = ApiHelper.addMessage(
         TestUtil.getRandomMessage(
            conversationDTO.getConversationId(),
            testUser2.getUserId(),
            MessageTypeConstants.IMAGE
         ),
         testUser.getToken()
      );
      try {
         ActivityScenario.launch(MainActivity.class);
         onView(withText("Received image from: " + testUser2.getDisplayName()))
            .check(matches(isDisplayed()));
      } finally {
         ApiHelper.deleteMessage(lastMessage, context, testUser);
         ApiHelper.deleteConversation(
            conversationDTO.getConversationId(),
            testUser,
            context
         );
      }
   }

   @Test
   public void testConversationArrivingWithBroadcastReceiver()
      throws IOException, InterruptedException {
      ActivityScenario.launch(MainActivity.class);

      assertTrue(
         "WebSocketService should be running",
         WebSocketService.isServiceRunning()
      );

      Thread.sleep(1500);

      List<Long> userIds = Arrays.asList(
         testUser.getUserId(),
         testUser2.getUserId()
      );
      ConversationDTO conversationDTO = ApiHelper.addConversationByUserIds(
         userIds,
         testUser.getToken()
      );

      MessageEntry lastMessage = ApiHelper.addMessage(
         TestUtil.getRandomMessage(
            conversationDTO.getConversationId(),
            testUser2.getUserId()
         ),
         testUser.getToken()
      );
      try {
         WebSocketService wsService = WebSocketService.getInstance();
         wsService.sendMessageBroadcast(lastMessage);

         Thread.sleep(1500);
         onView(withText(lastMessage.getContentEncrypted()))
            .check(matches(isDisplayed()));
      } finally {
         ApiHelper.deleteMessage(lastMessage, context, testUser);
         ApiHelper.deleteConversation(
            conversationDTO.getConversationId(),
            testUser,
            context
         );
      }
   }

   @Test
   public void testConversationArrivingWithBroadcastReceiverExistingConversation()
      throws IOException, InterruptedException {
      ActivityScenario.launch(MainActivity.class);

      assertTrue(
         "WebSocketService should be running",
         WebSocketService.isServiceRunning()
      );

      Thread.sleep(1500);

      List<Long> userIds = Arrays.asList(
         testUser.getUserId(),
         testUser2.getUserId()
      );
      ConversationDTO conversationDTO = ApiHelper.addConversationByUserIds(
         userIds,
         testUser.getToken()
      );

      ApiHelper.addMessage(
         TestUtil.getRandomMessage(
            conversationDTO.getConversationId(),
            testUser2.getUserId()
         ),
         testUser.getToken()
      );

      MessageEntry lastMessage = ApiHelper.addMessage(
         TestUtil.getRandomMessage(
            conversationDTO.getConversationId(),
            testUser2.getUserId()
         ),
         testUser.getToken()
      );
      try {
         ActivityScenario.launch(MainActivity.class);

         WebSocketService wsService = WebSocketService.getInstance();
         wsService.sendMessageBroadcast(lastMessage);

         Thread.sleep(1500);
         onView(withText(lastMessage.getContentEncrypted()))
            .check(matches(isDisplayed()));
      } finally {
         ApiHelper.deleteMessage(lastMessage, context, testUser);
         ApiHelper.deleteConversation(
            conversationDTO.getConversationId(),
            testUser,
            context
         );
      }
   }
}
