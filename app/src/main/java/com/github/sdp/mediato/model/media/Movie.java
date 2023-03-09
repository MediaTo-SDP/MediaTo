package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.model.media.MediaType;

public class Movie extends Media{
    public Movie(MediaType mediaType, String title, String summary, String imageUrl) {
        super(MediaType.MOVIE, title, summary, imageUrl);
    }

    @Override
    public String toString() {
        return "Movie title: " + getTitle() + "\n" +
                "Summary: " + getSummary();
    }
}
