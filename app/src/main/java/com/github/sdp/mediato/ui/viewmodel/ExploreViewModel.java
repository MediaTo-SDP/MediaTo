package com.github.sdp.mediato.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.data.UserDatabase;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.post.Post;
import com.github.sdp.mediato.model.post.ReviewPost;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The ViewModel for the {@link com.github.sdp.mediato.ui.ExploreFragment}
 */
public class ExploreViewModel extends AndroidViewModel {

    Application application;
    private final MutableLiveData<List<ReviewPost>> posts = new MutableLiveData<List<ReviewPost>>();
    String username;
    //add posts cache

    public ExploreViewModel(@NonNull Application application, String username) {
        super(application);
        this.application = application;
        this.username = username;
    }

    public LiveData<List<ReviewPost>> getPosts(){
        createNearbyUsersPosts();
        return posts;
    }

    public void createNearbyUsersPosts(){
        CompletableFuture<List<User>> future = UserDatabase.getNearbyUsers(username, 0);
        List<ReviewPost> reviewPosts = new ArrayList<>();
        future.thenAccept(nearbyUsers -> nearbyUsers.forEach(
                user -> {
                    reviewPosts.addAll(user.fetchReviewPosts());
                }
        ));
        posts.setValue(reviewPosts);
    }
}
