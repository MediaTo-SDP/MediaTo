package com.github.sdp.apibootcamp.Storage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StoredActivity {
    @PrimaryKey
    public int uid;
    private static int currID = 0;
    @ColumnInfo(name = "text")
    public String activity;

    public StoredActivity(String activity){
        this.activity = activity;
        uid = currID++;
    }
}
