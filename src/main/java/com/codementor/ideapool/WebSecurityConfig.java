package com.codementor.ideapool;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.codementor.ideapool.auth.JwtAuthenticationProvider;
import com.codementor.ideapool.auth.JwtTokenAuthenticationProcessingFilter;
import com.codementor.ideapool.auth.LoginAuthenticationProvider;
import com.codementor.ideapool.auth.LoginProcessingFilter;
import com.codementor.ideapool.auth.RestAuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String JWT_TOKEN_HEADER_PARAM = "x-access-token";
    public static final String FORM_BASED_LOGIN_ENTRY_POINT = "/access-tokens";
    public static final String TOKEN_REFRESH_ENTRY_POINT = "/access-tokens/refresh";

    public static final String USER_ENTRY_POINT = "/users";
    public static final String ME_ENTRY_POINT = "/me";
    public static final String IDEAS_ENTRY_POINT = "/ideas/**";

    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    private AuthenticationFailureHandler failureHandler;

    @Autowired
    private LoginAuthenticationProvider loginAuthenticationProvider;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    protected LoginProcessingFilter buildLoginProcessingFilter() throws Exception {
        LoginProcessingFilter filter = new LoginProcessingFilter(
                new AntPathRequestMatcher(FORM_BASED_LOGIN_ENTRY_POINT, HttpMethod.POST.name()), successHandler,
                failureHandler, objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter() throws Exception {
        List<RequestMatcher> matcherList = Arrays.asList(
                new AntPathRequestMatcher(FORM_BASED_LOGIN_ENTRY_POINT, HttpMethod.DELETE.name()),
                new AntPathRequestMatcher(ME_ENTRY_POINT), new AntPathRequestMatcher(IDEAS_ENTRY_POINT));
        JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(failureHandler,
                new OrRequestMatcher(matcherList));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    protected BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) {
        builder.authenticationProvider(loginAuthenticationProvider);
        builder.authenticationProvider(jwtAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // authentication configurations
        // @formatter:off
        http
            .csrf().disable()
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, FORM_BASED_LOGIN_ENTRY_POINT).permitAll()
                    .antMatchers(TOKEN_REFRESH_ENTRY_POINT, USER_ENTRY_POINT).permitAll()
            .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.DELETE, FORM_BASED_LOGIN_ENTRY_POINT).authenticated()
                    .antMatchers(ME_ENTRY_POINT, IDEAS_ENTRY_POINT).authenticated()
            .and()
                .addFilterBefore(buildLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
        // @formatter:on

        // response header configurations
        // @formatter:off
        http.headers()
            .defaultsDisabled()
            .frameOptions().sameOrigin()
            .xssProtection().block(true)
            .and()
            .contentTypeOptions()
            .and()
            .addHeaderWriter(new StaticHeadersWriter("Cache-Control", "max-age=0, private, must-revalidate"))
            .addHeaderWriter(new StaticHeadersWriter("Vary", "Accept-Encoding"));
        // @formatter:on

    }
}
