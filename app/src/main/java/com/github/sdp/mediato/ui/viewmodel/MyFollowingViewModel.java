package com.github.sdp.mediato.ui.viewmodel;

import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MyFollowingViewModel extends UserViewModel {

    /**
     * Gets the user data from the database. Updates the user's "following" list by loading all their
     * followed users from the database.
     */
    public void reloadUser() {
        UserDatabase.getUser(getUserName()).thenAccept(value -> {
            userLiveData.setValue(value);
            reloadFollowingFollower(getUser().getFollowing());
        });
    }
}