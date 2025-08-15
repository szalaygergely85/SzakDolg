package com.zen_vy.chat.models.contacts.dto;

import com.zen_vy.chat.models.conversation.entity.Conversation;
import com.zen_vy.chat.models.conversation.entity.ConversationParticipant;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConversationDTO implements Serializable {

   private Conversation conversation;
   private List<ConversationParticipant> participants;
   private List<User> users;

   private MessageEntry messageEntry;

   public ConversationDTO(
      Conversation conversation,
      List<ConversationParticipant> conversationParticipants,
      List<User> users,
      MessageEntry messageEntry
   ) {
      this.conversation = conversation;
      this.users = users;
      this.messageEntry = messageEntry;

      this.participants = conversationParticipants;
   }

   public ConversationDTO(
      Conversation conversation,
      List<User> users,
      MessageEntry messageEntry
   ) {
      new ConversationDTO(
         conversation,
         getParticipantList(),
         users,
         messageEntry
      );
   }

   private List<ConversationParticipant> getParticipantList() {
      List<ConversationParticipant> conversationParticipantList =
         new ArrayList<>();
      for (User user : users) {
         conversationParticipantList.add(
            new ConversationParticipant(getConversationId(), user.getUserId())
         );
      }
      return conversationParticipantList;
   }

   public ConversationDTO() {}

   public long getConversationId() {
      return conversation.getConversationId();
   }

   public Conversation getConversation() {
      return conversation;
   }

   public void setConversation(Conversation conversation) {
      this.conversation = conversation;
   }

   public List<ConversationParticipant> getParticipants() {
      return participants;
   }

   public void setParticipants(List<ConversationParticipant> participants) {
      this.participants = participants;
   }

   public List<User> getUsers() {
      return users;
   }

   public void setUsers(List<User> users) {
      this.users = users;
   }

   public MessageEntry getMessageEntry() {
      return messageEntry;
   }

   public void setMessageEntry(MessageEntry messageEntry) {
      this.messageEntry = messageEntry;
   }
}
