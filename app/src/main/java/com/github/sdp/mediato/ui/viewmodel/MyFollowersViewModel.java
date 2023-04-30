package com.github.sdp.mediato.ui.viewmodel;

import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MyFollowersViewModel extends UserViewModel {

    /**
     * Gets the user data from the database. Updates the user's "followers" list by
     * loading all the users that follow the user from the database
     */
    public void reloadUser() {
        UserDatabase.getUser(getUserName()).thenAccept(value -> {
            userLiveData.setValue(value);
            reloadFollowingFollower(getUser().getFollowers());
        });
    }
}