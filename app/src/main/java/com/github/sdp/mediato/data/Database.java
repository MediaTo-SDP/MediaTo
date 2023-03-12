package com.github.sdp.mediato.data;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.github.javafaker.Bool;
import com.github.sdp.mediato.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.CompletableFuture;

public class Database implements GenericDatabase {

    public static final String USER_PATH = "Users/";
    public static final String USER_PROFILE_PICS_PATH = "ProfilePics/";

    public static final int PROFILE_PIC_MAX_SIZE = 1024*1024; //1 Megabyte

    public static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static StorageReference profilePics = FirebaseStorage.getInstance().getReference().child(USER_PROFILE_PICS_PATH);

    /**
     * Adds a new user to the database with the profile pic
     * @param user to be added
     * @uri the uri to the profile pic
     */
    public static void addUser(User user, Uri profilePicUri){
        database.child(USER_PATH + user.getUsername()).setValue(user);
        UploadTask uploadProfilePic = profilePics.child(user.getUsername()+".jpg")
                .putFile(profilePicUri);
    }
    /**
     * Adds a new user to the database without the profile pic
     * @param user to be added
     */
    public static void addUser(User user){
        database.child(USER_PATH + user.getUsername()).setValue(user);
    }

    /**
     * Uploads the profile pic corresponding to username
     * @param username
     * @param profilePicUri
     */
    public static void setProfilePic(String username, Uri profilePicUri){
        UploadTask uploadProfilePic = profilePics.child(username+".jpg")
                .putFile(profilePicUri);
    }

    /**
     * Deletes user from the database
     * @param user to be deleted
     * @TODO handle cases for removing user from friends lists...
     */
    public static void deleteUser(User user) {
        database.child(USER_PATH + user.getUsername()).setValue(null);
    }

    /**
     * Retrieve a CompletableFuture containing the user
     * @param username of the user to be retrieved
     * @return CompletableFuture<User> containing the user object
     */
    public static CompletableFuture<User> getUser(String username){
        CompletableFuture<User> future = new CompletableFuture<>();
        database.child(USER_PATH + username).get().addOnSuccessListener(
                dataSnapshot-> {
                    if (dataSnapshot.getValue() == null) future.completeExceptionally(new NoSuchFieldException());
                    else future.complete(dataSnapshot.getValue(User.class));
                }).addOnFailureListener(e->future.completeExceptionally(e));

        return future;
    }

    /**
     * Checks if the username is unique by looking for an already existing user that has it in the database
     * @param username to be checked
     * @return CompletableFuture<Boolean> containing the uniqueness value
     */
    public static CompletableFuture<Boolean> isUsernameUnique(String username){
        CompletableFuture<Boolean> unique = new CompletableFuture<>();
        database.child(USER_PATH + username).get().addOnSuccessListener(
                dataSnapshot -> {unique.complete(!dataSnapshot.exists());}
        ).addOnFailureListener(e->unique.completeExceptionally(e));
        return unique;
    }

    /**
     * Retrieve a CompletableFuture containing a byte[] of the profile pic
     * @param username of the user corresponding to the profile pic
     * @return CompletableFuture<User> containing the byte[] of the pic
     */
    public static CompletableFuture<byte[]> getProfilePic(String username){
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        profilePics.child(username+".jpg").getBytes(PROFILE_PIC_MAX_SIZE)
                .addOnSuccessListener(dataSnapshot-> {
                    if (dataSnapshot == null) future.completeExceptionally(new NoSuchFieldException());
                    else future.complete(dataSnapshot);})
                .addOnFailureListener(e->future.completeExceptionally(e));
        return future;
    }
}
