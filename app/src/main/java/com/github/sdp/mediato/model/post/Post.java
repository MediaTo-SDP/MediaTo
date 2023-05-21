package com.github.sdp.mediato.model.post;

/**
 * Abstract class representing a post
 */
public abstract class Post {
    private PostType postType;
    private String username;

    private Post() {}

    public Post(PostType postType, String username){
        this.postType = postType;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
