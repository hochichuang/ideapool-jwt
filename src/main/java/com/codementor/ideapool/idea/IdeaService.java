package com.codementor.ideapool.idea;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codementor.ideapool.entity.Idea;
import com.google.common.base.Optional;

@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;

    @Autowired
    public IdeaService(IdeaRepository ideaRepository) {
        this.ideaRepository = ideaRepository;
    }

    public Optional<Idea> getIdeaById(String id) {
        return ideaRepository.findIdeaById(id);
    }

    public Optional<List<Idea>> getIdeasByUserId(String userId) {
        return ideaRepository.findIdeasByUserId(userId);
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
