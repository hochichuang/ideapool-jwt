package com.codementor.ideapool.model;

public class JwtToken {
    private final String token;

    protected JwtToken(final String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

}
