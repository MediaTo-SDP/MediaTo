package com.github.sdp.mediato.model.media;

public class Media {

    private final MediaType mediaType;
    private final String title;
    private final String summary;

    public Media(MediaType mediaType, String title, String summary) {
        this.mediaType = mediaType;
        this.title = title;
        this.summary = summary;
    }

    public MediaType getMediaType() {return mediaType;}

    public String getTitle() {return title;}

    public String getSummary() {return summary;}
}
