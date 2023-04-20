package com.github.sdp.mediato.cache;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.github.sdp.mediato.cache.dao.MediaDao;
import com.github.sdp.mediato.cache.dao.UserDao;
import com.github.sdp.mediato.cache.models.CachedMedia;
import com.github.sdp.mediato.cache.models.SearchResMediaCrossRef;
import com.github.sdp.mediato.cache.models.SearchResults;
import com.github.sdp.mediato.model.User;

@Database(entities = {CachedMedia.class, User.class, SearchResults.class, SearchResMediaCrossRef.class}, version = 1)
public abstract class AppCache extends RoomDatabase {
    public abstract MediaDao mediaDao();
    public abstract UserDao userDAO();
}
