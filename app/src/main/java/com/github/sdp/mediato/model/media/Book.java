package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.api.gbook.models.GoogleBook;
import com.github.sdp.mediato.api.openlibrary.models.OLSearchBook;
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
                "Loading Description ...",
                "https://covers.openlibrary.org/b/ID/" + olTrendingBook.getCoverI() + "-L.jpg",
                "https://covers.openlibrary.org/b/ID/" + olTrendingBook.getCoverI() + "-M.jpg",
                olTrendingBook.getKey()
        );
        this.year = olTrendingBook.getFirstPublishYear();
    }

    public Book(OLSearchBook olSearchBook) {
        super(
                MediaType.BOOK,
                olSearchBook.getTitle(),
                "Loading Description ...",
                "https://covers.openlibrary.org/b/ID/" + olSearchBook.getCoverId() + "-L.jpg",
                "https://covers.openlibrary.org/b/ID/" + olSearchBook.getCoverId() + "-M.jpg",
                olSearchBook.getKey()
        );
    }

    @Override
    public String toString() {
        return "Book title: " + getTitle() + "\n" +
                "Summary: " + getSummary();
    }
}
