package com.github.sdp.mediato.model;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.github.sdp.mediato.model.media.MediaType;
import com.github.sdp.mediato.model.media.Movie;

import org.junit.Test;

public class MovieTests {
    private static final String DESCRIPTION = "This is a sample description.";
    private static final String URL = "Sample url";
    private static final String TITLE = "Sample title";
    private static final String OBJECT_STRING = "Movie title: " + TITLE + "\nSummary: " + DESCRIPTION;

    private static final Movie MOVIE = new Movie(TITLE, DESCRIPTION, URL, 1);


    @Test
    public void MediaTypeReturnsMovie() {
        assertThat(MOVIE.getMediaType(), is(MediaType.MOVIE));
    }

    @Test
    public void CanGetTheTitle() {
        assertThat(MOVIE.getTitle(), is(TITLE));
    }

    @Test
    public void CanGetTheId() {
        assertThat(MOVIE.getId(), is(1));
    }

    @Test
    public void CanGetTheDescription() {
        assertThat(MOVIE.getSummary(), is(DESCRIPTION));
    }

    @Test
    public void RightObjectString() {
        assertThat(MOVIE.toString(), is(OBJECT_STRING));
    }

    @Test
    public void CanGetTheURLThroughCorrespondingFunctions() {
        assertThat(MOVIE.getPosterUrl(), is(URL));
        assertThat(MOVIE.getImageURL(), is(URL));
        assertThat(MOVIE.getIconUrl(), is(URL));
    }
}
