package com.codementor.ideapool.model;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.codementor.ideapool.config.JwtSettings;
import com.codementor.ideapool.entity.User;
import com.codementor.ideapool.user.UserService;
import com.codementor.ideapool.utils.Utils;
import com.google.common.base.Strings;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenFactory {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFactory.class);

    private final JwtSettings settings;

    private final UserService userService;

    @Autowired
    public JwtTokenFactory(JwtSettings settings, UserService userService) {
        this.settings = settings;
        this.userService = userService;
    }

    /**
     * Factory method for issuing new JWT Tokens.
     * 
     * @param email
     * @return
     */
    public JwtToken createAccessJwtToken(String email) {
        if (Strings.isNullOrEmpty(email)) {
            throw new IllegalArgumentException("Cannot create JWT Token without email");
        }

        User user = userService.getByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("id", String.valueOf(user.getId()));
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());

        LocalDateTime currentTime = LocalDateTime.now();

        Key key = null;
        try {
            ClassPathResource resource = new ClassPathResource("server.jks");
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(resource.getInputStream(), "keystorepass".toCharArray());

            key = keystore.getKey("jwtkey", "keypass".toCharArray());
        } catch (KeyStoreException | UnrecoverableKeyException | CertificateException | NoSuchAlgorithmException
                | IOException e) {
            logger.warn("Unknown keystore exception: " + e.getMessage());
            throw new AuthenticationServiceException("Unknown keystore exception", e);
        }

        // @formatter:off
        String token = Jwts.builder()
          .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
          .setClaims(claims)
          .setExpiration(Date.from(currentTime
              .plusMinutes(settings.getTokenExpirationTime())
              .atZone(ZoneId.systemDefault()).toInstant()))
          .signWith(SignatureAlgorithm.RS256, key)
        .compact();
        // @formatter:on

        return new JwtToken(token);
    }

    public JwtToken createRefreshToken(String email) {
        if (Strings.isNullOrEmpty(email)) {
            throw new IllegalArgumentException("Cannot create JWT Token without email");
        }

        String token = Utils.generateRefreshToken();

        return new JwtToken(token);
    }

}
