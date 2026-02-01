package com.team.GroundTruth.routing.geodata;

/**
 * Interface for fetching geodata from external APIs to compute walk safety modifiers.
 */
public interface GeoDataProvider {

	/**
	 * Fetches population density/pedestrian count data for a location.
	 *
	 * @param lat latitude
	 * @param lon longitude
	 * @param radiusMeters search radius
	 * @return normalized value (0-1 scale, higher = more foot traffic)
	 */
	double getPopDensity(double lat, double lon, double radiusMeters);

	/**
	 * Fetches streetlight coverage data for a location from OpenStreetMap.
	 *
	 * @param lat latitude
	 * @param lon longitude
	 * @param radiusMeters search radius
	 * @return normalized value (0-1 scale, higher = more streetlights)
	 */
	double getStreetlightCoverage(double lat, double lon, double radiusMeters);

	/**
	 * Fetches crime incident data for a location.
	 *
	 * @param lat latitude
	 * @param lon longitude
	 * @param radiusMeters search radius
	 * @return normalized value (0-1 scale, higher = more crime)
	 */
	double getCrimeLevel(double lat, double lon, double radiusMeters);
}