package com.zen_vy.chat.DTO;


import android.os.Message;

import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.message.entity.MessageStatus;

public class MessageDTO {
    private MessageEntry message;

    private MessageStatus status;

    private long timestamp;

    public MessageDTO(MessageEntry message, MessageStatus status, long timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public MessageDTO() {
    }

    public MessageEntry getMessage() {
        return message;
    }

    public void setMessage(MessageEntry message) {
        this.message = message;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }
}
