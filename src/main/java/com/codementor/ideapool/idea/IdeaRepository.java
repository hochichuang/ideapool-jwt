package com.codementor.ideapool.idea;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codementor.ideapool.entity.Idea;
import com.google.common.base.Optional;

public interface IdeaRepository extends JpaRepository<Idea, String> {

    @Query("select i from Idea i where i.id = :id")
    public Optional<Idea> findIdeaById(@Param("id") String id);

    @Query("select i from Idea i where i.user.id = :userId")
    public Optional<List<Idea>> findIdeasByUserId(@Param("userId") String userId);

}
