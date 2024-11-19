package com.example.szakdolg.DTO;

import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import java.io.Serializable;
import java.util.List;

public class ConversationContent implements Serializable {

   private Long conversationId;
   private List<User> participants;
   private List<MessageEntry> messages;
   private String PublicKey;

   public Long getConversationId() {
      return conversationId;
   }

   public void setConversationId(Long conversationId) {
      this.conversationId = conversationId;
   }

   public List<User> getParticipants() {
      return participants;
   }

   public void setParticipants(List<User> participants) {
      this.participants = participants;
   }

   public List<MessageEntry> getMessages() {
      return messages;
   }

   public void setMessages(List<MessageEntry> messages) {
      this.messages = messages;
   }

   public String getPublicKey() {
      return PublicKey;
   }

   public void setPublicKey(String publicKey) {
      PublicKey = publicKey;
   }

   public ConversationContent(
      Long conversationId,
      List<User> participants,
      List<MessageEntry> messages,
      String publicKey
   ) {
      this.conversationId = conversationId;
      this.participants = participants;
      this.messages = messages;
      PublicKey = publicKey;
   }
}
