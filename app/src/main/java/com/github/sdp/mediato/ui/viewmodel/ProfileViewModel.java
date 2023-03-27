package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;

/**
 * The ViewModel for the ProfileFragment. Handles all communication between the UI and the data
 * layer.
 */
public class ProfileViewModel extends AndroidViewModel {

  private MutableLiveData<Collection> collectionLiveData;
  private MutableLiveData<String> usernameLiveData;
  private MutableLiveData<Bitmap> profilePicLiveData;

  public ProfileViewModel(Application application) {
    super(application);
    collectionLiveData = new MutableLiveData<>();
    usernameLiveData = new MutableLiveData<>();
    profilePicLiveData = new MutableLiveData<>();
  }

  public LiveData<Collection> getCollectionLiveData() {
    return collectionLiveData;
  }


  public LiveData<String> getUsernameLiveData() {
    return usernameLiveData;
  }


  public LiveData<Bitmap> getProfilePicLiveData() {
    return profilePicLiveData;
  }


  public void setCollection(Collection collection) {
    collectionLiveData.setValue(collection);
  }

  public void addReviewToCollection(Review review) {
    Collection collection = getCollection();
    collection.addReview(review);
    collectionLiveData.setValue(collection);
  }

  public void setUsername(String username) {
    usernameLiveData.setValue(username);
  }


  public void setProfilePic(Bitmap profilePic) {
    profilePicLiveData.setValue(profilePic);
  }

  public Collection getCollection() {
    return collectionLiveData.getValue();
  }

}

