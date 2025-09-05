package com.zen_vy.chat.websocket;

import static com.zen_vy.chat.models.message.constants.MessageTypeConstants.MESSAGE_STATUS_UPDATE;

import com.google.gson.Gson;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.message.entity.MessageStatusType;

import java.util.HashMap;
import java.util.Map;

public class MessageFactory {

   private static final Gson gson = new Gson();

   private static String build(BaseMessage base, Map<String, Object> extra) {
      Map<String, Object> map = base.toMap();
      if (extra != null) {
         map.putAll(extra);
      }
      return gson.toJson(map);
   }

   public static String messageStatusUpdate(MessageEntry messageEntry,  long userId, MessageStatusType messageStatusType) {
      BaseMessage base = new BaseMessage(
      MESSAGE_STATUS_UPDATE,
              messageEntry.getUuid(),
         userId
      );

      Map<String, Object> extra = new HashMap<>();
      extra.put("messageStatusType", messageStatusType);
      extra.put("conversationId", messageEntry.getConversationId());


      return build(base, extra);
   }

   public static String pingMessage(String uuid, long userId) {
      BaseMessage base = new BaseMessage(
         MessageTypeConstants.PING,
         uuid,
         userId
      );
      return build(base, null);
   }

   public static String textMessage(
      String uuid,
      long userId,
      long conversationId,
      long timestamp,
      String content,
      boolean encrypted
   ) {
      BaseMessage base = new BaseMessage(
         MessageTypeConstants.MESSAGE,
         uuid,
         userId
      );
      Map<String, Object> extra = new HashMap<>();
      extra.put("conversationId", conversationId);

      extra.put("timestamp", timestamp);
      extra.put("content", content);
      extra.put("encrypted", encrypted);
      return build(base, extra);
   }

   public static String MessageEntryMessage(
           MessageEntry messageEntry
   ) {
     return textMessage(
           messageEntry.getUuid(),
           messageEntry.getSenderId(),
           messageEntry.getConversationId(),
           messageEntry.getTimestamp(),
           messageEntry.getContent(),
           messageEntry.isEncrypted()
   );
   }

   public static String imageMessage(String uuid, long userId, String text) {
      BaseMessage base = new BaseMessage(
         MessageTypeConstants.MESSAGE,
         uuid,
         userId
      );
      Map<String, Object> extra = new HashMap<>();
      extra.put("text", text);
      return build(base, extra);
   }
   /*
	public static String typing(String uuid, long userId, boolean isTyping) {
		BaseMessage base = new BaseMessage(
				MessageTypeConstants.TYPING, uuid, userId
		);
		Map<String, Object> extra = new HashMap<>();
		extra.put("isTyping", isTyping);
		return build(base, extra);
	}

	 */

}
