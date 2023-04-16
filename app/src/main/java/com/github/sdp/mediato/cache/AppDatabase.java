package com.github.sdp.mediato.cache;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.github.sdp.mediato.cache.dao.MediaDao;
import com.github.sdp.mediato.cache.dao.UserDao;
import com.github.sdp.mediato.model.User;
import com.github.sdp.mediato.model.media.Media;

@Database(entities = {Media.class, User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MediaDao mediaDao();
    public abstract UserDao userDAO();
}
