package com.github.sdp.mediato.cache;

import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.cache.models.CachedReviewPost;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.post.ReviewPost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class CacheHelper {
    private CacheHelper(){
        // CacheHelper should not be instantiated
    }

    public static void insertAllReviewPostIn(List<ReviewPost> posts, AppCache cache, boolean myFeed){
        CompletableFuture.supplyAsync(() -> {
            List<CachedReviewPost> cachedPosts = List.copyOf(posts).stream().map(reviewPost ->
                new CachedReviewPost(reviewPost.getId(), myFeed,
                        reviewPost.getUsername(), reviewPost.getGrade(),
                        reviewPost.getComment(), reviewPost.getCollectionName(),
                        reviewPost.getMedia().getMediaType(), reviewPost.getMedia().getId()))
                .collect(Collectors.toList());

            List<Media> medias = List.copyOf(posts).stream()
                    .map(ReviewPost::getMedia)
                    .collect(Collectors.toList());

            try {
                cache.reviewDao().clearFeed(myFeed);
                System.out.println(Arrays.toString(cache.reviewDao().insertAll(cachedPosts)));
                cache.mediaDao().insertAll(medias);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
            return null;
    });
    }

    public static void setReviewPostFrom(AppCache cache, boolean myFeed, MutableLiveData<List<ReviewPost>> toSet) {
        CompletableFuture.supplyAsync(() -> {
            List<CachedReviewPost> cachedPosts;
            List<ReviewPost> reviewPosts = new ArrayList<>();
            try {
                cachedPosts = cache.reviewDao().getAllReviews(myFeed);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return new ArrayList<>();
            }

            for (CachedReviewPost post : cachedPosts) {
                Media media;
                try {
                    media = cache.mediaDao().getMediaFromTypeAndId(post.getMediaType(), post.getMediaId());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return new ArrayList<>();
                }

                Review review = new Review(post.getUsername(), media, post.getGrade(), post.getComment());
                Collection collection = new Collection(post.getCollectionName());

                reviewPosts.add(new ReviewPost(post.getUsername(), review, collection));
            }
            toSet.setValue(reviewPosts);
            return null;
        });
    }
}
