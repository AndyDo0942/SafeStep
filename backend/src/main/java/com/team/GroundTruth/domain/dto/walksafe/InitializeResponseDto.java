package com.team.GroundTruth.domain.dto.walksafe;

/**
 * Response for walk safety initialization operation.
 *
 * @param edgesInitialized number of edges that were initialized
 */
public record InitializeResponseDto(int edgesInitialized) {
}