package com.monguide.monguide.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Answer {
    private String uid;
    private String timestamp;
    private String body;

    public Answer() {}

    public Answer(String uid, String body) {
        this.uid = uid;
        this.body = body;
        // for custom timestamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("h:mm a, d MMM yyyy");
        this.timestamp = simpleDateFormat.format(Calendar.getInstance().getTime());
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
