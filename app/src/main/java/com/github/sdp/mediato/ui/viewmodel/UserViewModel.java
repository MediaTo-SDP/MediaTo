package com.github.sdp.mediato.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;

import java.util.ArrayList;
import java.util.List;

public abstract class UserViewModel extends ViewModel {
    protected final MutableLiveData<String> userNameLiveData = new MutableLiveData<>();
    protected final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    protected final MutableLiveData<List<User>> userListLiveData = new MutableLiveData<>();

    public UserViewModel() {
        userNameLiveData.setValue("None");
        userLiveData.setValue(new User.UserBuilder("None")
                .setUsername("None")
                .setEmail("None")
                .setRegisterDate("01/01/0001")
                .setLocation(new Location(3.14, 3.14))
                .build()
        );
        userListLiveData.setValue(new ArrayList<>());
    }

    public String getUserName() {
        return userNameLiveData.getValue();
    }

    public void setUserName(String userName) {
        this.userNameLiveData.setValue(userName);
        reloadUser();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public User getUser() {
        return userLiveData.getValue();
    }

    public LiveData<List<User>> getUserListLiveData() {
        return userListLiveData;
    }

    public List<User> getUserList() {
        return userListLiveData.getValue();
    }

    public void setUserList(List<User> userList) {
        this.userListLiveData.setValue(userList);
    }

    public void clearUserList() {
        userListLiveData.setValue(new ArrayList<>());
    }

    public abstract void reloadUser();
}