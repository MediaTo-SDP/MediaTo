package com.github.sdp.mediato.model;

/**
 * A class that represents a comment on a review
 */
public class Comment {
    private String collectionName;
    private String review;
    private String text;
    private String refUsername;

    public Comment(String collectionName, String review, String text, String refUsername) {
        this.collectionName = collectionName;
        this.review = review;
        this.text = text;
        this.refUsername = refUsername;
    }

    public Comment(String text, String refUsername) {
        this.text = text;
        this.refUsername = refUsername;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getReview() {
        return review;
    }

    public String getText() {
        return text;
    }

    public String getRefUsername() {
        return refUsername;
    }
}
