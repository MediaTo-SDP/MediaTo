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

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRefUsername() {
        return refUsername;
    }

    public void setRefUsername(String refUsername) {
        this.refUsername = refUsername;
    }
}
