package com.team.GroundTruth.services.user_service;

import com.team.GroundTruth.domain.UserRequest;
import com.team.GroundTruth.domain.entity.User.User;
import com.team.GroundTruth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link UserService}.
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * Repository used to persist and load users.
     */
    private final UserRepository userRepository;

    /**
     * Creates the service with its repository dependency.
     *
     * @param userRepository repository for user persistence
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(UserRequest request) {
        User user = new User(
                null,
                request.username(),
                new ArrayList<>()
        );

        return this.userRepository.save(user);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getUsers() { return this.userRepository.findAll(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public User updateUser(UUID id, UserRequest updateUserRequest) {

        User existingUser = this.userRepository.findById(id).orElse(null);

        existingUser.setUsername(updateUserRequest.username());

        return this.userRepository.save(existingUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(UUID id) { this.userRepository.deleteById(id); }
}
