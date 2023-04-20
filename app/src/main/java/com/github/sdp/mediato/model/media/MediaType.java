package com.github.sdp.mediato.model.media;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@TypeConverters
public enum MediaType {
    BOOK,
    MOVIE,
    SERIES;
    @TypeConverter
    public static int fromMediaType(@NonNull MediaType type) {
        return type.ordinal();
    }

    @TypeConverter
    public static MediaType toMediaType(int ordinal) {
        return MediaType.values()[ordinal];
    }

    }

