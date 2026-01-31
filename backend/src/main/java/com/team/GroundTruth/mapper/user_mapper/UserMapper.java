package com.team.GroundTruth.mapper.user_mapper;

import com.team.GroundTruth.domain.UserRequest;
import com.team.GroundTruth.domain.dto.user.UserRequestDto;
import com.team.GroundTruth.domain.dto.user.UserDto;
import com.team.GroundTruth.domain.entity.User.User;

/**
 * Maps user-related DTOs to domain models and back.
 */
public interface UserMapper {
    /**
     * Converts the incoming create-user DTO into a domain request.
     *
     * @param dto incoming request DTO
     * @return domain request
     */
    UserRequest fromDto(UserRequestDto dto);

    /**
     * Converts a domain user to an outbound DTO.
     *
     * @param user domain user
     * @return user DTO
     */
    UserDto toDto(User user);
}
