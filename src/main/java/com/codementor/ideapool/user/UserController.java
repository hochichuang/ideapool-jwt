package com.codementor.ideapool.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codementor.ideapool.entity.User;
import com.codementor.ideapool.model.JwtToken;
import com.codementor.ideapool.model.JwtTokenFactory;
import com.codementor.ideapool.model.TokenRequest;
import com.codementor.ideapool.model.UserMe;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JwtTokenFactory tokenFactory;

    @Autowired
    private ObjectMapper mapper;

    @RequestMapping(value = "/access-tokens/refresh", method = RequestMethod.POST)
    public void refreshAccessToken(@RequestBody TokenRequest token, HttpServletResponse response) throws IOException {
        if (token == null)
            throw new IllegalArgumentException("no token data provided");
        if (Strings.isNullOrEmpty(token.getRefreshToken()))
            throw new IllegalArgumentException("refresh_token not provided");

        User user = userService.getByRefreshToken(token.getRefreshToken())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + token.getRefreshToken()));

        JwtToken accessToken = tokenFactory.createAccessJwtToken(user.getEmail());

        Map<String, String> tokenMap = new HashMap<String, String>();
        tokenMap.put("jwt", accessToken.getToken());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        mapper.writeValue(response.getWriter(), tokenMap);
    }

    @RequestMapping(value = "/access-tokens", method = RequestMethod.DELETE)
    public void deleteRefreshToken(@RequestBody TokenRequest token) {
        if (token == null)
            throw new IllegalArgumentException("no token data provided");
        if (Strings.isNullOrEmpty(token.getRefreshToken()))
            throw new IllegalArgumentException("refresh_token not provided");

        User user = userService.getByRefreshToken(token.getRefreshToken())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + token.getRefreshToken()));
        user.setRefreshToken("");
        userService.save(user);
    }

    @RequestMapping(value = "/me")
    public UserMe getMe(HttpServletRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (Strings.isNullOrEmpty(email)) {
            logger.error("unknown error: cannot find authenticated principal");
            throw new AuthenticationServiceException("unknown error: cannot find authenticated principal");
        }

        User user = userService.getByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new UserMe(user.getEmail(), user.getName(), user.getAvatarUrl());
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public void signupUser(@RequestBody User user, HttpServletResponse response) throws IOException {
        if (user == null)
            throw new IllegalArgumentException("no user data provided");
        if (Strings.isNullOrEmpty(user.getEmail()))
            throw new IllegalArgumentException("user.email not provided");
        if (Strings.isNullOrEmpty(user.getName()))
            throw new IllegalArgumentException("user.name not provided");
        if (Strings.isNullOrEmpty(user.getPassword()))
            throw new IllegalArgumentException("user.password not provided");

        user.setPassword(encoder.encode(user.getPassword()));
        this.userService.save(user);

        JwtToken accessToken = tokenFactory.createAccessJwtToken(user.getEmail());
        JwtToken refreshToken = tokenFactory.createRefreshToken(user.getEmail());

        Map<String, String> tokenMap = new HashMap<String, String>();
        tokenMap.put("jwt", accessToken.getToken());
        tokenMap.put("refresh_token", refreshToken.getToken());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        mapper.writeValue(response.getWriter(), tokenMap);
    }

}
