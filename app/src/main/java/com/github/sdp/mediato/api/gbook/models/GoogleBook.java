package com.github.sdp.mediato.api.gbook.models;

import com.github.sdp.mediato.errorCheck.Preconditions;

import java.util.List;

import javax.annotation.Nullable;

public final class GoogleBook {
    private static final String BASE_ICON_URL =
            "https://books.google.com/books/content/images/frontcover/%id?fife=w132-h132";
    private static final String BASE_POSTER_URL =
            "https://books.google.com/books/content/images/frontcover/%id?fife=w3000-h6000";
    private String id;
    private VolumeInfo volumeInfo;

    private GoogleBook(){
        // Not possible to create a custom GoogleBook
    }

    /**
     * Get the Google unique ID of the book
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the title of the book
     * @return the title
     */
    public String getTitle() {
        return volumeInfo.title;
    }

    /**
     * Get the subtitle of the book
     * @return the subtitle
     */
    public String getSubtitle() {
        return volumeInfo.subtitle;
    }

    /**
     * Get the summary
     * @return the summary (might be null or empty
     */
    @Nullable
    public String getOverview() {
        return volumeInfo.description;
    }

    /**
     * Get the authors of the book
     * @return a {@link List} of authors as a string
     */
    public List<String> getAuthors() {
        return volumeInfo.authors;
    }

    /**
     * Get the published date (often the year)
     * @return the date as a string
     */
    public String getPublishedDate() {
        return volumeInfo.publishedDate;
    }

    /**
     * Get the url of the cover in icon / thumbnail size (less than 200px height)
     * @return the url as a string
     */
    public String getIconURL(){
        // Good thumbnail are often provided
        String thumbnail = volumeInfo.imageLinks.thumbnail;
        return (thumbnail != null && !thumbnail.equals("")) ?
                thumbnail : BASE_ICON_URL.replace("%id", id);
    }

    /**
     * Get the url of the cover in poster size (greatest available size)
     * @return the url as a string
     */
    public String getPosterURL(){
        // almost no good poster size image is provided
        return BASE_POSTER_URL.replace("%id", id);
    }

    private static class VolumeInfo {
        private String title;
        private String subtitle;
        private String description;
        private List<String> authors;
        private String publishedDate;

        private ImageLinks imageLinks;


        private VolumeInfo(){
            // Not possible to create a custom VolumeInfo
        }
        private static class ImageLinks {
            private String smallThumbnail;
            private String thumbnail;
            private String small;
            private String medium;
            private String large;
            private String extraLarge;


        }
    }
}
