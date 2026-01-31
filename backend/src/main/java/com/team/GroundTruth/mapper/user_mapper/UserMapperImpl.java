package com.team.GroundTruth.mapper.user_mapper;

import com.team.GroundTruth.domain.UserRequest;
import com.team.GroundTruth.domain.dto.user.UserRequestDto;
import com.team.GroundTruth.domain.dto.user.UserDto;
import com.team.GroundTruth.domain.entity.User.User;
import org.springframework.stereotype.Component;

/**
 * Spring component that implements user DTO mapping.
 */
@Component
public class UserMapperImpl implements UserMapper {
    /**
     * {@inheritDoc}
     */
    @Override
    public UserRequest fromDto(UserRequestDto dto) {
        return new UserRequest(dto.username());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto toDto(User user) {
        return new UserDto(user.getId() ,user.getUsername(), user.getReports());
    }
}
