package com.example.szakdolg;

public class MessageB {
    private String from;
    private String to;
    private String messageID;
    private String message;
    private String imageUrl;

    public MessageB(String messageID, String from, String to, String message, String imageUrl) {
        this.from = from;
        this.to = to;
        this.messageID = messageID;
        this.message = message;
        this.imageUrl = imageUrl;

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
