package com.github.sdp.mediato.data;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class UserDatabase {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static StorageReference profilePics = FirebaseStorage.getInstance().getReference()
            .child(DatabaseUtils.USER_PROFILE_PICS_PATH);


    /**
     * Adds a new user to the database with the profile pic
     *
     * @param user          to be added
     * @param profilePicUri the uri to the profile pic
     * @return a CompletableFuture containing the username of the user that has been added
     */
    public static CompletableFuture<String> addUser(User user, Uri profilePicUri) {
        CompletableFuture<String> future = new CompletableFuture<>();
        database.getReference().child(DatabaseUtils.USERS_PATH + user.getUsername()).setValue(user,
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
        database.getReference().child(DatabaseUtils.USERS_PATH + user.getUsername()).setValue(user,
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
        database.getReference().child(DatabaseUtils.USERS_PATH + username).setValue(null,
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
        database.getReference().child(DatabaseUtils.USERS_PATH + username).get().addOnSuccessListener(
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
        database.getReference(DatabaseUtils.USERS_PATH)
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
        database.getReference().child(DatabaseUtils.USERS_PATH + username).get().addOnSuccessListener(
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
        profilePics.child(username + ".jpg").getBytes(DatabaseUtils.PROFILE_PIC_MAX_SIZE)
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot == null) {
                        System.out.println("No profile pic found for " + username);
                        future.completeExceptionally(new NoSuchFieldException());
                    } else {
                        future.complete(dataSnapshot);
                    }
                })
                .addOnFailureListener(future::completeExceptionally);
        return future;
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

    public static CompletableFuture<Boolean> follows(String myUsername, String followedUsername) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        database.getReference()
                .child(DatabaseUtils.USERS_PATH + myUsername + DatabaseUtils.FOLLOWING_PATH + followedUsername).get()
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
                .child(DatabaseUtils.USERS_PATH + myUsername + DatabaseUtils.FOLLOWING_PATH + targetUserUsername).setValue(value)
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
                .child(DatabaseUtils.USERS_PATH + targetUserUsername + DatabaseUtils.FOLLOWERS_PATH + myUsername).setValue(value)
                .addOnCompleteListener(task -> System.out.println(myUsername + " is now set to " + value + " in " + targetUserUsername + " followers list."));
    }

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
                                    .map(nearbyUsername -> getUser(nearbyUsername)
                                            .thenAccept(user -> users.add(user)))
                                    .toArray(CompletableFuture[]::new)
                    ).thenRun(() -> future.complete(users));
                }
        );
        return future;
    }

    /**
     * Gets all the users
     * @param username of the reference user
     * @return a completable future with a list of users containing all the user other than the
     *          user itself.
     */
    public static CompletableFuture<List<User>> getAllUser(String username) {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        UserDatabase.database.getReference(DatabaseUtils.USERS_PATH).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                List<User> allUsers = new ArrayList<>();
                dataSnapshot.getChildren().forEach(
                        userSnapshot -> {
                            String userKey = userSnapshot.getKey();
                            if (!userKey.equals(username)) {
                                allUsers.add(userSnapshot.getValue(User.class));
                            }
                        }
                );
                future.complete(allUsers);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    /**
     * Gets all the users the user is following
     * @param username of the reference user
     * @return a completable future with a list of users
     */
    public static CompletableFuture<List<User>> getFollowingUsers(String username) {
        CompletableFuture<List<User>> future = new CompletableFuture<>();

        getUser(username).thenApply(user -> {
            List<String> followingUsernames = user.getFollowing(); // Assuming following is a List<String> in the User object
            List<User> followingUsers = new ArrayList<>();
            List<CompletableFuture<User>> futures = new ArrayList<>();

            for (String followingUsername : followingUsernames) {
                CompletableFuture<User> followingUserFuture = getUser(followingUsername);
                futures.add(followingUserFuture);
            }

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            allFutures.thenRun(() -> {
                for (CompletableFuture<User> followingUserFuture : futures) {
                    User followingUser = followingUserFuture.join();
                    followingUsers.add(followingUser);
                }
                future.complete(followingUsers);
            });

            return null;
        });

        return future;
    }
}

