package dov.projects.restfulskeleton.controllers.errorhandling;

public enum ErrorCode {
    UNSPECIFIED_GENERIC_EXCEPTION(1),
    JPA_EXCEPTION(2),
    USER_EXISTS(3),
    USER_NOT_FOUND(4);

    public int getValue() {
        return value;
    }

    private final int value;

    ErrorCode(int value) {
        this.value = value;
    }
}
