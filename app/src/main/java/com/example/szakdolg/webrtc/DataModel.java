package com.example.szakdolg.webrtc;

import org.json.JSONObject;

public class DataModel {
    private String target;
    private String sender;
    private String data;
    private DataModelType type;

    public DataModel(String target, String sender, String data, DataModelType type) {
        this.target = target;
        this.sender = sender;
        this.data = data;
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public DataModelType getType() {
        return type;
    }

    public void setType(DataModelType type) {
        this.type = type;
    }

    public JSONObject getJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject message = new JSONObject();
            jsonObject.put("to", target);
            jsonObject.put("from", sender);
            jsonObject.put("data", data);
            jsonObject.put("type", type.toString()); // Assuming DataModelType has a proper toString() implementation
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static DataModel fromJSON(JSONObject jsonObject) {
        try {
            String target = jsonObject.getString("to");
            String sender = jsonObject.getString("from");

                String data = jsonObject.optString("data", null);

            DataModelType type = DataModelType.valueOf(jsonObject.getString("type")); // Assuming DataModelType is an enum
            return new DataModel(target, sender, data, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle this case appropriately in production code
        }
    }
}