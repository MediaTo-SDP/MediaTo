package com.github.sdp.mediato.cache.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.github.sdp.mediato.model.media.MediaType;

@Entity(tableName = "reviews")
public class CachedReviewPost {
    @PrimaryKey
    private final int id;
    private final boolean myFeed;
    private final String username;
    private final int grade;
    private final String comment;
    private final String collectionName;
    private MediaType mediaType;
    private String mediaId;

    public CachedReviewPost(int id, boolean myFeed, String username, int grade, String comment, String collectionName, MediaType mediaType, String mediaId) {
        this.id = id;
        this.myFeed = myFeed;
        this.username = username;
        this.grade = grade;
        this.comment = comment;
        this.collectionName = collectionName;
        this.mediaType = mediaType;
        this.mediaId = mediaId;
    }

    public int getId() {
        return id;
    }

    public boolean isMyFeed() {
        return myFeed;
    }

    public String getUsername() {
        return username;
    }

    public int getGrade() {
        return grade;
    }

    public String getComment() {
        return comment;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getMediaId() {
        return mediaId;
    }
}
