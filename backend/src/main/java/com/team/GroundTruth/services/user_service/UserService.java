package com.team.GroundTruth.services.user_service;

import com.team.GroundTruth.domain.UserRequest;
import com.team.GroundTruth.domain.entity.User.User;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for user operations.
 */
public interface UserService {
    /**
     * Creates and persists a new user.
     *
     * @param userRequest request containing user details
     * @return created user
     */
    User createUser(UserRequest userRequest);
    /**
     * Returns a list of all users in the database
     *
     * @return List of users
     */
    List<User> getUsers();

    /**
     * Updates a user by id with the supplied data.
     *
     * @param UserId user identifier
     * @param updateUserRequest updated user data
     * @return updated user
     */
    User updateUser(UUID UserId, UserRequest updateUserRequest);

    /**
     * Deletes a user by id.
     *
     * @param UserId user identifier
     */
    void deleteUser(UUID UserId);

}
