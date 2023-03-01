package com.github.sdp.apibootcamp.Storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StoredActivity.class}, version = 1)
public abstract class ActivityDatabase extends RoomDatabase {
    public abstract UserDAO getDAO();
}
