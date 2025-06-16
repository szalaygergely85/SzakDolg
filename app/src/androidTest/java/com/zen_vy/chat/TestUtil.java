package com.zen_vy.chat;

import android.content.Context;
import com.zen_vy.chat.constans.SharedPreferencesConstants;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.models.user.service.UserService;
import com.zen_vy.chat.util.RandomUtil;
import com.zen_vy.chat.util.SharedPreferencesUtil;
import timber.log.Timber;

public class TestUtil {

   public static final String TEST_EMAIL = "test@zenvy.com";
   public static final String TEST_DISPLAY_NAME = "test";
   public static final String TEST_PASSWORD = "123456";
   public static final String TEST_UUID = "test_123456";

   public static final String TEST_TOKEN = "test_123456";

   public static void deleteUser(Long userId, Context context) {
      UserService userService = new UserService(context);
      userService.deleteUser(
         userId,
         TEST_UUID,
         new UserService.UserCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
               Timber.i("TestUser deleted");
            }

            @Override
            public void onError(Throwable t) {}
         }
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

   public static String createRandomEmail() {
      return (
         RandomUtil.getRandomString(5) +
         "@" +
         RandomUtil.getRandomString(3) +
         ".com"
      );
   }

   public static void deleteUser(String email, Context context) {
      UserService userService = new UserService(context);
      userService.deleteUser(
         email,
         TEST_UUID,
         new UserService.UserCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
               Timber.i("TestUser deleted");
            }

            @Override
            public void onError(Throwable t) {}
         }
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
}
