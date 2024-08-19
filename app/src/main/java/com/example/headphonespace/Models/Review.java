package com.example.headphonespace.Models;

import com.google.firebase.auth.FirebaseUser;

public class Review {
    public static final int MAX_LINES_COLLAPSED = 3;
    public static final int MIN_LINES_COLLAPSED = 1;

    FirebaseUser user = null;
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

    public Review(FirebaseUser User, String contents, String title,String headphoneName, float rating)
    {
        this.user = user;
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

    public FirebaseUser getUser() {
        return user;
    }

    public Review setUser(FirebaseUser user) {
        this.user = user;
        return this;
    }
}
