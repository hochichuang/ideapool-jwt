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

    @OneToMany
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private List<Idea> ideas;

    public User() {
    }

    public User(String id, String email, String name, String password, String avatarUrl) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.avatarUrl = avatarUrl;
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

    public List<Idea> getIdeas() {
        return ideas;
    }

}
