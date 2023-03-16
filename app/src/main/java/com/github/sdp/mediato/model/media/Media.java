package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.errorCheck.Preconditions;

import java.util.Objects;

public class Media {

    private MediaType mediaType;
    private String title;
    private String summary;
    private String imageUrl;


    Media(){}

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
