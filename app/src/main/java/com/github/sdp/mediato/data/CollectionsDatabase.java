package com.github.sdp.mediato.data;

import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

public class CollectionsDatabase {
    private static final String USERS_PATH = "Users/";
    private static final String REVIEWS_PATH = "reviews/";
    private static final String USER_COLLECTIONS_PATH = "/collections/";

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Adds a collection to the user
     *
     * @param username   the concerned user
     * @param collection the collection to be added
     */
    public static void addCollection(String username, Collection collection) {
        getCollectionReference(username, collection.getCollectionName())
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
        getCollectionReference(username, collectionName)
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
        getCollectionReference(username, collectionName).get().addOnSuccessListener(
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
        getCollectionReference(username, collectionName).child(REVIEWS_PATH + review.getMedia().getTitle()).setValue(review)
                .addOnCompleteListener(
                        task -> {
                            System.out.println("Added review of " + review.getMedia().getTitle() + " for " + username);
                        }
                );
    }

    /**
     * Helper method that returns the database reference for a collection
     *
     * @param username       the username of the user concerned
     * @param collectionName the name of the collection needed
     * @return the database reference for the collection
     */
    public static DatabaseReference getCollectionReference(String username, String collectionName) {
        return database.getReference().child(USERS_PATH + username + USER_COLLECTIONS_PATH + collectionName);
    }
}
