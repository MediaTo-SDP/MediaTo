package com.github.sdp.mediato.model;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.media.Media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Review implements Serializable {

    public static final int MAX_GRADE = 10;
    public static final int MIN_GRADE = 0;
    private String username;
    private Media media;
    private int grade;
    private String comment;
    private Map<String, Boolean> likes = new HashMap<>();
    private Map<String, Boolean> dislikes = new HashMap<>();
    private Map<String, String> comments = new HashMap<>();

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

    public Map<String, String> getComments() { return comments;}

    public void setComment(String comment) {
        this.comment = comment;
    }
    public List<String> getLikes() {
        return likes.entrySet().stream()
                .filter(Map.Entry::getValue).map(Map.Entry::getKey)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<String> getDislikes() {
        return dislikes.entrySet().stream()
                .filter(Map.Entry::getValue).map(Map.Entry::getKey)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    }
    public int getLikeCount() {
        return getLikes().size();
    }

    public int getDislikeCount() {
        return getDislikes().size();
    }
    public void unLike(String username) {
        likes.put(username, false);
    }
    public void unDislike(String username) {
        dislikes.put(username, false);
    }
    public void like(String username) {
        likes.put(username, true);
        dislikes.put(username, false);
    }
    public void dislike(String username) {
        dislikes.put(username, true);
        likes.put(username, false);
    }
    public void addComment(String username, String comment) {
        comments.put(username, comment);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Review)) {
            throw new IllegalArgumentException("Object is not an instance of Review");
        }
        Review other = (Review) obj;
        return Objects.equals(this.username, other.username) && Objects.equals(this.media, other.media) && this.grade == other.grade && Objects.equals(this.comment, other.comment);
    }

    /**
     * Formats a rating given as an integer to a rating displayed with circle symbols.
     * @param rating the rating to format
     * @return the formatted rating
     */
    public static String formatRating(int rating) {
        Preconditions.checkGrade(rating);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= Review.MAX_GRADE; i++) {
            if (i <= rating) {
                sb.append("●");
            } else {
                sb.append("○");
            }
        }
        return sb.toString();
    }

}
