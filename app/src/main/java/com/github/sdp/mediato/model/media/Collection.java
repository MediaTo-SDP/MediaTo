package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.model.Review;

import java.util.HashMap;
import java.util.Map;

public class Collection {
    private CollectionType collectionType;
    private String collectionName;
    private Map<String, Review> reviews = new HashMap<>();

    private Collection() {
    }

    //Constructor for Custom Collections
    public Collection(String collectionName, Map<String, Review> reviews) {
        this.collectionType = CollectionType.CUSTOM;
        this.collectionName = collectionName;
        this.reviews = reviews;
    }

    //Constructor for default Collections defined in CollectionType
    public Collection(CollectionType collectionType, Map<String, Review> reviews) {
        if (collectionType == CollectionType.CUSTOM) {
            throw new IllegalArgumentException("Use constructor for Custom collections");
        }
        this.collectionType = collectionType;
        this.collectionName = collectionType.toString();
        this.reviews = reviews;

    }

    public CollectionType getCollectionType() {
        return collectionType;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Map<String, Review> getReviews() {
        return reviews;
    }

    public void setReviews() {
        this.reviews = reviews;
    }

    public void addReview(Review review) {
        this.reviews.put(review.getMedia().getTitle(), review);
    }
}
