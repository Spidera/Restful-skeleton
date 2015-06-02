package dov.projects.restfulskeleton.services.errorhandling.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("Could not find user '" + userId + "'");
    }
}

