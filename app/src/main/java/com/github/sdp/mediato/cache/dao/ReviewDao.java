package com.github.sdp.mediato.cache.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.sdp.mediato.cache.models.CachedReviewPost;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    long[] insertAll(List<CachedReviewPost> reviews);

    @Query("DELETE FROM reviews WHERE myFeed = :myFeed")
    void clearFeed(boolean myFeed);

    @Query("SELECT * FROM reviews WHERE myFeed = :myFeed")
    List<CachedReviewPost> getAllReviews(boolean myFeed);
}
