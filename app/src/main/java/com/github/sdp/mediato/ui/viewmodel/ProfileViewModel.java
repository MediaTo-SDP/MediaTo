package com.github.sdp.mediato.ui.viewmodel;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
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

  public ProfileViewModel() {
    collectionsLiveData = new MutableLiveData<>();
    usernameLiveData = new MutableLiveData<>();
    profilePicLiveData = new MutableLiveData<>();
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
    Preconditions.checkUsername(username);
    usernameLiveData.setValue(username);
  }


  public void setProfilePic(@NonNull Bitmap profilePic) {
    profilePicLiveData.setValue(profilePic);
  }

  public void addReviewToCollection(@NonNull Review review, String collectionName) {
    Preconditions.checkNullOrEmptyString(collectionName, "collectionName");
    Collection collection = getCollection(collectionName);

    collection.addReview(review);

    // Notify observers about the change in collections
    collectionsLiveData.setValue(getCollections());
  }

  @Deprecated
  public void addReviewToCollection(@NonNull Review review) {
    addReviewToCollection(review, "Recently watched");
  }


  /**
   * Adds a collection with the chosen name if no collection of that name already exists, or makes
   * no changes if such a collection already exists.
   *
   * @param collectionName
   * @return
   */
  public boolean addCollection(String collectionName) {
    Preconditions.checkNullOrEmptyString(collectionName, "collectionName");
    List<Collection> collections = getCollections();

    for (Collection collection : collections) {
      if (collection.getCollectionName().equals(collectionName)) {
        // TODO add a Toast or something to notify to the user that two collections cannot have the same name
        return false;
      }
    }
    Collection newCollection = new Collection(collectionName);
    collections.add(newCollection);

    // Notify observers about the change in collections
    collectionsLiveData.setValue(collections);
    return true;
  }

  /**
   * Returns the collection with the corresponding name in the viewmodel, or throws an error if such
   * a collection does not exist.
   *
   * @param collectionName
   * @return
   */
  private Collection getCollection(String collectionName) {
    Preconditions.checkNullOrEmptyString(collectionName, "collectionName");
    List<Collection> collections = getCollections();

    for (Collection collection : collections) {
      if (collection.getCollectionName().equals(collectionName)) {
        return collection;
      }
    }
    // TODO handle collection not found error
    throw new IllegalArgumentException("A collection with this name does not exist.");
  }

}

