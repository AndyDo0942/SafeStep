package com.team.GroundTruth.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

/**
 * DTO used to create a new user.
 *
 * @param username requested username
 */
public record UserRequestDto(
        @NotBlank(message = ERROR_MESSAGE_TITLE_LENGTH)
        @Length(max = 255, message = ERROR_MESSAGE_TITLE_LENGTH)
        String username
) {
    private static final String ERROR_MESSAGE_TITLE_LENGTH = "Invalid username";
}
