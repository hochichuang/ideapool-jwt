package com.codementor.ideapool.exception;

import org.springframework.security.core.AuthenticationException;

import com.codementor.ideapool.model.JwtToken;

public class JwtExpiredTokenException extends AuthenticationException {

    private static final long serialVersionUID = -2965794957298410907L;

    private JwtToken token;

    public JwtExpiredTokenException(String msg) {
        super(msg);
    }

    public JwtExpiredTokenException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }

}
