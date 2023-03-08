package com.github.sdp.mediato.model;

import android.provider.MediaStore;

import com.github.sdp.mediato.model.media.Media;

public class Review {

    static final int MAX_GRADE = 10;
    static final int MIN_GRADE = 0;
    private final String userName;
    private final Media media;
    private int grade;
    private String comment;

    public Review(String userName, Media media) {
        this.userName = userName;
        this.media = media;
    }

    public Review(String userName, Media media, int grade) {
        this.userName = userName;
        this.media = media;
        this.grade = grade;
    }


    public Review(String userName, Media media, int grade, String comment) {
        this.userName = userName;
        this.media = media;
        this.grade = grade;
        this.comment = comment;
    }

    public String getUsername() {return userName;}
    public Media getMedia() {return media;}
    public int getGrade() {return grade;}
    public void setGrade(int grade) {
        if ((grade > MAX_GRADE) || (grade < MIN_GRADE)) throw new IllegalArgumentException("Grade must be between " + MIN_GRADE + " and " + MAX_GRADE);
        this.grade = grade;
    }
    public String getComment() {return comment;}

    public void setComment(String comment) {
        if (comment == null || comment.isEmpty()) throw new IllegalArgumentException("Comment must not be null or empty");
        this.comment = comment;
    }

}
