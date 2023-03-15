package com.github.sdp.mediato.model;

import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.media.Collection;

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
    private List<User> friends = new ArrayList<>();
    private Map<String, Collection> collections = new HashMap<>();
    private User(){}
    private User(UserBuilder builder){
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

    public List<User> getFriends() {
        return friends;
    }

    public Map<String, Collection> getCollections() {
        return collections;
    }

    public static class UserBuilder {
        private String id = "";
        private String username = "";
        private String email = "";
        private String registerDate = "";

        private Location location = new Location();
        private List<User> friends = new ArrayList<>();
        private Map<String, Collection> collections = new HashMap<>();

        public UserBuilder(String id){
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

        public User build(){
            User user = new User(this);
            //Check mandatory fields are here
            Preconditions.checkUser(user);
            return user;
        }

    }

}
