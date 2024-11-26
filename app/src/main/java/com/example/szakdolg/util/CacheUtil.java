package com.example.szakdolg.util;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.models.user.entity.User;
import java.io.File;
import java.util.HashMap;

public class CacheUtil {

   private static final String TAG = "CacheUtil";

   public static void writePublicKeysCache(Context context, User user) {
      File cacheDir = context.getCacheDir();
      File hashMapFile = new File(cacheDir, "public_cache.dat");
      HashMap<String, String> keys = new HashMap<>();

      keys = FileUtil.readHashMapFromFile(hashMapFile);
      if (keys != null) {
         if (keys.containsKey(user.getEmail())) {
            return;
         }
      }
      keys = new HashMap<>();
      keys.put(user.getEmail(), user.getPublicKey());
      Log.e(TAG, keys.toString());
      // Write HashMap to file
      FileUtil.writeHashMapToFile(keys, hashMapFile);
   }

   public static String getPublicKeyFromCache(Context context, String email) {
      File cacheDir = context.getCacheDir();
      File hashMapFile = new File(cacheDir, "public_cache.dat");
      HashMap<String, String> keys = FileUtil.readHashMapFromFile(hashMapFile);

      return keys.get(email);
   }
   /*
public static void validateMessages(
	ArrayList<MessageEntry> messageEntries,
	Context context,
	User user
) {
	MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(
		context,
		user
	);
	List<Long> localMessageIds = messageDatabaseUtil.getAllMessageIds();

	if (messageEntries == null || messageEntries.isEmpty()) {
		throw new IllegalArgumentException(
			"messageEntries should not be null or empty"
		);
	}

	for (MessageEntry messageEntry : messageEntries) {
		if (!localMessageIds.contains(messageEntry.getMessageId())) {
			messageDatabaseUtil.insertMessageEntry(messageEntry);
		}
	}
}

public static void validateContacts(
	ArrayList<User> userEntries,
	Context context,
	User currentUser
) {
	UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
		context,
		currentUser
	);

	if (userEntries == null || userEntries.isEmpty()) {
		throw new IllegalArgumentException(
			"messageEntries should not be null or empty"
		);
	}
	for (User user : userEntries) {
		if (userDatabaseUtil.getUserById(user.getUserId()) == null) {
			userDatabaseUtil.insertUser(user);
		}
	}
}

public static void validateConversation(
	List<Conversation> conversations,
	Context context,
	User user
) {
	ConversationDatabaseUtil conversationDatabaseUtil =
		new ConversationDatabaseUtil(context, user);
	List<Conversation> localConversations =
		conversationDatabaseUtil.getAllConversations();
	if (conversations == null || conversations.isEmpty()) {
		throw new IllegalArgumentException(
			"conversations should not be null or empty"
		);
	}
	for (Conversation conversation : conversations) {
		if (!localConversations.contains(conversation)) {
			conversationDatabaseUtil.insertConversation(conversation);
		}
	}
}

public static void validateConversationParticipant(
	List<ConversationParticipant> participants,
	Context context,
	User user
) {
	ConversationDatabaseUtil conversationDatabaseUtil =
		new ConversationDatabaseUtil(context, user);
	List<ConversationParticipant> localParticipants =
		conversationDatabaseUtil.getAllConversationParticipant();
	if (participants == null || participants.isEmpty()) {
		throw new IllegalArgumentException(
			"conversations should not be null or empty"
		);
	}
	for (ConversationParticipant conversationParticipant : participants) {
		if (!localParticipants.contains(conversationParticipant)) {
			conversationDatabaseUtil.insertConversationParticipant(
			conversationParticipant
			);
		}
	}
}*/
}
