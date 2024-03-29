package com.github.sdp.mediato.data;

import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

/**
 * CollectionsDatabase class to handle the collections related database operations
 */
public class CollectionsDatabase {

    public static FirebaseDatabase database = DatabaseUtils.getFirebaseInstance();

    /**
     * Adds a collection to the user
     *
     * @param username   the concerned user
     * @param collection the collection to be added
     */
    public static void addCollection(String username, Collection collection) {
        DatabaseUtils.getCollectionReference(username, collection.getCollectionName())
                .setValue(collection)
                .addOnCompleteListener(task -> System.out.println("Added " + collection.getCollectionName() + " to " + username));
    }

    /**
     * Remove a collection from a user
     *
     * @param username
     * @param collectionName
     */
    public static void removeCollection(String username, String collectionName) {
        DatabaseUtils.getCollectionReference(username, collectionName)
                .setValue(null)
                .addOnCompleteListener(task -> System.out.println("Removed " + collectionName + " from " + username));
    }

    /**
     * Retrieves a collection
     *
     * @param username
     * @param collectionName
     * @return
     */
    public static CompletableFuture<Collection> getCollection(String username, String collectionName) {
        CompletableFuture<Collection> future = new CompletableFuture<>();
        DatabaseUtils.getCollectionReference(username, collectionName).get().addOnSuccessListener(
                dataSnapshot -> {
                    if (dataSnapshot.getValue() == null) {
                        future.completeExceptionally(new NoSuchFieldException());
                    } else {
                        future.complete(dataSnapshot.getValue(Collection.class));
                    }
                }).addOnFailureListener(future::completeExceptionally);

        return future;
    }


    /**
     * Adds a review to a collection
     *
     * @param username
     * @param collectionName
     * @param review
     */
    public static void addReviewToCollection(String username, String collectionName, Review review) {
        DatabaseUtils.getCollectionReference(username, collectionName).child(DatabaseUtils.REVIEWS_PATH + review.getMedia().getTitle()).setValue(review)
                .addOnCompleteListener(task ->
                        System.out.println("Added review of " + review.getMedia().getTitle() + " for " + username)
                );
    }

}
