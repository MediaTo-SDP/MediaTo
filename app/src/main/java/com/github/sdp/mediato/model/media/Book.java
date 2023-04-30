package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.api.gbook.models.GoogleBook;

public class Book extends Media{

    public Book(String title, String summary, String posterUrl, String iconUrl, String id) {
        super(MediaType.BOOK, title, summary, posterUrl, iconUrl, id);
    }

    public Book(GoogleBook book){
        this(book.getTitle(), book.getOverview() == null ? " " : book.getOverview(),
                book.getPosterURL(), book.getIconURL(), book.getId());
    }

    @Override
    public String toString() {
        return "Book title: " + getTitle() + "\n" +
                "Summary: " + getSummary();
    }
}
