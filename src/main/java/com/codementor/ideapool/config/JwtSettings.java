package com.codementor.ideapool.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.codementor.ideapool.model.JwtToken;

@Configuration
@ConfigurationProperties(prefix = "ideapool.security.jwt")
public class JwtSettings {
    private static final Logger logger = LoggerFactory.getLogger(JwtSettings.class);

    /**
     * {@link JwtToken} will expire after this time.
     */
    private Integer tokenExpirationTime;

    /**
     * {@link JwtToken} can be refreshed during this timeframe.
     */
    private Integer refreshTokenExpTime;

    public Integer getRefreshTokenExpTime() {
        return refreshTokenExpTime;
    }

    public void setRefreshTokenExpTime(Integer refreshTokenExpTime) {
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    public Integer getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(Integer tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    @PostConstruct
    public void init() {
        if (tokenExpirationTime == null) {
            logger.warn("tokenExpirationTime not set, set to default [10]");
            tokenExpirationTime = 10;
        }
        if (refreshTokenExpTime == null) {
            logger.warn("refreshTokenExpTime not set, set to default [43200]");
            refreshTokenExpTime = 43200;
        }
    }
}
