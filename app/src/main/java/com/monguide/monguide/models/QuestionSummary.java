package com.monguide.monguide.models;

import com.google.firebase.Timestamp;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class QuestionSummary {
    private String uid;
    private String timestamp;
    private String title;
    private String body;
    private int upvoteCount;
    private int downvoteCount;
    private int answerCount;
    private HashMap<String, Boolean> upvoters;
    private HashMap<String, Boolean> downvoters;

    public QuestionSummary() {}

    public QuestionSummary(String uid, String title, String body) {
        this.uid = uid;
        this.title = title;
        this.body = body;
        this.upvoteCount = 0;
        this.downvoteCount = 0;
        this.answerCount = 0;
        // for custom timestamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("h:mm a, d MMM yyyy");
        this.timestamp = simpleDateFormat.format(Calendar.getInstance().getTime());
        upvoters = new HashMap<>();
        downvoters = new HashMap<>();
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

    public int getUpvoteCount() {
        return upvoteCount;
    }

    public void setUpvoteCount(int upvoteCount) {
        this.upvoteCount = upvoteCount;
    }

    public int getDownvoteCount() {
        return downvoteCount;
    }

    public void setDownvoteCount(int downvoteCount) {
        this.downvoteCount = downvoteCount;
    }

    public String getTitle() {
        return title;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HashMap<String, Boolean> getUpvoters() {
        return upvoters;
    }

    public void setUpvoters(HashMap<String, Boolean> upvoters) {
        this.upvoters = upvoters;
    }

    public HashMap<String, Boolean> getDownvoters() {
        return downvoters;
    }

    public void setDownvoters(HashMap<String, Boolean> downvoters) {
        this.downvoters = downvoters;
    }

}
