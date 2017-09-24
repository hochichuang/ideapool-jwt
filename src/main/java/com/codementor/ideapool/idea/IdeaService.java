package com.codementor.ideapool.idea;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.codementor.ideapool.entity.Idea;
import com.google.common.base.Optional;

@Service
public class IdeaService {
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final IdeaRepository ideaRepository;

    @Autowired
    public IdeaService(IdeaRepository ideaRepository) {
        this.ideaRepository = ideaRepository;
    }

    public Optional<Idea> getIdeaById(String id) {
        return ideaRepository.findIdeaById(id);
    }

    public Optional<List<Idea>> getIdeasByUserId(String userId, int page) {
        PageRequest request = new PageRequest(page, DEFAULT_PAGE_SIZE, Sort.Direction.DESC, "averageScore");
        return ideaRepository.findIdeasByUserId(userId, request);
    }

    @Transactional
    public Idea save(Idea idea) {
        return ideaRepository.save(idea);
    }

    @Transactional
    public void delete(Idea idea) {
        ideaRepository.delete(idea);
    }
}
