package com.github.sdp.mediato.cache.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.sdp.mediato.cache.models.CachedMedia;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;

import java.util.List;

@Dao
public interface MediaDao {
    @Insert
    void insert(CachedMedia... media);

    @Insert
    void insertAll(List<CachedMedia> media);
    @Delete
    void delete(CachedMedia media);

    @Query("SELECT * FROM medias")
    List<Media> getAllMedia();

    @Query("SELECT * FROM medias WHERE mediaType = :mediaType AND id = :id LIMIT 1")
    Media getMediaFromTypeAndId(int mediaType, String id);

    @Query("SELECT * FROM medias WHERE mediaType = :mediaType AND title MATCH :searchTerm")
    List<Media> searchInTitle(MediaType mediaType, String searchTerm);

    @Query("SELECT * FROM medias WHERE mediaType = :mediaType AND summary MATCH :searchTerm || '*'")
    List<Media> searchInSummary(MediaType mediaType, String searchTerm);
}
