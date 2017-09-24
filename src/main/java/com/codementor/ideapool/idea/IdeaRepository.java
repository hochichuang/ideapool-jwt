package com.codementor.ideapool.idea;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.codementor.ideapool.entity.Idea;
import com.google.common.base.Optional;

public interface IdeaRepository extends PagingAndSortingRepository<Idea, String> {

    @Query("select i from Idea i where i.id = ?1")
    public Optional<Idea> findIdeaById(String id);

    @Query("select i from Idea i where i.user.id = ?1")
    public Optional<List<Idea>> findIdeasByUserId(String userId, Pageable pageable);

}
