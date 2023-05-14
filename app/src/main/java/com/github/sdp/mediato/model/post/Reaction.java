package com.github.sdp.mediato.model.post;

public enum Reaction {
    LIKE, DISLIKE;

    @Override
    public String toString() {
        switch (this) {
            case LIKE:
                return "likes";
            case DISLIKE:
                return "dislikes";
            default:
                throw new IllegalArgumentException("Reaction must be either likes or dislikes");
        }
    }
}


