package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.api.gbook.models.GoogleBook;
import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

public class Book extends Media{
    private Book(){super();}

    public Book(String title, String summary, String posterUrl, String iconUrl, String id) {
        super(MediaType.BOOK, title, summary, posterUrl, iconUrl, id);
    }
    public Book(GoogleBook book){
        this(book.getTitle(), book.getOverview(),
                book.getPosterURL(), book.getIconURL(), book.getId());
    }

    @Override
    public String toString() {
        return "Book title: " + getTitle() + "\n" +
                "Summary: " + getSummary();
    }
}
