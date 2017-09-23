package com.codementor.ideapool.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserMe {

    private String email;

    private String name;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    public UserMe(String email, String name, String avatarUrl) {
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

}
