package com.github.sdp.mediato.cache.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
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

    @Query("SELECT * FROM medias")
    List<Media> getAllMedia();
    @Query("SELECT * FROM medias WHERE MediaType = :type")
    List<Media> getAllMediaFromType(MediaType type);

    @Query("SELECT * FROM medias WHERE mediaType = :mediaType AND id = :id LIMIT 1")
    Media getMediaFromTypeAndId(MediaType mediaType, String id);
    @Query("DELETE FROM medias")
    void cleanMedias();

    @Query("SELECT * FROM medias WHERE mediaType = :mediaType AND title LIKE '%' || :searchTerm || '%'")
    List<Media> searchInTitle(MediaType mediaType, String searchTerm);

    @Query("SELECT * FROM (SELECT * FROM medias WHERE " +
            "mediaType = :mediaType AND title LIKE '%' || :searchTerm || '%') " +
            "UNION SELECT * FROM (SELECT * FROM medias WHERE " +
            "mediaType = :mediaType AND summary LIKE '%' || :searchTerm || '%')")
    List<Media> search(MediaType mediaType, String searchTerm);
}
