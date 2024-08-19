package com.example.headphonespace.Models;

import java.io.Serializable;
import java.util.HashMap;

public class Headphone implements Serializable {
    private String name = "";
    private String attributes = "";
    private int headphone_IMG = 0;
    private int headphone_IMG_graph = 0;
    private String headphone_info = "";
    private float rating = 0.0f;
    private boolean favorite = false;
    private HashMap<String, Review> reviews = new HashMap<String, Review>();

    public float getRating() {
        return rating;
    }

    public Headphone() {
    }

    public HashMap<String, Review> getReviewList() {
        return reviews;
    }

    public Headphone setReviewList(HashMap<String, Review> reviews) {
        this.reviews = reviews;
        return this;
    }

    public Headphone(String name, String attributes, int headphone_IMG, int headphone_IMG_graph, String headphone_info, float rating, HashMap<String, Review> reviewList) {
        this.name = name;
        this.attributes = attributes;
        this.headphone_IMG = headphone_IMG;
        this.headphone_IMG_graph = headphone_IMG_graph;
        this.headphone_info = headphone_info;
        this.rating = rating;
        this.reviews = reviewList;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public Headphone setFavorite(boolean favorite) {
        this.favorite = favorite;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getAttributes() {
        return attributes;
    }

    public int getHeadphone_IMG() {
        return headphone_IMG;
    }

    public int getHeadphone_IMG_graph() {
        return headphone_IMG_graph;
    }

    public String getHeadphone_info() {
        return headphone_info;
    }

    public Headphone addReview(String position, Review review)
    {
        if (rating != 0.0f)
            rating = ((rating * reviews.size()) + review.getRating()) / (reviews.size() + 1);
        else
            rating = review.getRating();
        reviews.put(position, review);
        return this;
    }

    @Override
    public String toString() {
        return "Headphone{" +
                "name='" + name + '\'' +
                ", attributes='" + attributes + '\'' +
                ", headphone_IMG=" + headphone_IMG +
                ", headphone_IMG_graph=" + headphone_IMG_graph +
                ", headphone_info='" + headphone_info + '\'' +
                ", rating=" + rating +
                ", favorite=" + favorite +
                ", reviews=" + reviews +
                '}';
    }
}
