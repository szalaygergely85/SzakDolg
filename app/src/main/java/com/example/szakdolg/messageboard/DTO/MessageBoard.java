package com.example.szakdolg.messageboard.DTO;

import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.entity.User;
import java.security.PublicKey;
import java.util.List;

public class MessageBoard {

   private Long conversationId;

   private MessageEntry message;

   private List<User> participants;
   private PublicKey publicKey;

   public MessageBoard(
      Long conversationId,
      MessageEntry message,
      List<User> participants
   ) {
      this.conversationId = conversationId;
      this.message = message;
      this.participants = participants;
   }

   public Long getConversationId() {
      return conversationId;
   }

   public void setConversationId(Long conversationId) {
      this.conversationId = conversationId;
   }

   public MessageEntry getMessage() {
      return message;
   }

   public void setMessage(MessageEntry message) {
      this.message = message;
   }

   public List<User> getParticipants() {
      return participants;
   }

   public void setParticipants(List<User> participants) {
      this.participants = participants;
   }

   public PublicKey getPublicKey() {
      return publicKey;
   }

   public void setPublicKey(PublicKey publicKey) {
      this.publicKey = publicKey;
   }
}
