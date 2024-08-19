package com.example.headphonespace.Models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ReviewList {
    private String name ="";
    private HashMap<String, Review> reviews = new HashMap<>();

    public ReviewList()
    {
    }

    public String getName(){return name;}

    public HashMap<String, Review> getReviews() {
        return reviews;
    }

    public ReviewList setReviews(HashMap<String, Review> reviews) {
        this.reviews = reviews;
        return this;
    }

    public ReviewList setName(String name) {
        this.name = name;
        return this;
    }

    public ReviewList addReview(String position, Review review)
    {
        reviews.put(position, review);
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "ReviewList{" +
                "name='" + name + '\'' +
                ", Reviews=" + reviews +
                '}';
    }
}
