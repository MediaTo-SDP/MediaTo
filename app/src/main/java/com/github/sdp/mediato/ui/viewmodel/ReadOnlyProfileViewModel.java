package com.github.sdp.mediato.ui.viewmodel;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.media.Collection;
import java.util.List;

/**
 * The ViewModel for the ProfileFragment. Handles all communication between the UI and the data
 * layer.
 */
public class ReadOnlyProfileViewModel extends ViewModel {

  protected MutableLiveData<List<Collection>> collectionsLiveData;
  protected MutableLiveData<String> usernameLiveData;
  protected MutableLiveData<Bitmap> profilePicLiveData;
  protected MutableLiveData<Integer> followingLiveData;
  protected MutableLiveData<Integer> followersLiveData;

  public ReadOnlyProfileViewModel() {
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

