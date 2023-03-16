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
    private CompletableFuture<Bitmap> image;


    Media() {
    }

    public Media(MediaType mediaType, String title, String summary, String imageUrl) {
        Preconditions.checkMedia(mediaType, title, summary, imageUrl);
        this.mediaType = mediaType;
        this.title = title;
        this.summary = summary;
        this.image = CompletableFuture.supplyAsync(() -> {
            try {
                InputStream imageStream = new URL(imageUrl).openStream();
                return BitmapFactory.decodeStream(imageStream);
            } catch (Exception e) {
                Bitmap image = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
                image.eraseColor(Color.GREEN);
                return image;
            }
        });
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

    public CompletableFuture<Bitmap> getImage() {
        return image;
    }

}
