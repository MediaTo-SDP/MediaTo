package com.github.sdp.mediato.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.github.sdp.mediato.api.gbook.GbookApiTest;
import com.github.sdp.mediato.model.media.Book;
import com.github.sdp.mediato.model.media.MediaType;

import org.junit.Test;

import java.io.IOException;

public class BookTest {
    private static final String ID = "CUSTOKnpwqweoQU2I98231";
    private static final String DESCRIPTION = "This is a sample description.";
    private static final String URL1 = "Sample url1";
    private static final String URL2 = "Sample url2";
    private static final String TITLE = "Sample title";
    private static final String OBJECT_STRING = "Book title: " + TITLE + "\nSummary: " + DESCRIPTION;
    private static final Book BOOK = new Book(TITLE, DESCRIPTION, URL1, URL2, ID);
    private final GbookApiTest db = new GbookApiTest();



    @Test
    public void MediaTypeReturnsMovie() {
        assertThat(BOOK.getMediaType(), is(MediaType.BOOK));
    }

    @Test
    public void CanGetTheTitle() {
        assertThat(BOOK.getTitle(), is(TITLE));
    }

    @Test
    public void CanGetTheId() {
        assertThat(BOOK.getId(), is(ID));
    }


    @Test
    public void CanGetTheDescription() {
        assertThat(BOOK.getSummary(), is(DESCRIPTION));
    }

    @Test
    public void RightObjectString() {
        assertThat(BOOK.toString(), is(OBJECT_STRING));
    }

    @Test
    public void CanGetTheURLThroughCorrespondingFunctions() {
        assertThat(BOOK.getPosterUrl(), is(URL1));
        assertThat(BOOK.getImageURL(), is(URL1));
        assertThat(BOOK.getIconUrl(), is(URL2));
    }

    @Test
    public void isEqualWorks() throws IOException {
        Book book = new Book(db.getBooks().get(0));
        assertThat(book.isTheSame(book), is(true));
        assertThat(book.isTheSame(BOOK), is(false));

    }


    @Test
    public void GBookConstructorWorks() throws IOException {
        Book book = new Book(db.getBooks().get(0));
        assertThat(book.getId(), is("LanWAAAAMAAJ"));
    }
}
