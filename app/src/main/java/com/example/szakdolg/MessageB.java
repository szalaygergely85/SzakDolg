package com.example.szakdolg;

public class MessageB {

    private String messageId;
    private String contactId;
    private String contactUserName;
    private String text;
    private int read;
    private String imageUrl;

    public MessageB(String messageId, String contactId, String contactUserName, String text, int read, String imageUrl) {
        this.messageId = messageId;
        this.contactId = contactId;
        this.contactUserName = contactUserName;
        this.text = text;
        this.read = read;
        this.imageUrl = imageUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactUserName() {
        return contactUserName;
    }

    public void setContactUserName(String contactUserName) {
        this.contactUserName = contactUserName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int isRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "MessageB{" +
                "messageId='" + messageId + '\'' +
                ", contactId='" + contactId + '\'' +
                ", contactUserName='" + contactUserName + '\'' +
                ", text='" + text + '\'' +
                ", read=" + read +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
