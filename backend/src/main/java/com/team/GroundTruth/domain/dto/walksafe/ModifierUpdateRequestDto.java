package com.team.GroundTruth.domain.dto.walksafe;

/**
 * Request body for updating a walk safety modifier at a geodata location.
 *
 * @param lat latitude in decimal degrees
 * @param lon longitude in decimal degrees
 * @param radiusMeters search radius in meters
 * @param value the modifier value to set
 */
public record ModifierUpdateRequestDto(double lat, double lon, Double radiusMeters, double value) {

	private static final double DEFAULT_RADIUS_METERS = 50.0;

	public double radiusMetersOrDefault() {
		return radiusMeters != null ? radiusMeters : DEFAULT_RADIUS_METERS;
	}
}