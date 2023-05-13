package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.api.gbook.models.GoogleBook;
import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

import java.util.List;

public class Book extends Media{
    private Book(){super();}

    private List<String> subjects;

    public Book(String id, String title, String summary, int coverId, List<String> subjects) {
        super(MediaType.BOOK, title, summary, "https://covers.openlibrary.org/b/ID/" + coverId + "-M.jpg", "https://covers.openlibrary.org/b/ID/" + coverId + "-S.jpg", id);
        this.subjects = subjects;
    }

    @Override
    public String toString() {
        return "Book title: " + getTitle() + "\n" +
                "Summary: " + getSummary();
    }
}
