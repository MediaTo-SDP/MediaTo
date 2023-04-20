package com.github.sdp.mediato.cache.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.sdp.mediato.cache.models.CachedMedia;

import java.util.List;

@Dao
public interface MediaDao {
    @Insert
    void insert(CachedMedia... media);

    @Delete
    void delete(CachedMedia media);

    @Query("SELECT * FROM medias")
    List<CachedMedia> getAllMedia();

    @Query("SELECT * FROM medias WHERE mediaType = :mediaType AND id = :id")
    CachedMedia getMediaFromTypeAndMid(int mediaType, String id);

    @Query("SELECT * FROM medias INNER JOIN searchresmediacrossref as crossref ON (medias.rowid = crossref.rowId)")
    CachedMedia getMediafromSearchTerm(int rowid);
}
