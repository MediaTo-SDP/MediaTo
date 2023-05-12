package com.github.sdp.mediato.data;

import static com.github.sdp.mediato.data.DatabaseUtils.getReactionReference;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.post.Reaction;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewInteractionDatabase {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void likeReview(String refUsername, String tarUsername, String collectionName, String review) {
        DatabaseReference reactionPath = getReactionReference(tarUsername, collectionName, review, Reaction.LIKE);
        setReactionValue(reactionPath, refUsername, true);
    }

    public static void unLikeReview(String refUsername, String tarUsername, String collectionName, String review) {
        DatabaseReference reactionPath = getReactionReference(tarUsername, collectionName, review, Reaction.LIKE);
        setReactionValue(reactionPath, refUsername, false);
    }

    public static void dislikeReview(String refUsername, String tarUsername, String collectionName, String review) {
        DatabaseReference reactionPath = getReactionReference(tarUsername, collectionName, review, Reaction.DISLIKE);
        setReactionValue(reactionPath, refUsername, true);
    }

    public static void unDislikeReview(String refUsername, String tarUsername, String collectionName, String review) {
        DatabaseReference reactionPath = getReactionReference(tarUsername, collectionName, review, Reaction.DISLIKE);
        setReactionValue(reactionPath, refUsername, false);
    }

    private static void setReactionValue(DatabaseReference reactionPath, String refUsername, Boolean value) {
        reactionPath
                .child(refUsername)
                .setValue(value)
                .addOnCompleteListener(task -> System.out.println("Reaction set to " + value + " for " + refUsername + "in " + reactionPath.toString()));
    }

}
