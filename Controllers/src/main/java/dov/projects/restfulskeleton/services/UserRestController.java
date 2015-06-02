package dov.projects.restfulskeleton.services;

import dov.projects.restfulskeleton.dao.UserRepository;
import dov.projects.restfulskeleton.model.User;
import dov.projects.restfulskeleton.services.errorhandling.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;

@RestController
@RequestMapping(value = "/users", produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserRestController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long userId) {
        validateUser(userId);
        return userRepository.findOne(userId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<User> addUser(@RequestBody User input) {
        User result = userRepository.save(input);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(result.getId()).toUri());

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@PathVariable("userId") Long userId) {
        validateUser(userId);
        userRepository.delete(userRepository.findOne(userId));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void validateUser(Long userId) {
        if (!userRepository.exists(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}