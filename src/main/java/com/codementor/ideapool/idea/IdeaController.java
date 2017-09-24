package com.codementor.ideapool.idea;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codementor.ideapool.entity.Idea;
import com.codementor.ideapool.entity.User;
import com.codementor.ideapool.user.UserService;
import com.google.common.base.Strings;

@RestController
public class IdeaController {

    private final static Logger logger = LoggerFactory.getLogger(IdeaController.class);

    @Autowired
    private IdeaService ideaService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/ideas", method = RequestMethod.GET)
    public @ResponseBody List<Idea> getIdeas(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page) throws IOException {
        if (page <= 0) {
            throw new IllegalArgumentException("param page cannot be negative");
        }

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (Strings.isNullOrEmpty(email)) {
            logger.error("unknown error: cannot find authenticated principal");
            throw new AuthenticationServiceException("unknown error: cannot find authenticated principal");
        }

        User user = userService.getByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        List<Idea> result = ideaService.getIdeasByUserId(user.getId(), page - 1).orNull();

        if (result != null) {
            return result;
        } else {
            throw new EntityNotFoundException("cannot find any ideas by email: " + email);
        }
    }

    @RequestMapping(value = "/ideas", method = RequestMethod.POST)
    public @ResponseBody Idea createIdea(@RequestBody Idea idea, HttpServletResponse response) throws IOException {
        if (idea == null)
            throw new IllegalArgumentException("no idea data provided");
        if (Strings.isNullOrEmpty(idea.getContent()))
            throw new IllegalArgumentException("idea.content not provided");
        if (idea.getImpact() < 0 || idea.getImpact() > 10)
            throw new IllegalArgumentException("idea.impact must between 1 and 10");
        if (idea.getEase() < 0 || idea.getEase() > 10)
            throw new IllegalArgumentException("idea.ease must between 1 and 10");
        if (idea.getConfidence() < 0 || idea.getConfidence() > 10)
            throw new IllegalArgumentException("idea.confidence must between 1 and 10");

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (Strings.isNullOrEmpty(email)) {
            logger.error("unknown error: cannot find authenticated principal");
            throw new AuthenticationServiceException("unknown error: cannot find authenticated principal");
        }

        User user = userService.getByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        idea.setUser(user);
        Idea saved = this.ideaService.save(idea);
        return saved;
    }

    @RequestMapping(value = "/ideas/{ideaId}", method = RequestMethod.DELETE)
    public void deleteIdea(@PathVariable(value = "ideaId", required = true) String ideaId) {
        if (Strings.isNullOrEmpty(ideaId)) {
            throw new IllegalArgumentException("path param [ideaId] must be provided");
        }

        Idea idea = ideaService.getIdeaById(ideaId).orNull();
        if (idea == null) {
            throw new EntityNotFoundException("cannot find idea by id: " + ideaId);
        }

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (email != null && email.equals(idea.getUser().getEmail())) {
            ideaService.delete(idea);
        } else {
            throw new AuthorizationServiceException("cannot delete idea with user: " + email);
        }
    }

    @RequestMapping(value = "/ideas/{ideaId}", method = RequestMethod.PUT)
    public @ResponseBody Idea updateIdea(@PathVariable(value = "ideaId", required = true) String ideaId,
            @RequestBody Idea idea) {
        if (Strings.isNullOrEmpty(ideaId)) {
            throw new IllegalArgumentException("path param [ideaId] must be provided");
        }
        if (idea == null)
            throw new IllegalArgumentException("no idea data provided");
        if (Strings.isNullOrEmpty(idea.getContent()))
            throw new IllegalArgumentException("idea.content not provided");
        if (idea.getImpact() < 0 || idea.getImpact() > 10)
            throw new IllegalArgumentException("idea.impact must between 1 and 10");
        if (idea.getEase() < 0 || idea.getEase() > 10)
            throw new IllegalArgumentException("idea.ease must between 1 and 10");
        if (idea.getConfidence() < 0 || idea.getConfidence() > 10)
            throw new IllegalArgumentException("idea.confidence must between 1 and 10");

        Idea oldIdea = ideaService.getIdeaById(ideaId).orNull();
        if (oldIdea == null) {
            throw new EntityNotFoundException("cannot find idea by id: " + ideaId);
        }

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (email != null && email.equals(oldIdea.getUser().getEmail())) {
            oldIdea.setContent(idea.getContent());
            oldIdea.setImpact(idea.getImpact());
            oldIdea.setEase(idea.getEase());
            oldIdea.setConfidence(idea.getConfidence());
            return ideaService.save(oldIdea);
        } else {
            throw new AuthorizationServiceException("cannot delete idea with user: " + email);
        }

    }

}
