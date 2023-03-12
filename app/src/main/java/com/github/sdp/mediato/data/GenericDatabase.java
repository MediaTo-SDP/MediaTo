package com.github.sdp.mediato.data;

import com.github.sdp.mediato.model.User;

import java.util.concurrent.CompletableFuture;

public interface GenericDatabase {

    //Adds a user to the database
    public static void addUser(User user){}

    //Deletes a user from the database
    public static void deleteUser(User user){}

    //Retrieves a completable future with the user from the database
    public static CompletableFuture<User> getUser(String username){return null;}

    //Retrieves a completable future with the byte array representing the profile pic
    public static CompletableFuture<byte[]> getProfilePic(String username){return null;}

}
