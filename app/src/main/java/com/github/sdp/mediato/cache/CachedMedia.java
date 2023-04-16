package com.github.sdp.mediato.cache;

import android.os.Build;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;


@Entity(tableName = "media")
public class CachedMedia extends Media {
    @PrimaryKey(autoGenerate = true)
    private String cacheId;
    private final long creationTime;

    public CachedMedia(MediaType mediaType, String title, String summary, String imageUrl, int id) {
        super(mediaType, title, summary, imageUrl, id);
        creationTime = System.currentTimeMillis();
    }

    public CachedMedia(MediaType mediaType, String title, String summary, String posterUrl, String iconUrl, int id) {
        super(mediaType, title, summary, posterUrl, iconUrl, id);
        creationTime = System.currentTimeMillis();
    }
}
