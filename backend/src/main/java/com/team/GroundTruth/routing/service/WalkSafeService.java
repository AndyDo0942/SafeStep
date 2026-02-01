package com.team.GroundTruth.routing.service;

/**
 * Service for managing walk safety edge costs based on statistical modifiers.
 */
public interface WalkSafeService {

	/**
	 * Initializes the walk_safe_edge_costs table by computing costs for all walk edges
	 * based on their current modifier values.
	 *
	 * @return number of edges initialized
	 */
	int initializeEdgeCosts();

	/**
	 * Updates population density modifier for edges near the given location.
	 *
	 * @param lon longitude
	 * @param lat latitude
	 * @param radiusMeters search radius
	 * @param value population density value
	 * @return number of edges updated
	 */
	int updatePopDensity(double lon, double lat, double radiusMeters, double value);

	/**
	 * Updates streetlight modifier for edges near the given location.
	 *
	 * @param lon longitude
	 * @param lat latitude
	 * @param radiusMeters search radius
	 * @param value streetlight coverage value
	 * @return number of edges updated
	 */
	int updateStreetlight(double lon, double lat, double radiusMeters, double value);

	/**
	 * Updates crime-in-area modifier for edges near the given location.
	 *
	 * @param lon longitude
	 * @param lat latitude
	 * @param radiusMeters search radius
	 * @param value crime level value
	 * @return number of edges updated
	 */
	int updateCrimeInArea(double lon, double lat, double radiusMeters, double value);
}