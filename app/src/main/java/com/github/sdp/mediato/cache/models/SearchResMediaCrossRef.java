package com.github.sdp.mediato.cache.models;

import androidx.room.Entity;

@Entity(primaryKeys = {"rowId", "searchId"})
public class SearchResMediaCrossRef {
    private long rowId;
    private long searchId;
}
