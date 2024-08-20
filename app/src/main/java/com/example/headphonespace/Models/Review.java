package com.example.headphonespace.Models;

public class Review {
    public static final int MAX_LINES_COLLAPSED = 3;
    public static final int MIN_LINES_COLLAPSED = 1;

    String userUri = null;
    String userName = null;
    String contents;
    String title;
    String headphoneName;
    private boolean collapsed = false;
    private float rating;

    public Review() {
    }

    public String getHeadphoneName() {
        return headphoneName;
    }

    public Review setHeadphoneName(String headphoneName) {
        this.headphoneName = headphoneName;
        return this;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public Review setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Review setTitle(String title) {
        this.title = title;
        return this;
    }

    public float getRating() {
        return rating;
    }

    public Review setRating(float rating) {
        this.rating = rating;
        return this;
    }

    public Review(String userUri, String userName, String contents, String title,String headphoneName, float rating)
    {
        this.userUri = userUri;
        this.userName = userName;
        this.contents = contents;
        this.title = title;
        this.headphoneName = headphoneName;
        this.rating = rating;
    }

    public String getContents() {
        return contents;
    }

    public Review setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public String getUserUri() {
        return userUri;
    }

    public Review setUserUri(String userUri) {
        this.userUri = userUri;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public Review setUserName(String userName) {
        this.userName = userName;
        return this;
    }
}
