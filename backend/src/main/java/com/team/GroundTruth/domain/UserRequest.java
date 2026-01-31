package com.team.GroundTruth.domain;

/**
 * Domain request used to create a new user.
 *
 * @param username requested username
 */
public record UserRequest(String username) {}
