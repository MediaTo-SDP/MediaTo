package com.github.sdp.mediato.cache;

import com.github.sdp.mediato.cache.dao.MediaDao;
import com.github.sdp.mediato.model.media.Book;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;
import com.github.sdp.mediato.model.media.Movie;


import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class MediaCache {
    private final MediaDao dao;
    public MediaCache(MediaDao dao){
        this.dao = dao;
    }

    public List<Book> searchBooks(String searchTerm) {
        return getSearchedResults(searchTerm, MediaType.BOOK).stream()
                .map(cachedMedia ->
                    new Book(cachedMedia.getTitle(), cachedMedia.getSummary(), cachedMedia.getPosterUrl(),
                         cachedMedia.getIconUrl(), cachedMedia.getId()))
                .collect(Collectors.toList());
    }

    public List<Movie> searchMovies(String searchTerm){
        return getSearchedResults(searchTerm, MediaType.MOVIE).stream()
                .map(cachedMedia ->
                     new Movie(cachedMedia.getTitle(), cachedMedia.getSummary(), cachedMedia.getPosterUrl(),
                             cachedMedia.getIconUrl(), cachedMedia.getId()))
                .collect(Collectors.toList());
    }



    private List<Media> getSearchedResults(String searchTerm, MediaType type) {
        List<Media> res = dao.searchInTitle(type, searchTerm);
        res.addAll(dao.searchInSummary(type, searchTerm));
        return res;
    }
}
