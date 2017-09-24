package com.codementor.ideapool.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRequest {
    private String refreshToken;

    @JsonCreator
    public TokenRequest(@JsonProperty("refresh_token") String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}
