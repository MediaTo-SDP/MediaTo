package com.github.sdp.mediato.data;

import android.util.Log;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocationDatabase {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Updates the user's location in the database
     *
     * @param username
     * @param latitude
     * @param longitude
     */
    public static void updateLocation(String username, double latitude, double longitude) {
        Location location = new Location(latitude, longitude);
        Preconditions.checkLocation(location);
        database.getReference().child(DatabaseUtils.USERS_PATH + username + DatabaseUtils.LOCATION_PATH)
                .setValue(location);
    }

    /**
     * Retrieves the user's saved location from the database
     *
     * @param username
     * @return a completable future with the location in it
     * @see Location class to check for validity
     */
    public static CompletableFuture<Location> getSavedLocation(String username) {
        CompletableFuture<Location> future = new CompletableFuture<>();
        database.getReference().child(DatabaseUtils.USERS_PATH + username + DatabaseUtils.LOCATION_PATH).get().addOnSuccessListener(
                dataSnapshot -> {
                    if (dataSnapshot.getValue() == null) {
                        future.completeExceptionally(new NoSuchFieldException());
                    } else {
                        future.complete(dataSnapshot.getValue(Location.class));
                    }
                }).addOnFailureListener(future::completeExceptionally);

        return future;
    }

    /**
     * Gets all the nearby users' usernames
     * @param username of the reference user
     * @param radius in which we want to look for users
     * @return a completable future with a list of strings containing the usernames
     * @Note For now, DEFAULT_RADIUS is used instead of the radius parameter because the settings aren't implemented yet
     */
    public static CompletableFuture<List<String>> getNearbyUsernames(String username, double radius) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        List<String> nearbyUsers = new ArrayList<>();
        getSavedLocation(username).thenAccept(
                location -> {
                    if (!location.isValid()) {
                        Log.d("DatabaseUtils", "No location, fetching all usernames");
                        DatabaseUtils.getAllUsernames(future, username);
                    }
                    else {
                        Log.d("DatabaseUtils", "Location found, fetching nearby usernames");
                        nearbyUsers.addAll(DatabaseUtils.findNearbyUsers(future, location, username, DatabaseUtils.DEFAULT_RADIUS));
                    }
                });
        return future;
    }

    /**
     * Gets all the nearby users
     * @param username of the reference user
     * @param radius in which we want to look for users
     * @return a completable future with a list of users
     */
    public static CompletableFuture<List<User>> getNearbyUsers(String username, double radius) {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        List<User> users = new ArrayList<>();
        getNearbyUsernames(username, radius).thenAccept(
                nearbyUsernames -> {
                    CompletableFuture.allOf(
                            nearbyUsernames.stream()
                                    .map(nearbyUsername -> UserDatabase.getUser(nearbyUsername)
                                            .thenAccept(user -> users.add(user)))
                                    .toArray(CompletableFuture[]::new)
                    ).thenRun(() -> future.complete(users));
                }
        );
        return future;
    }

}
