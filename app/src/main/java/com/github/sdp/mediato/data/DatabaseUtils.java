package com.github.sdp.mediato.data;

import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This is a DatabaseUtils class for the database classes
 */
public class DatabaseUtils {

    //-----------Constant definitions-------------

    static final String USERS_PATH = "Users/";
    static final String REVIEWS_PATH = "reviews/";
    static final String USER_COLLECTIONS_PATH = "/collections/";
    static final String LOCATION_PATH = "/location/";
    static final String FOLLOWING_PATH = "/following/";
    static final String FOLLOWERS_PATH = "/followers/";

    static final String USER_PROFILE_PICS_PATH = "ProfilePics/";

    static final int PROFILE_PIC_MAX_SIZE = 1024 * 1024; //1 Megabyte

    public static final int DEFAULT_RADIUS = 100;

    //---------------------Util methods-------------------------------------

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
     * Helper method for UserDatabase that returns the nearby users and completes the future
     *
     * @param future to track the state
     * @param location center location of the current user
     * @param username username of the current user
     * @return a list of strings with the users' usernames
     */
    static List<String> findNearbyUsers(CompletableFuture<List<String>> future, Location location, String username, double radius) {
        List<String> nearbyUsers = new ArrayList<>();
        UserDatabase.database.getReference(USERS_PATH).addValueEventListener(new ValueEventListener() {
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
}
