package com.codementor.ideapool.model;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;

import com.codementor.ideapool.exception.JwtExpiredTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class RawJwtToken extends JwtToken {
    private static Logger logger = LoggerFactory.getLogger(RawJwtToken.class);

    public RawJwtToken(String token) {
        super(token);
    }

    /**
     * Parses and validates JWT Token signature.
     * 
     * @throws BadCredentialsException
     * @throws JwtExpiredTokenException
     * 
     */
    public Jws<Claims> parseClaims() {
        try {
            ClassPathResource resource = new ClassPathResource("server.jks");
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(resource.getInputStream(), "keystorepass".toCharArray());

            Key key = keystore.getKey("jwtkey", "keypass".toCharArray());

            return Jwts.parser().setSigningKey(key).parseClaimsJws(this.getToken());
        } catch (KeyStoreException | UnrecoverableKeyException | CertificateException | NoSuchAlgorithmException
                | IOException e) {
            logger.warn("Unknown keystore exception: " + e.getMessage());
            throw new AuthenticationServiceException("Unknown keystore exception", e);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            logger.error("Invalid JWT Token", ex);
            throw new BadCredentialsException("Invalid JWT token: ", ex);
        } catch (ExpiredJwtException expiredEx) {
            logger.info("JWT Token is expired", expiredEx);
            throw new JwtExpiredTokenException(this, "JWT Token expired", expiredEx);
        }
    }

    public String getToken() {
        return super.getToken();
    }

}
