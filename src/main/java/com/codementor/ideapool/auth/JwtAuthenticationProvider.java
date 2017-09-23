package com.codementor.ideapool.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.codementor.ideapool.model.RawJwtToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

    public JwtAuthenticationProvider() {
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RawJwtToken rawAccessToken = (RawJwtToken) authentication.getCredentials();

        Jws<Claims> jwsClaims = rawAccessToken.parseClaims();
        String email = (String) jwsClaims.getBody().get("email");

        return new JwtAuthenticationToken(email, LoginAuthenticationProvider.DEFAULT_AUTHORITIES);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
