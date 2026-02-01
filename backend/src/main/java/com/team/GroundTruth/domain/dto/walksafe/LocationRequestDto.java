package com.team.GroundTruth.domain.dto.walksafe;

/**
 * Request body for computing walk safety modifiers at a geodata location.
 *
 * @param lat latitude in decimal degrees
 * @param lon longitude in decimal degrees
 * @param radiusMeters search radius in meters
 */
public record LocationRequestDto(double lat, double lon, Double radiusMeters) {

	private static final double DEFAULT_RADIUS_METERS = 100.0;

	public double radiusMetersOrDefault() {
		return radiusMeters != null ? radiusMeters : DEFAULT_RADIUS_METERS;
	}
}