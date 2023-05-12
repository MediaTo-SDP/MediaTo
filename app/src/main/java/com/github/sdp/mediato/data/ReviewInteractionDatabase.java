package com.github.sdp.mediato.data;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.post.Reaction;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewInteractionDatabase {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void likeReview(String refUsername, String tarUsername, String collectionName, String review) {
        setReactionValue(refUsername, tarUsername, collectionName, review, true, Reaction.LIKE);
    }

    public static void unLikeReview(String refUsername, String tarUsername, String collectionName, String review) {
        setReactionValue(refUsername, tarUsername, collectionName, review, false, Reaction.LIKE);
    }

    public static void dislikeReview(String refUsername, String tarUsername, String collectionName, String review) {
        setReactionValue(refUsername, tarUsername, collectionName, review, true, Reaction.DISLIKE);
    }

    public static void unDislikeReview(String refUsername, String tarUsername, String collectionName, String review) {
        setReactionValue(refUsername, tarUsername, collectionName, review, false, Reaction.DISLIKE);
    }

    private static void setReactionValue(String refUsername, String tarUsername, String collectionName, String reviewTitle, Boolean value, Reaction reaction) {
        Preconditions.checkNullOrEmptyString(refUsername, "refusername");
        Preconditions.checkNullOrEmptyString(tarUsername, "tarusername");
        Preconditions.checkNullOrEmptyString(collectionName, "collectionname");
        Preconditions.checkNullOrEmptyString(reviewTitle, "reviewtitle");
        if (reaction != Reaction.LIKE && reaction != Reaction.DISLIKE) throw new IllegalArgumentException("Reaction must be either likes or dislikes");
        DatabaseUtils.getReviewReference(tarUsername, collectionName, reviewTitle)
                .child(reaction.toString())
                .child(refUsername)
                .setValue(value)
                .addOnCompleteListener(task -> System.out.println("Edit " + reaction + " to " + reviewTitle + " from " + refUsername + " to " + tarUsername));
    }

}
