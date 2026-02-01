package com.team.GroundTruth.domain.dto.routing;

/**
 * A single coordinate point in a route path.
 *
 * @param lat latitude in decimal degrees
 * @param lon longitude in decimal degrees
 */
public record CoordinateDto(double lat, double lon) {
}