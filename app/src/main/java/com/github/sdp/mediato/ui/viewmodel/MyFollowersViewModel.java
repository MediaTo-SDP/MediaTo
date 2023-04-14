package com.github.sdp.mediato.ui.viewmodel;

import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MyFollowersViewModel extends UserViewModel {

  public void reloadUser() {
    UserDatabase.getUser(getUserName()).thenAccept(value -> {
      userLiveData.setValue(value);
      reloadFollowers();
    });
  }

  public void reloadFollowers() {
    clearUserList();
    List<User> followers = new ArrayList<>();
    List<String> followersUserNames = getUser().getFollowers();
    CompletableFuture[] futures = new CompletableFuture[followersUserNames.size()];
    int i = 0;

    Collections.sort(followersUserNames);

    for (String username : followersUserNames) {
      futures[i++] = UserDatabase.getUser(username).thenAccept(followers::add);
    }

    // Wait for all the CompletableFuture to complete
    CompletableFuture.allOf(futures).thenRun(() -> setUserList(followers));
  }
}