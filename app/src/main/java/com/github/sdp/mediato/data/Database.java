package com.github.sdp.mediato.data;

import android.net.Uri;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Collection;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import java.util.concurrent.CompletableFuture;

public class Database implements GenericDatabase {

  public static final String USER_PATH = "Users/";

  public static final String USER_COLLECTIONS_PATH = "/collections/";
  public static final String USER_PROFILE_PICS_PATH = "ProfilePics/";

  public static final int PROFILE_PIC_MAX_SIZE = 1024 * 1024; //1 Megabyte

  public static FirebaseDatabase database = FirebaseDatabase.getInstance();
  public static StorageReference profilePics = FirebaseStorage.getInstance().getReference()
      .child(USER_PROFILE_PICS_PATH);

  /**
   * Adds a new user to the database with the profile pic
   *
   * @param user to be added
   * @return a CompletableFuture containing the username of the user that has been added
   * @uri the uri to the profile pic
   */
  public static CompletableFuture<String> addUser(User user, Uri profilePicUri) {
    CompletableFuture<String> future = new CompletableFuture<>();
    database.getReference().child(USER_PATH + user.getUsername()).setValue(user,
        (error, ref) -> {
          if (error == null) {
            future.complete(user.getUsername());
          } else {
            future.completeExceptionally(error.toException());
          }
        }
    );
    UploadTask uploadProfilePic = profilePics.child(user.getUsername() + ".jpg")
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
    database.getReference().child(USER_PATH + user.getUsername()).setValue(user,
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
   * @param username
   * @param profilePicUri
   */
  public static StorageTask<TaskSnapshot> setProfilePic(String username, Uri profilePicUri) {
    return profilePics.child(username + ".jpg")
        .putFile(profilePicUri).addOnSuccessListener(
            taskSnapshot -> {
              taskSnapshot.getBytesTransferred();
            }
        );
  }

  /**
   * Deletes user from the database
   *
   * @param username of the user to be deleted
   * @return a completable future with the username of the deleted user
   * @TODO handle cases for removing user from friends lists...
   */
  public static CompletableFuture<String> deleteUser(String username) {
    CompletableFuture<String> future = new CompletableFuture<>();
    database.getReference().child(USER_PATH + username).setValue(null,
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
    database.getReference().child(USER_PATH + username).get().addOnSuccessListener(
        dataSnapshot -> {
          if (dataSnapshot.getValue() == null) {
            future.completeExceptionally(new NoSuchFieldException());
          } else {
            future.complete(dataSnapshot.getValue(User.class));
          }
        }).addOnFailureListener(e -> future.completeExceptionally(e));

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
    database.getReference().child(USER_PATH + username).get().addOnSuccessListener(
        dataSnapshot -> {
          unique.complete(!dataSnapshot.exists());
        }
    ).addOnFailureListener(e -> unique.completeExceptionally(e));
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
        .addOnFailureListener(e -> future.completeExceptionally(e));
    return future;
  }


  public static CompletableFuture<String> addCollection(String username, Collection collection) {
    CompletableFuture<String> future = new CompletableFuture<>();
    database.getReference()
        .child(USER_PATH + username + USER_COLLECTIONS_PATH + collection.getCollectionName())
        .setValue(collection,
            (error, ref) -> {
              System.out.println("adding collection " + collection.getReviews().size());
              if (error == null) {
                future.complete(collection.getCollectionName());
              } else {
                future.completeExceptionally(error.toException());
              }
            }
        );
    return future;
  }

}
