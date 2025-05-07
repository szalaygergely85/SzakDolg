package com.zen_vy.chat.DTO;

import com.zen_vy.chat.models.contacts.Contact;
import com.zen_vy.chat.models.user.entity.User;

public class ContactsDTO {

   private Contact contact;
   private User user;

   public ContactsDTO() {}

   public ContactsDTO(Contact contact, User user) {
      this.contact = contact;
      this.user = user;
   }

   public Contact getContact() {
      return contact;
   }

   public void setContact(Contact contact) {
      this.contact = contact;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }
}
