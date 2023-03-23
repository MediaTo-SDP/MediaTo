package com.github.sdp.mediato.model.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.github.sdp.mediato.errorCheck.Preconditions;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class Media {

    private MediaType mediaType;
    private String title;
    private String summary;
    private String imageUrl;

    private int id;

    Media() {
    }

    public Media(MediaType mediaType, String title, String summary, String imageUrl, int id) {
        Preconditions.checkMedia(mediaType, title, summary, imageUrl);
        this.mediaType = mediaType;
        this.title = title;
        this.summary = summary;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getImageURL() {
        return imageUrl;
    }

    public int getId() { return id; }

}
