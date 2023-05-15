package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.api.gbook.models.GoogleBook;
import com.github.sdp.mediato.api.openlibrary.models.OLTrendingBook;
import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

import java.util.List;

public class Book extends Media{
    private int year;
    private Book(){super();}

    public Book(OLTrendingBook olTrendingBook) {
        super(
                MediaType.BOOK,
                olTrendingBook.getTitle(),
                "None",
                "https://covers.openlibrary.org/b/ID/" + olTrendingBook.getCoverI() + "-L.jpg",
                "https://covers.openlibrary.org/b/ID/" + olTrendingBook.getCoverI() + "-M.jpg",
                olTrendingBook.getKey()
        );
        this.year = olTrendingBook.getFirstPublishYear();
    }

    @Override
    public String toString() {
        return "Book title: " + getTitle() + "\n" +
                "Summary: " + getSummary();
    }
}
