package com.github.sdp.apibootcamp.Storage;

import androidx.room.Dao;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM storedactivity")
    List<StoredActivity> getStoredActivities();
    @Insert
    void insert(StoredActivity... storedActivities);
}
