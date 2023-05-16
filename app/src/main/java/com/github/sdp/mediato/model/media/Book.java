package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.api.openlibrary.models.OLBook;

public class Book extends Media{
    private int year;
    private Book(){super();}

    public Book(OLBook olBook) {
        super(
                MediaType.BOOK,
                olBook.getTitle(),
                "Loading Description ...",
                "https://covers.openlibrary.org/b/ID/" + olBook.getCoverI() + "-L.jpg",
                "https://covers.openlibrary.org/b/ID/" + olBook.getCoverI() + "-M.jpg",
                olBook.getKey()
        );
        this.year = olBook.getFirstPublishYear();
    }

    @Override
    public String toString() {
        return "Book title: " + getTitle() + "\n" +
                "Summary: " + getSummary();
    }
}
