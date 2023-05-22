package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sdp.mediato.ui.MainActivity;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Location;
import com.github.sdp.mediato.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserViewModel extends AndroidViewModel {

    protected final MutableLiveData<String> watchedUsernameLiveData = new MutableLiveData<>();
    protected final MutableLiveData<String> connectedUsernameLiveData = new MutableLiveData<>();
    protected final MutableLiveData<User> connectedUserLiveData = new MutableLiveData<>();
    protected final MutableLiveData<List<User>> userListLiveData = new MutableLiveData<>();

    public UserViewModel(Application application) {
        super(application);
        watchedUsernameLiveData.setValue("None");
        connectedUsernameLiveData.setValue("None");
        connectedUserLiveData.setValue(new User.UserBuilder("None")
                .setUsername("None")
                .setEmail("None")
                .setRegisterDate("01/01/0001")
                .setLocation(new Location(3.14, 3.14))
                .build()
        );
        userListLiveData.setValue(new ArrayList<>());
    }

    public void setConnectedUsername(String username) {
        this.connectedUsernameLiveData.setValue(username);
        reloadUser();
    }

    public void reloadUser() {
        UserDatabase.getUser(this.connectedUsernameLiveData.getValue())
                .thenAccept(value -> {
                    this.connectedUserLiveData.postValue(value);
                    value.getFollowing().forEach(System.out::println);
                });
    }

    public User getConnectedUser() {
        return connectedUserLiveData.getValue();
    }

    public LiveData<User> getConnectedUserLiveData() {
        return connectedUserLiveData;
    }

    public LiveData<List<User>> getUserListLiveData() {
        return userListLiveData;
    }

    public void setUserList(List<User> userList) {
        this.userListLiveData.postValue(userList);
    }

    public void clearUserList() {
        userListLiveData.postValue(new ArrayList<>());
    }

    public String getConnectedUsername() {
        return this.connectedUsernameLiveData.getValue();
    }

    public void setWatchedUsername(String username) {
        this.watchedUsernameLiveData.setValue(username);
    }

    public String getWatchedUsername() {
        return watchedUsernameLiveData.getValue();
    }

    public void loadFollowingFollower(List<String> listFollowingFollowerUsername) {
        clearUserList();
        List<User> listUser = new ArrayList<>();
        CompletableFuture[] futures = new CompletableFuture[listFollowingFollowerUsername.size()];
        int i = 0;

        sortUsersByName(listFollowingFollowerUsername);

        for (String username : listFollowingFollowerUsername) {
            futures[i++] = UserDatabase.getUser(username).thenAccept(listUser::add);
        }

        // Wait for all the CompletableFuture to complete
        CompletableFuture.allOf(futures).thenRun(() -> setUserList(listUser));
    }

    private static void sortUsersByName(List<String> listFollowingFollowerUsername) {
        Collections.sort(listFollowingFollowerUsername, Comparator.comparing(u -> u.toLowerCase()));
    }
}