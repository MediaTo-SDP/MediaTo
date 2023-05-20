package com.github.sdp.mediato.model.post;

import static com.github.sdp.mediato.model.post.PostType.REVIEW;

import androidx.annotation.Nullable;

import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;

import java.text.AttributedCharacterIterator;

/**
 * Class representing a review post
 */
public class ReviewPost extends Post{
    private Review review;
    private Collection collection;

    public ReviewPost(String username, Review review, Collection collection) {
        super(REVIEW, username);
        this.review = review;
        this.collection = collection;
    }

    public String getTitle() {
        return this.review.getMedia().getTitle();
    }
    public int getGrade() {
        return this.review.getGrade();
    }
    public String getComment() {
        return this.review.getComment();
    }
    public int getId() {return this.review.hashCode();}
    public String getMediaIconUrl() {return this.review.getMedia().getIconUrl();}
    public String getCollectionName() {return this.collection.getCollectionName();}
    public int getLikeCount() {return this.review.getLikeCount();}
    public int getDislikeCount() {return this.review.getDislikeCount();}
    public void unLike(String username) {this.review.unLike(username);}
    public void unDislike(String username) {this.review.unDislike(username);}
    public void like(String username) {this.review.like(username);}
    public void dislike(String username) {this.review.dislike(username);}
    public void addComment(String username, String comment) {this.review.addComment(username, comment);}

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ReviewPost)) return false;
        ReviewPost that = (ReviewPost) obj;
        return (this.getId() == that.getId()) && (this.getComment().equals(that.getComment()))
                && (this.getGrade() == that.getGrade()) && (this.getTitle().equals(that.getTitle()));
    }

}
