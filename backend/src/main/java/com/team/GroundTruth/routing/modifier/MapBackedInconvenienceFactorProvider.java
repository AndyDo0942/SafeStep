package com.team.GroundTruth.routing.modifier;

import java.util.Map;
import java.util.Objects;

/**
 * Inconvenience factor provider backed by a map of edge ID to factor.
 * Returns 1.0 for edges not present in the map. Useful for testing or when
 * factors are loaded from configuration or an external source.
 * <p>
 * The backing map can be injected or updated at runtime. For thread-safe
 * updates, use a concurrent map implementation.
 * </p>
 */
public class MapBackedInconvenienceFactorProvider implements InconvenienceFactorProvider {

	private static final double DEFAULT_FACTOR = 1.0;

	private final Map<Long, Double> factorsByEdgeId;

	/**
	 * Creates a provider with the given map. The map is not copied; modifications
	 * to it will be visible to this provider.
	 *
	 * @param factorsByEdgeId map of edge ID to inconvenience factor
	 */
	public MapBackedInconvenienceFactorProvider(Map<Long, Double> factorsByEdgeId) {
		this.factorsByEdgeId = Objects.requireNonNull(factorsByEdgeId, "factorsByEdgeId");
	}

	@Override
	public double getFactor(long edgeId, double baseCostSeconds, double lengthMeters) {
		Double factor = factorsByEdgeId.get(edgeId);
		return factor != null ? factor : DEFAULT_FACTOR;
	}
}
