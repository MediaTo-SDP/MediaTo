package com.github.sdp.mediato.cache;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.github.sdp.mediato.cache.dao.MediaDao;
import com.github.sdp.mediato.cache.dao.UserDao;
import com.github.sdp.mediato.cache.models.CachedMedia;
import com.github.sdp.mediato.model.User;

import java.io.Serializable;

@Database(entities = {CachedMedia.class}, version = 1)
public abstract class AppCache extends RoomDatabase implements Serializable {
    public abstract MediaDao mediaDao();
    public abstract UserDao userDAO();
}
