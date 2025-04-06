package com.example.szakdolg.DTO;

import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.conversation.entity.ConversationParticipant;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import java.io.Serializable;
import java.util.List;

public class ConversationDTO implements Serializable {

   private Conversation conversation;
   private List<ConversationParticipant> participants;
   private List<User> users;

   private MessageEntry messageEntry;


   public ConversationDTO(Conversation conversation, List<ConversationParticipant> participants, List<User> users, MessageEntry messageEntry) {
      this.conversation = conversation;
      this.participants = participants;
      this.users = users;
      this.messageEntry = messageEntry;
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
