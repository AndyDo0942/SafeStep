package com.team.GroundTruth.domain.dto.routing;

/**
 * Types of routes returned by the multi-route endpoint.
 */
public enum RouteType {
	/**
	 * Standard A* route (shortest time).
	 */
	STANDARD,

	/**
	 * Route that avoids hazards.
	 */
	SAFE,

	/**
	 * Route that prioritizes passing through hazards.
	 */
	HAZARD
}