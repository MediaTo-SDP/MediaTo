package com.github.sdp.mediato.data;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Collection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class Database {

    public static final String USERS_PATH = "Users/";

    public static final String REVIEWS_PATH = "reviews/";

    public static final String LOCATION_PATH = "/location/";
    public static final String LOCATION_VALIDITY_PATH = LOCATION_PATH + "/valid/";

    public static final String FOLLOWING_PATH = "/following/";
    public static final String FOLLOWERS_PATH = "/followers/";

    public static final String USER_COLLECTIONS_PATH = "/collections/";
    public static final String USER_PROFILE_PICS_PATH = "ProfilePics/";

    public static final int PROFILE_PIC_MAX_SIZE = 1024 * 1024; //1 Megabyte

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static StorageReference profilePics = FirebaseStorage.getInstance().getReference()
            .child(USER_PROFILE_PICS_PATH);


    /**
     * Adds a new user to the database with the profile pic
     *
     * @param user          to be added
     * @param profilePicUri the uri to the profile pic
     * @return a CompletableFuture containing the username of the user that has been added
     */
    public static CompletableFuture<String> addUser(User user, Uri profilePicUri) {
        CompletableFuture<String> future = new CompletableFuture<>();
        database.getReference().child(USERS_PATH + user.getUsername()).setValue(user,
                (error, ref) -> {
                    if (error == null) {
                        future.complete(user.getUsername());
                    } else {
                        future.completeExceptionally(error.toException());
                    }
                }
        );
        profilePics.child(user.getUsername() + ".jpg")
                .putFile(profilePicUri);
        return future;
    }

    /**
     * Adds a new user to the database without the profile pic
     *
     * @param user to be added
     * @return a CompletableFuture containing the username of the user that has been added
     */
    public static CompletableFuture<String> addUser(User user) {
        CompletableFuture<String> future = new CompletableFuture<>();
        database.getReference().child(USERS_PATH + user.getUsername()).setValue(user,
                (error, ref) -> {
                    if (error == null) {
                        future.complete(user.getUsername());
                    } else {
                        future.completeExceptionally(error.toException());
                    }
                }
        );
        return future;
    }

    /**
     * Uploads the profile pic corresponding to username
     *
     * @param username:      the user's name
     * @param profilePicUri: the profile picture uri
     */
    public static StorageTask<TaskSnapshot> setProfilePic(String username, Uri profilePicUri) {
        return profilePics.child(username + ".jpg")
                .putFile(profilePicUri).addOnSuccessListener(
                        TaskSnapshot::getBytesTransferred
                );
    }

    /**
     * Deletes user from the database
     *
     * @param username of the user to be deleted
     * @return a completable future with the username of the deleted user
     * TODO handle cases for removing user from friends lists...
     */
    public static CompletableFuture<String> deleteUser(String username) {
        CompletableFuture<String> future = new CompletableFuture<>();
        database.getReference().child(USERS_PATH + username).setValue(null,
                (error, ref) -> {
                    if (error == null) {
                        future.complete(username);
                    } else {
                        future.completeExceptionally(error.toException());
                    }
                }
        );
        return future;
    }

    /**
     * Retrieve a CompletableFuture containing the user
     *
     * @param username of the user to be retrieved
     * @return CompletableFuture<User> containing the user object
     */
    public static CompletableFuture<User> getUser(String username) {
        CompletableFuture<User> future = new CompletableFuture<>();
        database.getReference().child(USERS_PATH + username).get().addOnSuccessListener(
                dataSnapshot -> {
                    if (dataSnapshot.getValue() == null) {
                        future.completeExceptionally(new NoSuchFieldException());
                    } else {
                        future.complete(dataSnapshot.getValue(User.class));
                    }
                }).addOnFailureListener(future::completeExceptionally);

        return future;
    }

    /**
     * Retrieve a CompletableFuture containing the user
     *
     * @param email of the user to be retrieved
     * @return CompletableFuture<User> containing the user object
     */
    public static CompletableFuture<User> getUserByEmail(String email) {
        CompletableFuture<User> future = new CompletableFuture<>();
        database.getReference(USERS_PATH)
                .orderByChild("email")
                .equalTo(email)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot user : snapshot.getChildren()) {
                                future.complete(user.getValue(User.class));
                            }
                        } else {
                            future.completeExceptionally(new NoSuchFieldException());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        future.completeExceptionally(new CancellationException());
                    }
                });
        return future;
    }

    /**
     * Checks if the username is unique by looking for an already existing user that has it in the
     * database
     *
     * @param username to be checked
     * @return CompletableFuture<Boolean> containing the uniqueness value
     */
    public static CompletableFuture<Boolean> isUsernameUnique(String username) {
        CompletableFuture<Boolean> unique = new CompletableFuture<>();
        database.getReference().child(USERS_PATH + username).get().addOnSuccessListener(
                dataSnapshot -> unique.complete(!dataSnapshot.exists())
        ).addOnFailureListener(unique::completeExceptionally);
        return unique;
    }

    /**
     * Retrieve a CompletableFuture containing a byte[] of the profile pic
     *
     * @param username of the user corresponding to the profile pic
     * @return CompletableFuture<User> containing the byte[] of the pic
     */
    public static CompletableFuture<byte[]> getProfilePic(String username) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        System.out.println("Fetching image at path " + profilePics.child(username + ".jpg").getPath());
        profilePics.child(username + ".jpg").getBytes(PROFILE_PIC_MAX_SIZE)
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot == null) {
                        future.completeExceptionally(new NoSuchFieldException());
                    } else {
                        future.complete(dataSnapshot);
                    }
                })
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

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

    /**
     * Method to follow a user
     *
     * @param myUsername       the current user's username
     * @param usernameToFollow the username of the user to follow
     */
    public static void followUser(String myUsername, String usernameToFollow) {
        setValueInFollowing(myUsername, usernameToFollow, true);
        setValueInFollowers(myUsername, usernameToFollow, true);
    }

    /**
     * Method to unfollow a user
     *
     * @param myUsername         the current user's username
     * @param usernameToUnfollow the username of the user to unfollow
     */
    public static void unfollowUser(String myUsername, String usernameToUnfollow) {
        setValueInFollowing(myUsername, usernameToUnfollow, false);
        setValueInFollowers(myUsername, usernameToUnfollow, false);
    }

    /**
     * Helper method for follow and unfollow user that sets the value of a username in the followers list
     *
     * @param myUsername         the current user's username
     * @param targetUserUsername the username to follow or unfollow
     * @param value              true if following, false if unfollowing
     */
    public static void setValueInFollowers(String myUsername, String targetUserUsername, boolean value) {
        database.getReference()
                .child(USERS_PATH + myUsername + FOLLOWING_PATH + targetUserUsername).setValue(value)
                .addOnCompleteListener(task -> System.out.println(targetUserUsername + " is now set to " + value + " in " + myUsername + " following list."));
    }

    /**
     * Helper method for follow and unfollow user that sets the value of a username in the following list
     *
     * @param myUsername         the current user's username
     * @param targetUserUsername the username to follow or unfollow
     * @param value              true if following, false if unfollowing
     */
    public static void setValueInFollowing(String myUsername, String targetUserUsername, boolean value) {
        database.getReference()
                .child(USERS_PATH + targetUserUsername + FOLLOWERS_PATH + myUsername).setValue(value)
                .addOnCompleteListener(task -> System.out.println(myUsername + " is now set to " + value + " in " + targetUserUsername + " followers list."));
    }

    /**
     * Updates the user's location in the database
     * @param username
     * @param latitude
     * @param longitude
     */
    public static void updateLocation(String username, double latitude, double longitude) {
        Location location = new Location(latitude, longitude);
        Preconditions.checkLocation(location);
        database.getReference().child(USERS_PATH + username + LOCATION_PATH)
                .setValue(location);
    }

    /**
     * Retrieves the user's saved location from the database
     * @param username
     * @return a completable future with the location in it
     * @see Location class to check for validity
     */
    public static CompletableFuture<Location> getSavedLocation(String username) {
        CompletableFuture<Location> future = new CompletableFuture<>();
        database.getReference().child(USERS_PATH + username + LOCATION_PATH).get().addOnSuccessListener(
                dataSnapshot -> {
                    if (dataSnapshot.getValue() == null) {
                        future.completeExceptionally(new NoSuchFieldException());
                    } else {
                        future.complete(dataSnapshot.getValue(Location.class));
                    }
                }).addOnFailureListener(future::completeExceptionally);

        return future;
    }

}
