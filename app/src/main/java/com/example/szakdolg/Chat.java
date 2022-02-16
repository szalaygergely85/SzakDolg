package com.example.szakdolg;

import java.util.Date;

public class Chat {
    private String message;

    private String sender;

    public Chat(String message, String sender) {
        this.message = message;
  //
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public void setDate(Date date) {
      //  this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "message='" + message + '\'' +
        //        ", date=" + date +
                ", sender='" + sender + '\'' +
                '}';
    }
}
