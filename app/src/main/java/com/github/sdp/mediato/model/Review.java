package com.github.sdp.mediato.model;

import android.provider.MediaStore;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.media.Media;

public class Review {

    public static final int MAX_GRADE = 10;
    public static final int MIN_GRADE = 1;
    private final String username;
    private final Media media;
    private int grade;
    private String comment;

    public Review(String username, Media media) {
        Preconditions.checkUsername(username);
        Preconditions.checkMedia(media);
        this.username = username;
        this.media = media;
    }

    public Review(String username, Media media, int grade) {
        Preconditions.checkUsername(username);
        Preconditions.checkMedia(media);
        Preconditions.checkGrade(grade);
        this.username = username;
        this.media = media;
        this.grade = grade;
    }


    public Review(String username, Media media, int grade, String comment) {
        Preconditions.checkUsername(username);
        Preconditions.checkMedia(media);
        Preconditions.checkGrade(grade);
        Preconditions.checkComment(comment);
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
    public int getGrade() throws Exception {
        try {
            Preconditions.checkGrade(grade);
            return grade;
        }
        catch (Exception e){
            throw new Exception("This review does not have a grade.");
        }
    }
    public void setGrade(int grade) {
        Preconditions.checkGrade(grade);
        this.grade = grade;
    }
    public String getComment() throws Exception {
        try {
            Preconditions.checkComment(comment);
            return comment;
        }
        catch (Exception e){
            throw new Exception("This review does not have a comment.");
        }
    }

    public void setComment(String comment) {
        Preconditions.checkComment(comment);
        this.comment = comment;
    }

}
