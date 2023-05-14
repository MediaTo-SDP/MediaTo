package com.github.sdp.mediato.model;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.media.Collection;
import com.github.sdp.mediato.model.post.Post;
import com.github.sdp.mediato.model.post.ReviewPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String id = "";
    private String username = "";
    private String email = "";
    private String registerDate = "";

    private Location location = new Location();
    private final Map<String, Boolean> followers = new HashMap<>();
    private final Map<String, Boolean> following = new HashMap<>();

    private final Map<String, Collection> collections = new HashMap<>();

    private User() {
    }

    private User(UserBuilder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.registerDate = builder.registerDate;
        this.location = builder.location;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * Returns a list of the user's followers' usernames by adapting the map attribute
     */
    public List<String> getFollowers() {
        return followers.entrySet().stream()
                .filter(Map.Entry::getValue).map(Map.Entry::getKey)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    }

    /**
     * Returns a list of the usernames of the users the user is following by adapting the map
     * attribute
     */
    public List<String> getFollowing() {
        return following.entrySet().stream()
                .filter(Map.Entry::getValue).map(Map.Entry::getKey)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    }

    public int getFollowingCount() {
        return getFollowing().size();
    }

    public int getFollowersCount() {
        return getFollowers().size();
    }

    public Map<String, Collection> getCollections() {
        return collections;
    }

    /**
     * Adds a collection to the user's collections
     * @important: the collection is only added locally, it is not added to the database
     * @param collection
     */
    public void addCollection(Collection collection) {
        this.collections.put(collection.getCollectionName(), collection);
    }

    /**
     * Creates a review post for each review in the user's collections
     * @return a list of review posts
     */
    public List<ReviewPost> fetchReviewPosts() {
        List<ReviewPost> reviewPosts = new ArrayList<>();
        for(Collection collection : collections.values()) {
            collection.getReviews().values().forEach(
                    review -> {
                        reviewPosts.add(new ReviewPost(getUsername(), review, collection));
                    });
        }
        return reviewPosts;
    }

    public static class UserBuilder {

        private String id = "";
        private String username = "";
        private String email = "";
        private String registerDate = "";

        private Location location = new Location();
        private final Map<String, Boolean> followers = new HashMap<>();
        private final Map<String, Boolean> following = new HashMap<>();

        private final Map<String, Collection> collections = new HashMap<>();

        public UserBuilder(String id) {
            Preconditions.checkUID(id);
            this.id = id;
        }

        public UserBuilder setUsername(String username) {
            Preconditions.checkUsername(username);
            this.username = username;
            return this;
        }

        public UserBuilder setEmail(String email) {
            Preconditions.checkEmail(email);
            this.email = email;
            return this;
        }

        public UserBuilder setRegisterDate(String registerDate) {
            Preconditions.checkRegisterDate(registerDate);
            this.registerDate = registerDate;
            return this;
        }

        public UserBuilder setLocation(Location location) {
            Preconditions.checkLocation(location);
            this.location = location;
            return this;
        }

        public User build() {
            User user = new User(this);
            //Check mandatory fields are here
            Preconditions.checkUser(user);
            return user;
        }

    }

}
