package com.codementor.ideapool.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "db_user")
public class User {
    @Id
    @Column(name = "id")
    @GenericGenerator(name = "random_string_id", strategy = "com.codementor.ideapool.utils.RandomStringIdGenerator")
    @GeneratedValue(generator = "random_string_id")
    private String id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "refresh_token")
    private String refreshToken;

    @OneToMany
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private List<Idea> ideas;

    public User() {
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public List<Idea> getIdeas() {
        return ideas;
    }

}
