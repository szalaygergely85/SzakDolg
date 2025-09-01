package com.zen_vy.chat.websocket;

import java.util.HashMap;
import java.util.Map;

public class BaseMessage {
    private int type;
    private String uuid;
    private long userId;

    public BaseMessage(int type, String uuid, long userId) {
        this.type = type;
        this.uuid = uuid;
        this.userId = userId;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("uuid", uuid);
        map.put("userId", userId);
        return map;
    }

}
