package com.github.sdp.mediato.cache.models;

import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;

@Fts4
@Entity(tableName = "medias")
public class CachedMedia extends Media {
    @PrimaryKey(autoGenerate = true)
    public int rowid;
    public CachedMedia(MediaType mediaType, String title, String summary, String imageUrl, int id) {
        this(mediaType, title, summary, imageUrl, imageUrl, Integer.toString(id));
    }

    public CachedMedia(MediaType mediaType, String title, String summary, String posterUrl, String iconUrl, int id) {
        this(mediaType, title, summary, posterUrl, iconUrl, Integer.toString(id));
    }

    public CachedMedia(MediaType mediaType, String title, String summary, String posterUrl, String iconUrl, String id) {
        super(mediaType, title, summary, posterUrl, iconUrl, id);
    }
}
