package com.github.sdp.mediato.data;

import static com.github.sdp.mediato.data.DatabaseUtils.COMMENTS_PATH;
import static com.github.sdp.mediato.data.DatabaseUtils.getReactionReference;
import static com.github.sdp.mediato.data.DatabaseUtils.getReviewReference;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.Comment;
import com.github.sdp.mediato.model.post.Reaction;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

/**
 * This class is used to interact with the database for reviews.
 */
public class ReviewInteractionDatabase {

    public static FirebaseDatabase database = DatabaseUtils.getFirebaseInstance();

    /**
     * Adds a comment to a review.
     * @param tarUsername the username of the user who made the review
     * @param comment the comment
     */
    public static void commentReview(String tarUsername, Comment comment) {
        Preconditions.checkUsername(comment.getRefUsername());
        Preconditions.checkUsername(tarUsername);
        getReviewReference(tarUsername, comment.getCollectionName(), comment.getReview()).child(COMMENTS_PATH)
                .child(comment.getRefUsername() + " " + comment.hashCode())
                .setValue(comment.getText())
                .addOnCompleteListener(task -> System.out.println("Commented review"));
    }

    /**
     * Determines if the user has liked a given review.
     * @param refUsername the username of the user who is checked
     * @param tarUsername the username of the user who made the review
     * @param collectionName the name of the collection
     * @param review the review
     * @return a completable future that returns true if the user has liked the review, false otherwise
     */
    public static CompletableFuture<Boolean> likes(String refUsername, String tarUsername, String collectionName, String review) {
        Preconditions.checkUsername(refUsername);
        DatabaseReference reactionPath = getReactionReference(tarUsername, collectionName, review, Reaction.LIKE);
        return getReactionValue(reactionPath, refUsername);
    }

    /**
     * Determines if the user has disliked a given review.
     * @param refUsername the username of the user who is checked
     * @param tarUsername the username of the user who made the review
     * @param collectionName the name of the collection
     * @param review the review
     * @return a completable future that returns true if the user has disliked the review, false otherwise
     */
    public static CompletableFuture<Boolean> dislikes(String refUsername, String tarUsername, String collectionName, String review) {
        Preconditions.checkUsername(refUsername);
        DatabaseReference reactionPath = getReactionReference(tarUsername, collectionName, review, Reaction.DISLIKE);
        return getReactionValue(reactionPath, refUsername);
    }

    /**
     * Likes a review
     * @param refUsername the username of the user who is liking the review
     * @param tarUsername the username of the user who made the review
     * @param collectionName the name of the collection
     * @param review the review
     */
    public static void likeReview(String refUsername, String tarUsername, String collectionName, String review) {
        Preconditions.checkUsername(refUsername);
        DatabaseReference reactionPath = getReactionReference(tarUsername, collectionName, review, Reaction.LIKE);
        setReactionValue(reactionPath, refUsername, true);
    }

    /**
     * Unlikes a review
     * @param refUsername the username of the user who is unliking the review
     * @param tarUsername the username of the user who made the review
     * @param collectionName the name of the collection
     * @param review the review
     */
    public static void unLikeReview(String refUsername, String tarUsername, String collectionName, String review) {
        Preconditions.checkUsername(refUsername);
        DatabaseReference reactionPath = getReactionReference(tarUsername, collectionName, review, Reaction.LIKE);
        setReactionValue(reactionPath, refUsername, false);
    }

    /**
     * Dislikes a review
     * @param refUsername the username of the user who is disliking the review
     * @param tarUsername the username of the user who made the review
     * @param collectionName the name of the collection
     * @param review the review
     */
    public static void dislikeReview(String refUsername, String tarUsername, String collectionName, String review) {
        Preconditions.checkUsername(refUsername);
        DatabaseReference reactionPath = getReactionReference(tarUsername, collectionName, review, Reaction.DISLIKE);
        setReactionValue(reactionPath, refUsername, true);
    }

    /**
     * Undislikes a review
     * @param refUsername the username of the user who is undisliking the review
     * @param tarUsername the username of the user who made the review
     * @param collectionName the name of the collection
     * @param review the review
     */
    public static void unDislikeReview(String refUsername, String tarUsername, String collectionName, String review) {
        Preconditions.checkUsername(refUsername);
        DatabaseReference reactionPath = getReactionReference(tarUsername, collectionName, review, Reaction.DISLIKE);
        setReactionValue(reactionPath, refUsername, false);
    }

    /**
     * Sets the reaction value of a review
     * @param reactionPath the path to the reaction
     * @param refUsername the username of the user who is reacting
     * @param value the value of the reaction
     */
    private static void setReactionValue(DatabaseReference reactionPath, String refUsername, Boolean value) {
        reactionPath
                .child(refUsername)
                .setValue(value)
                .addOnCompleteListener(task -> System.out.println("Reaction set to " + value + " for " + refUsername + "in " + reactionPath.toString()));
    }

    /**
     * Helper function that determines the boolean value of a reaction given a path
     * @param reactionPath the path to the reaction
     * @param refUsername the username of the user who is checked
     * @return a completable future that returns true if the user has reacted, false otherwise
     */
    private static CompletableFuture<Boolean> getReactionValue(DatabaseReference reactionPath, String refUsername) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        reactionPath
                .child(refUsername)
                .get()
                .addOnSuccessListener(
                        dataSnapshot -> {
                            if ((dataSnapshot.getValue() == null) || (dataSnapshot.getValue() == Boolean.FALSE)) {
                                future.complete(Boolean.FALSE);
                            } else {
                                future.complete(Boolean.TRUE);
                            }
                        }).addOnFailureListener(future::completeExceptionally);

        return future;
    }

}
