package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.model.Review;
import java.util.ArrayList;
import java.util.List;

public class Collection {

  private CollectionType collectionType;
  private String collectionName;
  private List<Review> reviews;

  private Collection() {
  }

  public Collection(String collectionName) {
    this.collectionType = CollectionType.CUSTOM;
    this.collectionName = collectionName;
    this.reviews = new ArrayList<>();
  }

  //Constructor for Custom Collections
  public Collection(String collectionName, List<Review> reviews) {
    this.collectionType = CollectionType.CUSTOM;
    this.collectionName = collectionName;
    this.reviews = reviews;
  }

  //Constructor for default Collections defined in CollectionType
  public Collection(CollectionType collectionType, List<Review> reviews) {
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

  public List<Review> getReviews() {
    return reviews;
  }

  public void setReviews(List<Review> reviews) {
    this.reviews = reviews;
  }

  public void addReview(Review review) {
    this.reviews.add(review);
  }
}
