package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;

import com.github.sdp.mediato.data.UserDatabase;

public class FollowerViewModel extends UserViewModel{

    public FollowerViewModel(Application application) {
        super(application);
    }

    @Override
    public void reloadUser() {
        UserDatabase.getUser(this.connectedUsernameLiveData.getValue())
                .thenAccept(value -> {
                    this.connectedUserLiveData.postValue(value);
                    if (getConnectedUsername().equals(getWatchedUsername())) {
                        loadFollowingFollower(value.getFollowers());
                    }
                });
    }
}
