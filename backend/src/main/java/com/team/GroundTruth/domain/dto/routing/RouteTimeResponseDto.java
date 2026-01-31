package com.team.GroundTruth.domain.dto.routing;

/**
 * Response body containing the estimated travel time.
 *
 * @param durationSeconds estimated travel time in seconds
 */
public record RouteTimeResponseDto(double durationSeconds) {
}
