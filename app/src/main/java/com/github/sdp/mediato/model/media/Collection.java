package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.model.Review;

import java.util.List;

public class Collection {
    private final CollectionType collectionType;
    private String collectionName;
    private List<Review> reviews;

    //Constructor for Custom Collections
    public Collection(CollectionType collectionType, String collectionName, List<Review> reviews) {
        this.collectionType = collectionType;
        this.collectionName = collectionName;
        this.reviews = reviews;
    }

    //Constructor for default Collections defined in CollectionType
    public Collection(CollectionType collectionType, List<Review> reviews) {
        if (collectionType == CollectionType.CUSTOM){
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

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
