package com.github.sdp.mediato.ui.viewmodel;

import android.graphics.Bitmap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.github.sdp.mediato.model.media.Collection;

/**
 * The ViewModel for the ProfileFragment. Handles all communication between the UI and the data
 * layer.
 */
public class ProfileViewModel extends ViewModel {

  private MutableLiveData<Collection> collectionLiveData;
  private MutableLiveData<String> usernameLiveData;
  private MutableLiveData<Bitmap> profilePicLiveData;

  public ProfileViewModel() {
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
