package com.github.sdp.mediato.cache;

import com.github.sdp.mediato.cache.dao.MediaDao;
import com.github.sdp.mediato.cache.models.CachedMedia;
import com.github.sdp.mediato.model.media.Book;
import com.github.sdp.mediato.model.media.MediaType;


import java.util.List;
import java.util.stream.Collectors;

public final class MediaCache {
    private MediaCache(){}

    public static List<Book> getSearchedBooks(String searchTerm, MediaDao mediaDao) {
        return getSearchedResults(searchTerm, MediaType.BOOK, mediaDao).stream()
                .map(cachedMedia ->
                new Book(cachedMedia.getTitle(), cachedMedia.getSummary(), cachedMedia.getIconUrl(),
                         cachedMedia.getPosterUrl(), cachedMedia.getId()))
                .collect(Collectors.toList());
    }



    public static List<CachedMedia> getSearchedResults(String searchTerm, MediaType type, MediaDao mediaDao) {
        return mediaDao.getAllMedia();
    }
}
