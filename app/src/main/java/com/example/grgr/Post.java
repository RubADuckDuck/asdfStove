package com.example.grgr;

import java.util.List;

public class Post {
    private String username;
    private String uid;
    private String content;
    private List<String> comments;
    private List<AppUsageData> appUsageDataList;

    public Post() {
        // Default constructor required for Firebase database operations
    }

    public Post(String username, String uid, String content, List<String> comments, List<AppUsageData> appUsageDataList) {
        this.username = username;
        this.uid = uid;
        this.content = content;
        this.comments = comments;
        this.appUsageDataList = appUsageDataList;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }

    public String getContent() {
        return content;
    }

    public List<String> getComments() {
        return comments;
    }

    public List<AppUsageData> getAppUsageDataList() {
        return appUsageDataList;
    }

    // Setters can be added if you need to update the properties of a post
}
