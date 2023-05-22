package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.data.DatabaseUtils;
import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.post.ReviewPost;
import com.github.sdp.mediato.ui.FeedFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FeedViewModel extends AndroidViewModel {
    Application application;
    private final MutableLiveData<List<ReviewPost>> posts = new MutableLiveData<>(new ArrayList<>());
    private String username;
    public FeedViewModel (@NonNull Application application) {
        super(application);
        this.application = application;
    }

    /**
     * Sets the username of the user who is currently logged in and generates the list of posts
     * @param username the username of the user who is currently logged in
     */
    public void setData(String username, FeedFragment.FeedType feedType) {
        this.username = username;
        switch (feedType) {
            case MY_REVIEWS:
                createMyPosts();
                break;
            case FEED:
                createFollowingsPosts();
                break;
        }
    }

    /**
     * Returns the list of posts
     * @return the list of posts
     */
    public LiveData<List<ReviewPost>> getPosts(){
        return posts;
    }

    /**
     * Generates the list of posts from the users that the current user is following
     */
    public void createFollowingsPosts() {
        UserDatabase.getFollowingUsers(username)
                .thenApply(users -> {
                    List<ReviewPost> posts = new ArrayList<>();
                    for (User user : users) {
                        posts.addAll(user.fetchReviewPosts());
                    }
                    return posts;
                })
                .thenAccept(posts::setValue);
    }

    /**
     * Generates the list of posts from the current user
     */
    public void createMyPosts() {
        UserDatabase.getUser(username)
                .thenApply(user -> user.fetchReviewPosts())
                .thenAccept(posts::setValue);
    }

}
