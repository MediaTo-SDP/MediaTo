package com.github.sdp.mediato.model;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.media.Media;

public class Review {

    public static final int MAX_GRADE = 10;
    public static final int MIN_GRADE = 1;
    private String username;
    private Media media;
    private int grade;
    private String comment;

    private Review() {
    }

    public Review(String username, Media media) {
        Preconditions.checkReview(username, media);
        this.username = username;
        this.media = media;
    }

    public Review(String username, Media media, int grade) {
        Preconditions.checkReview(username, media, grade);
        this.username = username;
        this.media = media;
        this.grade = grade;
    }

    public Review(String username, Media media, int grade, String comment) {
        Preconditions.checkReview(username, media, grade, comment);
        this.username = username;
        this.media = media;
        this.grade = grade;
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public Media getMedia() {
        return media;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
