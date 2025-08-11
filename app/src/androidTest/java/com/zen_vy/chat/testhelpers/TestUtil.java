package com.zen_vy.chat.testhelpers;

import android.content.Context;
import com.zen_vy.chat.constans.SharedPreferencesConstants;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.models.user.service.UserService;
import com.zen_vy.chat.util.DateTimeUtil;
import com.zen_vy.chat.util.RandomUtil;
import com.zen_vy.chat.util.SharedPreferencesUtil;

import java.io.IOException;

public class TestUtil {

   public static final String TEST_EMAIL = "test@zenvy.com";
   public static final String TEST_DISPLAY_NAME = "test";
   public static final String TEST_PASSWORD = "123456";
   public static final String TEST_UUID = "test_123456";

   public static final String TEST_TOKEN = "test_123456";

   private static final Long TEST_ID = 9999L;

   public static void deleteSharedPreferences(Context context) {
      SharedPreferencesUtil.deletePreference(
         context,
         SharedPreferencesConstants.USERTOKEN
      );

      SharedPreferencesUtil.deletePreference(
         context,
         SharedPreferencesConstants.UUID
      );
   }

   public static User addUser(User user, Context context) {
      UserService userService = new UserService(context);
      userService.addUser(
         user,
         new UserService.LoginCallback<User>() {
            @Override
            public void onSuccess(User data) {}

            @Override
            public void onUserNotFound() {}

            @Override
            public void onError(Throwable t) {}
         }
      );
      return user;
   }

   public static User addUser(String email, String uuid, Context context) {
      User user = new User(
         RandomUtil.getRandomString(5),
         email,
         RandomUtil.getRandomString(6),
         RandomUtil.getRandomString(6),
         RandomUtil.getRandomString(6),
         RandomUtil.getRandomString(6),
         RandomUtil.getRandomLong(),
         uuid
      );
      user.setPublicKey(RandomUtil.getRandomString(6));
      return addUser(user, context);
   }

   public static User addUser(String email, Context context) {
      return addUser(email, RandomUtil.getRandomString(6), context);
   }

   public static User getTestUser() {
      User user = new User(
         TEST_DISPLAY_NAME,
         TEST_EMAIL,
         RandomUtil.getRandomString(6),
         RandomUtil.getRandomString(6),
         RandomUtil.getRandomString(6),
         RandomUtil.getRandomString(6),
         RandomUtil.getRandomLong(),
         TEST_UUID
      );
      user.setToken(TEST_TOKEN);
      user.setUserId(TEST_ID);
      return user;
   }

   public static String createRandomEmail() {
      return (
         RandomUtil.getRandomString(5) +
         "@" +
         RandomUtil.getRandomString(3) +
         ".com"
      );
   }

   public static MessageEntry getRandomMessage(Long conversationId, Long userId){
       return getRandomMessage(conversationId, userId, MessageTypeConstants.MESSAGE);
   }

    public static MessageEntry getRandomMessage(long conversationId, Long userId, int type) {
        return new MessageEntry(
                conversationId,
                userId,
                DateTimeUtil.now(),
                RandomUtil.getRandomString(15),
                type,
                RandomUtil.getRandomString(5)
        );
    }

   public static User getRandomUser() throws IOException {
       return
               new User(
                       RandomUtil.getRandomString(5),
                       TestUtil.createRandomEmail(),
                       RandomUtil.getRandomString(5),
                       RandomUtil.getRandomString(5),
                       null,
                       null,
                       RandomUtil.getRandomLong(),
                       RandomUtil.getRandomString(5)

       );
   }

   public static void performLogin(Context context)
      throws InterruptedException {
      UserService userService = new UserService(context);
      userService.getTokenByPasswordAndEmail(
         TEST_PASSWORD,
         TEST_EMAIL,
         new UserService.LoginCallback<User>() {
            @Override
            public void onSuccess(User data) {
               SharedPreferencesUtil.setStringPreference(
                  context,
                  SharedPreferencesConstants.USERTOKEN,
                  data.getToken()
               );

               SharedPreferencesUtil.setStringPreference(
                  context,
                  SharedPreferencesConstants.UUID,
                  data.getUuid()
               );
            }

            @Override
            public void onUserNotFound() {}

            @Override
            public void onError(Throwable t) {}
         }
      );
      Thread.sleep(1000);
   }

   public static void addConversation() {}


}
