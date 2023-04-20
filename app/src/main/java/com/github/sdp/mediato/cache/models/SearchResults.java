package com.github.sdp.mediato.cache.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class SearchResults {
    @Embedded
    private Search search;
    @Relation(
            parentColumn = "searchId",
            entityColumn = "rowId",
            associateBy = @Junction(SearchResMediaCrossRef.class)
    )
    public List<CachedMedia> medias;
}


