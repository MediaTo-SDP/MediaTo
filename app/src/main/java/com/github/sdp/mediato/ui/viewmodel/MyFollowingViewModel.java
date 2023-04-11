package com.github.sdp.mediato.ui.viewmodel;

import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MyFollowingViewModel extends UserViewModel {
    public void reloadUser() {
        UserDatabase.getUser(getUserName()).thenAccept(value -> {
            userLiveData.setValue(value);
            reloadFollowings();
        });
    }

    public void reloadFollowings() {
        clearUserList();
        List<User> followings = new ArrayList<>();
        CompletableFuture[] futures = new CompletableFuture[getUser().getFollowing().size()];
        int i = 0;

        List<String> followingUserNames = getUser().getFollowing();

        Collections.sort(followingUserNames);

        for (String username : followingUserNames) {
            futures[i++] = UserDatabase.getUser(username).thenAccept(followings::add);
        }

        // Wait for all the CompletableFuture to complete
        CompletableFuture.allOf(futures).thenRun(() -> setUserList(followings));
    }
}