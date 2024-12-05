package com.example.szakdolg.models.message;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import java.util.List;

public class MessageService extends BaseService {

   private MessageDatabaseUtil messageDatabaseUtil;

   public MessageService(Context context, User currentUser) {
      super(context, currentUser);
      this.messageDatabaseUtil = new MessageDatabaseUtil(context, currentUser);
   }

   public void addMessage(MessageEntry messageEntry) {
      if (messageEntry != null) {
         messageDatabaseUtil.insertMessageEntry(messageEntry);
      }
   }

   public void addMessages(List<MessageEntry> messageEntries) {
      for (MessageEntry messageEntry : messageEntries) {
         if (!_isMessageExists(messageEntry.getMessageId())) {
            addMessage(messageEntry);
         }
      }
   }

   public MessageEntry getLatestMessageEntry(Long conversationId) {
      MessageEntry messageEntry = messageDatabaseUtil.getLatestMessageEntry(
         conversationId
      );
      return messageEntry;
   }

   public List<MessageEntry> getMessagesByConversationId(Long conversationId) {
      List<MessageEntry> messageEntries =
         messageDatabaseUtil.getAllMessageEntriesByConversationId(
            conversationId
         );
      return messageEntries;
   }

   private boolean _isMessageExists(Long messageId) {
      List<Long> idList = messageDatabaseUtil.getAllMessageIds();
      return idList.contains(messageId);
   }

   public void updateMessage(MessageEntry entry) {
      MessageEntry existingEntry = messageDatabaseUtil.getMessageByUuid(
         entry.getUuId()
      );

      if (existingEntry != null) {
         MessageEntry updatedEntry = new MessageEntry();
         updatedEntry.setUuId(existingEntry.getUuId());

         if (
            entry.getMessageId() != null &&
            !entry.getMessageId().equals(existingEntry.getMessageId())
         ) {
            updatedEntry.setMessageId(entry.getMessageId());
         } else {
            updatedEntry.setMessageId(existingEntry.getMessageId());
         }

         if (
            entry.getConversationId() != null &&
            !entry.getConversationId().equals(existingEntry.getConversationId())
         ) {
            updatedEntry.setConversationId(entry.getConversationId());
         } else {
            updatedEntry.setConversationId(existingEntry.getConversationId());
         }

         if (
            entry.getSenderId() != null &&
            !entry.getSenderId().equals(existingEntry.getSenderId())
         ) {
            updatedEntry.setSenderId(entry.getSenderId());
         } else {
            updatedEntry.setSenderId(existingEntry.getSenderId());
         }

         if (
            entry.getTimestamp() != null &&
            !entry.getTimestamp().equals(existingEntry.getTimestamp())
         ) {
            updatedEntry.setTimestamp(entry.getTimestamp());
         } else {
            updatedEntry.setTimestamp(existingEntry.getTimestamp());
         }

         if (
            entry.getContentEncrypted() != null &&
            !entry
               .getContentEncrypted()
               .equals(existingEntry.getContentEncrypted())
         ) {
            updatedEntry.setContentEncrypted(entry.getContentEncrypted());
         } else {
            updatedEntry.setContentEncrypted(
               existingEntry.getContentEncrypted()
            );
         }

         if (entry.isRead() != existingEntry.isRead()) {
            updatedEntry.setRead(entry.isRead());
         } else {
            updatedEntry.setRead(existingEntry.isRead());
         }

         if (entry.getType() != existingEntry.getType()) {
            updatedEntry.setType(entry.getType());
         } else {
            updatedEntry.setType(existingEntry.getType());
         }

         if (
            entry.getContent() != null &&
            !entry.getContent().equals(existingEntry.getContent())
         ) {
            updatedEntry.setContent(entry.getContent());
         } else {
            updatedEntry.setContent(existingEntry.getContent());
         }

         if (entry.isUploaded() != existingEntry.isUploaded()) {
            updatedEntry.setUploaded(entry.isUploaded());
         } else {
            updatedEntry.setUploaded(existingEntry.isUploaded());
         }

         messageDatabaseUtil.updateMessageEntry(updatedEntry);
         Log.d("Service", "Message updated for uUId: " + entry.getUuId());
      } else {
         // If the message doesn't exist, add it using DatabaseUtil
         addMessage(entry);
         Log.d("Service", "New message added with uUId: " + entry.getUuId());
      }
   }

    public int getCountByNotReadMsg(Long conversationId) {
      return messageDatabaseUtil.getUnreadMessageCountByConversationId(conversationId);
    }

    public void setMessagesAsReadByConversationId(Long conversationId) {
       messageDatabaseUtil.setMessagesAsReadByConversationId(conversationId);
    }
}
