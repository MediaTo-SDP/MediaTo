package com.github.sdp.mediato.cache.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;

import java.util.List;

@Dao
public interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(Media... media);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<Media> media);
    @Delete
    void delete(Media media);

    @Query("SELECT * FROM medias")
    Media[] getAllMedia();

    @Query("SELECT * FROM medias WHERE mediaType = :mediaType AND id = :id LIMIT 1")
    Media getMediaFromTypeAndId(MediaType mediaType, String id);
    @Query("DELETE FROM medias")
    void cleanMedias();

    /* Order by id to allow a O(n) merge in MediaCache*/
    @Query("SELECT * FROM medias WHERE mediaType = :mediaType AND title MATCH :searchTerm ORDER BY id")
    List<Media> searchInTitle(MediaType mediaType, String searchTerm);

    @Query("SELECT * FROM medias WHERE mediaType = :mediaType AND summary MATCH :searchTerm || '*' ORDER BY id")
    List<Media> searchInSummary(MediaType mediaType, String searchTerm);
}
