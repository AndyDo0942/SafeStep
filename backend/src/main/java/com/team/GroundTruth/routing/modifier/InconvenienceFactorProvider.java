package com.team.GroundTruth.routing.modifier;

/**
 * Provides an inconvenience factor for graph edges used during A* routing.
 * <p>
 * The factor is a multiplier applied to the base edge cost (seconds). A factor of 1.0 means no
 * change; values greater than 1.0 increase the effective cost (e.g., 2.0 doubles the traversal
 * time), making the edge less preferable. Values between 0 and 1 reduce cost.
 * </p>
 *
 * @see com.team.GroundTruth.routing.service.RoutingServiceImpl
 */
public interface InconvenienceFactorProvider {

	/**
	 * Returns the inconvenience factor for an edge. The effective A* cost is
	 * {@code baseCostSeconds * getFactor(edgeId)}.
	 *
	 * @param edgeId graph edge identifier
	 * @param baseCostSeconds base traversal cost in seconds
	 * @param lengthMeters edge length in meters
	 * @return factor to multiply base cost by; must be positive
	 */
	double getFactor(long edgeId, double baseCostSeconds, double lengthMeters);
}
