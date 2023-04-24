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
    private final MutableLiveData<List<ReviewPost>> posts = new MutableLiveData<>(new ArrayList<>());
    private String username;
    private LiveData<User> user = new MutableLiveData<User>();
    //add posts cache

    public ExploreViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void setUsername(String username){
        this.username = username;
        createNearbyUsersPosts();
    }

    public LiveData<List<ReviewPost>> getPosts(){
        return posts;
    }

    public void createNearbyUsersPosts() {
        CompletableFuture<List<User>> future = UserDatabase.getAllUser(username);
        future.thenCompose(nearbyUsers -> {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            List<ReviewPost> post = new ArrayList<>();
            for (User user : nearbyUsers) {
                CompletableFuture<Boolean> followsFuture = UserDatabase.follows(username, user.getUsername());
                CompletableFuture<Void> postFuture = followsFuture.thenAccept(follows -> {
                    if (!follows) {
                        System.out.println(username + " doesn't follow " + user.getUsername());
                        post.addAll(user.fetchReviewPosts());
                    }
                });
                futures.add(postFuture);
            }
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            return allFutures.thenApply(v -> post);
        }).thenAccept(posts::setValue);
    }
}
