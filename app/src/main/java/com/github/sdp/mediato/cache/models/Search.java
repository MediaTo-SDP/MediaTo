package com.github.sdp.mediato.cache.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.github.sdp.mediato.model.media.MediaType;

@Entity
public class Search {
    @PrimaryKey(autoGenerate = true)
    private long searchId;
    private MediaType type;
    private String searchTerm;
}
