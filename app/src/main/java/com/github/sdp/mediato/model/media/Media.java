package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.errorCheck.Preconditions;

import java.util.List;


public class Media {

    private MediaType mediaType;
    private String title;
    private String summary;
    private String posterUrl;

    // Less than 200px wide
    private String iconUrl;

    private String id;

    Media() {
    }


    /**
     * @param mediaType
     * @param title
     * @param summary
     * @param imageUrl
     * @param id
     */
    public Media(MediaType mediaType, String title, String summary, String imageUrl, int id) {
        this(mediaType, title, summary, imageUrl, imageUrl, id);
    }
    public Media(MediaType mediaType, String title, String summary, String posterUrl, String iconUrl, int id) {
        this(mediaType, title, summary, posterUrl, iconUrl, Integer.toString(id));
    }

    public Media(MediaType mediaType, String title, String summary, String posterUrl, String iconUrl, String id) {
        Preconditions.checkMedia(mediaType, List.of(title, summary, posterUrl, iconUrl));
        this.mediaType = mediaType;
        this.title = title;
        this.summary = summary;
        this.posterUrl = posterUrl;
        this.iconUrl = iconUrl;
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

    /**
     * @deprecated use {@link #getIconUrl()} or {@link #getPosterUrl()} instead for higher granularity
     */
    @Deprecated
    public String getImageURL() {
        return getPosterUrl();
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public int getId() {
        return Integer.parseInt(id);
    }
    public String getStringId() {return id;}

}
