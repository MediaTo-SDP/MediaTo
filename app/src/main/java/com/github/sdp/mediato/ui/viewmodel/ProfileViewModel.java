package com.github.sdp.mediato.ui.viewmodel;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.github.sdp.mediato.data.CollectionsDatabase;
import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import java.util.List;

/**
 * The ViewModel for the ProfileFragment. Handles all communication between the UI and the data
 * layer.
 */
public class ProfileViewModel extends ViewModel {

  private MutableLiveData<List<Collection>> collectionsLiveData;
  private MutableLiveData<String> usernameLiveData;
  private MutableLiveData<Bitmap> profilePicLiveData;
  private MutableLiveData<Integer> followingLiveData;
  private MutableLiveData<Integer> followersLiveData;

  public ProfileViewModel() {
    collectionsLiveData = new MutableLiveData<>();
    usernameLiveData = new MutableLiveData<>();
    profilePicLiveData = new MutableLiveData<>();
    followingLiveData = new MutableLiveData<>();
    followersLiveData = new MutableLiveData<>();
    followersLiveData.setValue(0);
    followingLiveData.setValue(0);
  }

  public LiveData<List<Collection>> getCollectionsLiveData() {
    return collectionsLiveData;
  }

  public LiveData<String> getUsernameLiveData() {
    return usernameLiveData;
  }

  public LiveData<Bitmap> getProfilePicLiveData() {
    return profilePicLiveData;
  }

  public LiveData<Integer> getFollowingLiveData() {
    return followingLiveData;
  }

  public LiveData<Integer> getFollowersLiveData() {
    return followersLiveData;
  }

  public List<Collection> getCollections() {
    return collectionsLiveData.getValue();
  }

  public String getUsername() {
    return usernameLiveData.getValue();
  }

  public Bitmap getProfilePic() {
    return profilePicLiveData.getValue();
  }

  public void setCollections(@NonNull List<Collection> collections) {
    collectionsLiveData.setValue(collections);
  }

  public void setUsername(String username) {
    usernameLiveData.setValue(username);
  }

  public void setProfilePic(@NonNull Bitmap profilePic) {
    profilePicLiveData.setValue(profilePic);
  }

  public void setFollowing(int following) {
    followingLiveData.setValue(following);
  }

  public void setFollowers(int followers) {
    followersLiveData.setValue(followers);
  }

  /**
   * Adds a review to a collection or does nothing if the collection does not exits.
   *
   * @param review         the review to add
   * @param collectionName the name of the collection to add to
   */
  public void addReviewToCollection(@NonNull Review review, String collectionName) {
    Preconditions.checkNullOrEmptyString(collectionName, "collectionName");
    Collection collection = getCollection(collectionName);
    if (collection == null) {
      return;
    }

    collection.addReview(review);
    CollectionsDatabase.addReviewToCollection(getUsername(), collectionName, review);

    // Notify observers about the change in collections
    collectionsLiveData.setValue(getCollections());
  }

  /**
   * Adds a collection with the chosen name if no collection of that name already exists, or makes
   * no changes if such a collection already exists.
   *
   * @param collectionName name of the collection to add
   * @return true if the collection was sucessfully added, false otherwise
   */
  public boolean addCollection(String collectionName) {
    Preconditions.checkNullOrEmptyString(collectionName, "collectionName");
    List<Collection> collections = getCollections();

    for (Collection collection : collections) {
      if (collection.getCollectionName().equals(collectionName)) {
        return false;
      }
    }
    Collection newCollection = new Collection(collectionName);
    collections.add(newCollection);

    CollectionsDatabase.addCollection(getUsername(), newCollection);

    // Notify observers about the change in collections
    collectionsLiveData.setValue(collections);
    return true;
  }

  /**
   * Returns the collection with the corresponding name in the viewmodel, or throws an error if such
   * a collection does not exist.
   *
   * @param collectionName name of the collection to get
   * @return the collection if it exists, or null otherwise
   */
  public Collection getCollection(String collectionName) {
    Preconditions.checkNullOrEmptyString(collectionName, "collectionName");
    List<Collection> collections = getCollections();

    for (Collection collection : collections) {
      if (collection.getCollectionName().equals(collectionName)) {
        return collection;
      }
    }

    return null;
  }

}

