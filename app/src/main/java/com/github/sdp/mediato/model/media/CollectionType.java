package com.github.sdp.mediato.model.media;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public enum CollectionType {

    FAVOURITES, RECENTLY_WATCHED, CUSTOM;

    @NonNull
    @Contract(pure = true)
    @Override
    public String toString(){
        switch(this){
            case FAVOURITES:
                return "Favourites";
            case RECENTLY_WATCHED:
                return "Recently Watched";
            case CUSTOM:
                return "Custom";
            default:
                return "Collection Type does not exist";
        }
    }
    }
