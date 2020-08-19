package com.monguide.monguide.models;

public class Notification {
    // user and post that triggered this notification
    private String uid;
    private String qid;
    private Boolean isRead;

    public Notification() { }

    public Notification(String uid, String qid) {
        this.uid = uid;
        this.qid = qid;
        this.isRead = false;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
