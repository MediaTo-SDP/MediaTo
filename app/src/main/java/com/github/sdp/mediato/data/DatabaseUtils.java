package com.github.sdp.mediato.data;

import static com.github.sdp.mediato.data.UserDatabase.database;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.post.Reaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This is a DatabaseUtils class for the database classes
 */
public class DatabaseUtils {
    private static boolean persistenceIsActive = false;

    //-----------Constant definitions-------------

    static final String USERS_PATH = "Users/";
    static final String REVIEWS_PATH = "reviews/";
    static final String COMMENTS_PATH = "comments/";
    static final String USER_COLLECTIONS_PATH = "/collections/";
    static final String LOCATION_PATH = "/location/";
    static final String FOLLOWING_PATH = "/following/";
    static final String FOLLOWERS_PATH = "/followers/";

    static final String USER_PROFILE_PICS_PATH = "ProfilePics/";

    static final int PROFILE_PIC_MAX_SIZE = 1024 * 1024; //1 Megabyte

    //The default radius for the nearby users in Kilometers
    //It is now used for all the nearby users queries
    //But a future update may allow the user to choose the radius
    public static final int DEFAULT_RADIUS = 250;

    //---------------------Util methods-------------------------------------

    /**
     * Helper method that returns the database reference for a reaction path
     * @param tarUsername the username of the user who is being reacted to
     * @param collectionName the name of the collection the review is in
     * @param mediaTitle the title of the media reviewed
     * @param reaction the reaction to the review
     * @return the database reference for the reaction
     */
    static DatabaseReference getReactionReference(String tarUsername, String collectionName, String mediaTitle, Reaction reaction) {
        if (reaction != Reaction.LIKE && reaction != Reaction.DISLIKE) throw new IllegalArgumentException("Reaction must be either likes or dislikes");
        Preconditions.checkUsername(tarUsername);
        Preconditions.checkNullOrEmptyString(collectionName, "Collection name");
        Preconditions.checkNullOrEmptyString(mediaTitle, "Media title");
        return DatabaseUtils.getReviewReference(tarUsername, collectionName, mediaTitle)
                .child(reaction.toString());
    }

    /**
     * Helper method that returns the database reference for a review given its user and the collection it is in
     *
     * @param username the username of the user concerned
     * @param collectionName the name of the collection needed
     * @param mediaTitle the title of the media reviewed
     * @return the database reference for the user
     */
    static DatabaseReference getReviewReference(String username, String collectionName, String mediaTitle) {
        return getCollectionReference(username, collectionName).child(REVIEWS_PATH + mediaTitle);
    }

    /**
     * Helper method that returns the database reference for a collection
     *
     * @param username       the username of the user concerned
     * @param collectionName the name of the collection needed
     * @return the database reference for the collection
     */
    static DatabaseReference getCollectionReference(String username, String collectionName) {
        return CollectionsDatabase.database.getReference().child(USERS_PATH + username + USER_COLLECTIONS_PATH + collectionName);
    }

    /**
     * Gets all the usernames in the database
     * @param username of the reference user
     * @return a completable future with a list of usernames containing all the usernames other than the
     *          user's itself.
     */
    public static void getAllUsernames(CompletableFuture<List<String>> future, String username) {
        UserDatabase.database.getReference(DatabaseUtils.USERS_PATH).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                List<String> allUsernames = new ArrayList<>();
                dataSnapshot.getChildren().forEach(
                        userSnapshot -> {
                            String userKey = userSnapshot.getKey();
                            if (!userKey.equals(username)) {
                                allUsernames.add(userKey);
                            }
                        }
                );
                future.complete(allUsernames);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
    }

    /**
     * Helper method for UserDatabase that returns the nearby users and completes the future
     *
     * @param future to track the state
     * @param location center location of the current user
     * @param username username of the current user
     * @return a list of strings with the users' usernames
     */
    static List<String> findNearbyUsers(CompletableFuture<List<String>> future, Location location, String username, double radius) {
        List<String> nearbyUsers = new ArrayList<>();
        database.getReference(USERS_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren().forEach(
                        userSnapshot -> {
                            User user = userSnapshot.getValue(User.class);
                            if (!userSnapshot.getKey().equals(username) && user.getLocation().isValid() && user.getLocation().isInRadius(location, radius)) {
                                nearbyUsers.add(userSnapshot.getKey());
                            }
                        }
                );
                future.complete(nearbyUsers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return nearbyUsers;
    }
    public static FirebaseDatabase getFirebaseInstance(){
        if (!persistenceIsActive) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            persistenceIsActive = !persistenceIsActive;
        }
        return  FirebaseDatabase.getInstance();
    }
}
