package com.codementor.ideapool.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.codementor.ideapool.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email = ?1")
    public Optional<User> findByEmail(String email);

    @Query("select u from User u where u.refreshToken = ?1")
    public Optional<User> findByRefreshToken(String refreshToken);

}
