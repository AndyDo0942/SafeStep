package com.team.GroundTruth.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public static final String ERROR_MESSAGE = "User with ID '%S' does not exist";

    private final UUID id;

    public UserNotFoundException(UUID id) {
        super(String.format(ERROR_MESSAGE, id));
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
