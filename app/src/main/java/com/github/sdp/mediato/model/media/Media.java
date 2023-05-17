package com.github.sdp.mediato.model.media;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.github.sdp.mediato.errorCheck.Preconditions;

import java.io.Serializable;

import java.util.List;


@Entity(primaryKeys = {"mediaType", "id"}, tableName = "medias")
public class Media implements Serializable {

    @NonNull
    private MediaType mediaType;
    private String title;
    private String summary;
    private String posterUrl;

    // Less than 200px wide
    private String iconUrl;
    @NonNull
    private String id;

    /**
     * Custom constructor that allows a quick creation of a media from an already defined one
     * @param media the media we want to adapt / copy
     * @param <T> Any class extending THIS Media class
     */
    public <T extends Media> Media(T media){
        this(media.getMediaType(), media);
    }
    protected <T extends Media> Media(MediaType type, T media) {
        this(type, media.getTitle(), media.getSummary(), media.getPosterUrl(), media.getIconUrl(), media.getId());
    }

    /**
     * Custom constructor that accepts only one image url
     * @param mediaType the type of the media
     * @param title the title of the media
     * @param summary the summary of the media (might be empty)
     * @param imageUrl the url of the image representing the media
     * @param id the id of the media provided by the API
     */
    public Media(MediaType mediaType, String title, String summary, String imageUrl, String id) {
        this(mediaType, title, summary, imageUrl, imageUrl, id);
    }

    /**
     * Custom constructor that accepts two images and the id as an int
     * @param mediaType the type of the media
     * @param title the title of the media
     * @param summary the summary of the media (might be empty)
     * @param imageURL the url of the image representing the media
     * @param id the id of the media provided by the API as an int
     */
    @Deprecated
    public Media(MediaType mediaType, String title, String summary, String imageURL, int id) {
        this(mediaType, title, summary, imageURL, imageURL, Integer.toString(id));
    }

    /**
     * Default constructor
     * @param mediaType the type of the media
     * @param title the title of the media
     * @param summary the summary of the media (might be empty)
     * @param iconUrl the url of the icon representing the media (<200 pixel height)
     * @param posterUrl the url of the poster representing the media (full size)
     * @param id the id of the media provided by the API as a string
     */
    public Media(@NonNull MediaType mediaType, String title, String summary, String posterUrl, String iconUrl, @NonNull String id) {
        Preconditions.checkMedia(mediaType, List.of(title, summary, posterUrl, iconUrl));
        this.mediaType = mediaType;
        this.title = title;
        this.summary = summary;
        this.posterUrl = posterUrl;
        this.iconUrl = iconUrl;
        this.id = id;
    }

    @Ignore
    Media(){

    }

    /**
     * Getter for the MediaType
     * @return the media type
     */
    @NonNull
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * Getter for the title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for the summary
     * @return the summary
     */
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

    /**
     * Getter for the url of the poster image (Full-size)
     * @return the url of the image
     */
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * Getter for the url of the icon image ( < 200 pixels height)
     * @return the url of the image
     */
    public String getIconUrl() {
        return iconUrl;
    }

    /**
     * Getter for the id
     * @return the id
     */
    @NonNull
    public String getId() {return id;}

    public void setSummary(String summary) {this.summary = summary;}

    /**
     * Checks if two medias are equal
     * @param other the media to compare with
     * @return if they are completely equal in term of data
     */
    public boolean isTheSame(Media other){
        return this.mediaType == other.mediaType && this.id.equals(other.id) &&
                this.title.equals(other.title) && this.summary.equals(other.summary) &&
                this.iconUrl.equals(other.iconUrl) && this.posterUrl.equals(other.posterUrl);
    }
}
