package com.example.szakdolg.models.contacts;


import com.example.szakdolg.models.user.entity.User;

public class Contact {
   private Long contactId;
   private Long ownerId;
   private Long contactUserId;
   private User contactUser;

   public Contact(Long contactId, Long ownerId, Long contactUserId, User user) {
      this.contactId = contactId;
      this.ownerId = ownerId;
      this.contactUserId = contactUserId;
      this.contactUser = user;
   }

   public Contact() {}


   public Contact(Long contactId, Long ownerId, Long contactUserId) {
      this.contactId = contactId;
      this.ownerId = ownerId;
      this.contactUserId = contactUserId;
   }

   public User getContactUser() {
      return contactUser;
   }

   public void setContactUser(User contactUser) {
      this.contactUser = contactUser;
   }

   public Long getContactId() {
      return contactId;
   }

   public void setContactId(Long contactId) {
      this.contactId = contactId;
   }

   public Long getOwnerId() {
      return ownerId;
   }

   public void setOwnerId(Long ownerId) {
      this.ownerId = ownerId;
   }

   public Long getContactUserId() {
      return contactUserId;
   }

   public void setContactUserId(Long contactUserId) {
      this.contactUserId = contactUserId;
   }
}
