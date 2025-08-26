package com.zen_vy.chat.activity.chat;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.chat.activity.ChatActivity;
import com.zen_vy.chat.constans.IntentConstants;
import com.zen_vy.chat.helpers.ApiHelper;
import com.zen_vy.chat.helpers.RecycleViewTestHelper;
import com.zen_vy.chat.helpers.TestUtil;
import com.zen_vy.chat.models.contacts.dto.ConversationDTO;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.util.DateTimeUtil;
import com.zen_vy.chat.util.RandomUtil;
import com.zen_vy.chat.websocket.WebSocketService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ChatActivityTest {

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
   public void testArrivingMessages() {
      ConversationDTO conversationDTO = new ConversationDTO();

      try {
         List<Long> userIds = Arrays.asList(
            testUser.getUserId(),
            testUser2.getUserId()
         );
         conversationDTO =
         ApiHelper.addConversationByUserIds(userIds, testUser.getToken());

         Intent intent = new Intent(
            ApplicationProvider.getApplicationContext(),
            ChatActivity.class
         );
         intent.putExtra(IntentConstants.CONVERSATION_DTO, conversationDTO);

         ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(
            intent
         );

         MessageEntry firstMessage = ApiHelper.addMessage(
            TestUtil.getRandomMessage(
               conversationDTO.getConversationId(),
               testUser2.getUserId()
            ),
            testUser.getToken()
         );

         WebSocketService wsService = WebSocketService.getInstance();
         wsService.sendMessageBroadcast(firstMessage);
         Thread.sleep(2000);

         //TODO need to do encytptiooon....
         onView(withId(R.id.recViewChat))
            .check(
               matches(
                  RecycleViewTestHelper.atPosition(
                     0,
                     hasDescendant(
                        withText(
                           DateTimeUtil.toShortDateFormat(
                              firstMessage.getTimestamp()
                           )
                        )
                     )
                  )
               )
            );

         onView(withId(R.id.recViewChat))
            .check(
               matches(
                  RecycleViewTestHelper.atPosition(
                     1,
                     hasDescendant(
                        allOf(
                           withId(R.id.chatText),
                           withText(firstMessage.getContent()) // your expected message
                        )
                     )
                  )
               )
            );
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testSendMessages() {
      ConversationDTO conversationDTO = new ConversationDTO();

      try {
         List<Long> userIds = Arrays.asList(
            testUser.getUserId(),
            testUser2.getUserId()
         );
         conversationDTO =
         ApiHelper.addConversationByUserIds(userIds, testUser.getToken());

         Intent intent = new Intent(
            ApplicationProvider.getApplicationContext(),
            ChatActivity.class
         );
         intent.putExtra(IntentConstants.CONVERSATION_DTO, conversationDTO);

         ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(
            intent
         );

         String message = RandomUtil.getRandomString(5);
         _sendMessage(message);
         long timestamp = System.currentTimeMillis();

         onView(withId(R.id.recViewChat))
            .check(
               matches(
                  RecycleViewTestHelper.atPosition(
                     0,
                     hasDescendant(
                        withText(DateTimeUtil.toShortDateFormat(timestamp))
                     )
                  )
               )
            );

         onView(withId(R.id.recViewChat))
            .check(
               matches(
                  RecycleViewTestHelper.atPosition(
                     1,
                     hasDescendant(
                        allOf(
                           withId(R.id.chatTextFrMe),
                           withText(message) // your expected message
                        )
                     )
                  )
               )
            );

         onView(withId(R.id.recViewChat))
            .check(
               matches(
                  RecycleViewTestHelper.atPosition(
                     1,
                     hasDescendant(
                        allOf(
                           withId(R.id.chatTextTimeOut),
                           withEffectiveVisibility(
                              ViewMatchers.Visibility.VISIBLE
                           ),
                           withText(DateTimeUtil.getHHmm(timestamp))
                        )
                     )
                  )
               )
            );

         _sendMessage(RandomUtil.getRandomString(100000));

         onView(withId(R.id.recViewChat))
            .check(
               matches(
                  RecycleViewTestHelper.atPosition(
                     1,
                     hasDescendant(
                        allOf(
                           withId(R.id.chatTextTimeOut),
                           withEffectiveVisibility(ViewMatchers.Visibility.GONE)
                        )
                     )
                  )
               )
            );

         onView(withId(R.id.recViewChat))
            .check(
               matches(
                  RecycleViewTestHelper.atPosition(
                     2,
                     hasDescendant(
                        allOf(
                           withId(R.id.chatTextTimeOut),
                           withEffectiveVisibility(
                              ViewMatchers.Visibility.VISIBLE
                           ),
                           withText(DateTimeUtil.getHHmm(timestamp))
                        )
                     )
                  )
               )
            );

         _sendMessage("");

         onView(RecycleViewTestHelper.atPosition(3, ViewMatchers.isDisplayed()))
            .check(doesNotExist());

         for (int i = 0; i < 20; i++) {
            _sendMessage(RandomUtil.getRandomString(5));
         }

         onView(withId(R.id.recViewChat))
            .check(
               matches(RecycleViewTestHelper.atPosition(22, isDisplayed()))
            );
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         ApiHelper.deleteConversation(
            conversationDTO.getConversationId(),
            testUser,
            context
         );
      }
   }

   @Test
   public void testEmptyUiElements() throws IOException {
      ConversationDTO conversationDTO = new ConversationDTO();

      try {
         List<Long> userIds = Arrays.asList(
            testUser.getUserId(),
            testUser2.getUserId()
         );
         conversationDTO =
         ApiHelper.addConversationByUserIds(userIds, testUser.getToken());

         Intent intent = new Intent(
            ApplicationProvider.getApplicationContext(),
            ChatActivity.class
         );
         intent.putExtra(IntentConstants.CONVERSATION_DTO, conversationDTO);

         ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(
            intent
         );

         onView(withId(R.id.imgSend)).check(matches(isDisplayed()));
         onView(withId(R.id.tilChatInput)).check(matches(isDisplayed()));
         onView(withId(R.id.recViewChat)).check(matches(isDisplayed()));
         onView(withId(R.id.chatToolbar)).check(matches(isDisplayed()));
      } finally {
         ApiHelper.deleteConversation(
            conversationDTO.getConversationId(),
            testUser,
            context
         );
      }
   }

   @Test
   public void testConversationOfTwo() throws IOException {
      ConversationDTO conversationDTO = new ConversationDTO();

      List<MessageEntry> messageEntries = new ArrayList<>();

      try {
         List<Long> userIds = Arrays.asList(
            testUser.getUserId(),
            testUser2.getUserId()
         );
         conversationDTO =
         ApiHelper.addConversationByUserIds(userIds, testUser.getToken());

         _setUpMessagesForTwo(
            conversationDTO.getConversationId(),
            messageEntries
         );

         conversationDTO.setMessageEntry(messageEntries.getFirst());

         Intent intent = new Intent(
            ApplicationProvider.getApplicationContext(),
            ChatActivity.class
         );
         intent.putExtra(IntentConstants.CONVERSATION_DTO, conversationDTO);

         ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(
            intent
         );

         _assertsForTwo(messageEntries);
      } catch (Exception e) {
         throw new RuntimeException(e);
      } finally {
         // ApiHelper.deleteMessage(lastMessage, context, testUser);
         ApiHelper.deleteConversation(
            conversationDTO.getConversationId(),
            testUser,
            context
         );
      }
   }

   private void _assertsForTwo(List<MessageEntry> messageEntries) {
      onView(withId(R.id.recViewChat))
         .check(
            matches(
               RecycleViewTestHelper.atPosition(
                  0,
                  hasDescendant(
                     withText(
                        DateTimeUtil.toShortDateFormat(
                           messageEntries.getFirst().getTimestamp()
                        )
                     )
                  )
               )
            )
         );

      onView(withId(R.id.recViewChat))
         .check(
            matches(
               RecycleViewTestHelper.atPosition(
                  3,
                  hasDescendant(
                     withText(
                        DateTimeUtil.toShortDateFormat(
                           messageEntries.get(2).getTimestamp()
                        )
                     )
                  )
               )
            )
         );

      onView(withId(R.id.recViewChat))
         .check(
            matches(
               RecycleViewTestHelper.atPosition(
                  6,
                  hasDescendant(
                     withText(
                        DateTimeUtil.toShortDateFormat(
                           messageEntries.get(4).getTimestamp()
                        )
                     )
                  )
               )
            )
         );

      onView(withId(R.id.recViewChat))
         .check(
            matches(
               RecycleViewTestHelper.atPosition(
                  1,
                  hasDescendant(
                     allOf(
                        withId(R.id.chatTextFrMe),
                        withText(
                           messageEntries.getFirst().getContent()
                        ) // your expected message
                     )
                  )
               )
            )
         );

      onView(withId(R.id.recViewChat))
         .check(
            matches(
               RecycleViewTestHelper.atPosition(
                  1,
                  hasDescendant(
                     allOf(
                        withId(R.id.chatTextTimeOut),
                        withEffectiveVisibility(ViewMatchers.Visibility.GONE)
                     )
                  )
               )
            )
         );

      onView(withId(R.id.recViewChat))
         .check(
            matches(
               RecycleViewTestHelper.atPosition(
                  2,
                  hasDescendant(
                     allOf(
                        withId(R.id.chatTextTimeOut),
                        withEffectiveVisibility(
                           ViewMatchers.Visibility.VISIBLE
                        ),
                        withText(
                           DateTimeUtil.getHHmm(
                              messageEntries.get(1).getTimestamp()
                           )
                        )
                     )
                  )
               )
            )
         );
   }

   private void _setUpMessagesForTwo(
      long conversationId,
      List<MessageEntry> messageEntries
   ) throws IOException {
      messageEntries.add(
         ApiHelper.addMessage(
            TestUtil.getRandomMessage(
               conversationId,
               testUser.getUserId(),
               DateTimeUtil.toMillis(2025, 8, 12, 12, 1),
               MessageTypeConstants.MESSAGE
            ),
            testUser.getToken()
         )
      );
      messageEntries.add(
         ApiHelper.addMessage(
            TestUtil.getRandomMessage(
               conversationId,
               testUser.getUserId(),
               DateTimeUtil.toMillis(2025, 8, 12, 12, 2),
               MessageTypeConstants.MESSAGE
            ),
            testUser.getToken()
         )
      );
      messageEntries.add(
         ApiHelper.addMessage(
            TestUtil.getRandomMessage(
               conversationId,
               testUser.getUserId(),
               DateTimeUtil.toMillis(2025, 8, 13, 12, 5),
               MessageTypeConstants.MESSAGE
            ),
            testUser.getToken()
         )
      );
      messageEntries.add(
         ApiHelper.addMessage(
            TestUtil.getRandomMessage(
               conversationId,
               testUser2.getUserId(),
               DateTimeUtil.toMillis(2025, 8, 13, 12, 6),
               MessageTypeConstants.MESSAGE
            ),
            testUser.getToken()
         )
      );

      messageEntries.add(
         ApiHelper.addMessage(
            TestUtil.getRandomMessage(
               conversationId,
               testUser2.getUserId(),
               DateTimeUtil.toMillis(2025, 8, 14, 12, 6),
               MessageTypeConstants.MESSAGE
            ),
            testUser.getToken()
         )
      );
   }

   private void _sendMessage(String text) {
      onView(withId(R.id.edtChatMes))
         .perform(replaceText(text), closeSoftKeyboard());

      onView(withId(R.id.imgSend)).perform(click());
   }
}
