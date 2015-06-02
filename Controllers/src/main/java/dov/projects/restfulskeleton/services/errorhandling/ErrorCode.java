package dov.projects.restfulskeleton.services.errorhandling;

public enum ErrorCode {
    UNSPECIFIED_GENERIC_EXCEPTION(1),
    UNSPECIFIED_JPA_EXCEPTION(2),
    DATA_TOO_LONG_FOR_PASSWORD(3),
    USER_NOT_FOUND(4);

    public int getValue() {
        return value;
    }

    private final int value;

    ErrorCode(int value) {
        this.value = value;
    }
}
