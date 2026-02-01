package com.team.GroundTruth.domain.dto.walksafe;

/**
 * Response for walk safety modifier update operations.
 *
 * @param edgesUpdated number of edges that were updated
 */
public record ModifierUpdateResponseDto(int edgesUpdated) {
}