package com.github.sdp.mediato.api.gbook.models;

import java.util.List;

public class GoogleBook {
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
        return "https://books.google.com/books/content/images/frontcover/%id?fife=w132-h132".replace("%id", id);
    }

    /**
     * Get the url of the cover in poster size (greatest available size)
     * @return the url as a string
     */
    public String getPosterURL(){
        return "https://books.google.com/books/content/images/frontcover/%id?fife=w3000-h6000".replace("%id", id);
    }

    // TODO: Set the class as private if possible
    public class VolumeInfo {
        private String title;
        private String subtitle;



        private List<String> authors;
        private String publishedDate;

        private VolumeInfo(){
            // Not possible to create a custom VolumeInfo
        }


    }
}
