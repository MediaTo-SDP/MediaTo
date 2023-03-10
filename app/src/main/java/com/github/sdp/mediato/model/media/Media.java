package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.errorCheck.Preconditions;

import java.util.Objects;

public abstract class Media {

    private final MediaType mediaType;
    private final String title;
    private final String summary;
    private final String imageUrl;


    public Media(MediaType mediaType, String title, String summary, String imageUrl) {
        Preconditions.checkMedia(mediaType,title, summary, imageUrl);
        this.mediaType = mediaType;
        this.title = title;
        this.summary = summary;
        this.imageUrl = imageUrl;
    }

    public MediaType getMediaType() {return mediaType;}

    public String getTitle() {return title;}

    public String getSummary() {return summary;}
    public String getimageUrl() {return imageUrl;}

}
