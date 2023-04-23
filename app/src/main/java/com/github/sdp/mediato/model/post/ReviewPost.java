package com.github.sdp.mediato.model.post;

import static com.github.sdp.mediato.model.post.PostType.REVIEW;

import androidx.annotation.Nullable;

import com.github.sdp.mediato.model.Review;

import java.text.AttributedCharacterIterator;

public class ReviewPost extends Post{
    private String title;
    private int grade;
    private String comment;
    private int id;

    private String mediaIconUrl;

    public ReviewPost(String username, Review review){
        super(REVIEW, username);
        if (review.getUsername() != username){
            throw new IllegalArgumentException("This review wasn't made by " + username);
        }
        this.title = review.getMedia().getTitle();
        this.grade = review.getGrade();
        this.comment = review.getComment();
        this.id = review.hashCode();
        this.mediaIconUrl = review.getMedia().getIconUrl();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getId() {return this.id;}

    public String getMediaIconUrl() {return this.mediaIconUrl;}

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ReviewPost)) return false;
        ReviewPost that = (ReviewPost) obj;
        return (this.getId() == that.getId()) && (this.getComment().equals(that.getComment()))
                && (this.getGrade() == that.getGrade()) && (this.getTitle().equals(that.getTitle()));
    }

}
