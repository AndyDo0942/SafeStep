package com.team.GroundTruth.routing.modifier;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Default inconvenience factor provider that returns 1.0 for all edges (no modification).
 * Use this when no street-specific inconvenience adjustments are needed, or as a fallback.
 * <p>
 * Replace with a custom implementation (e.g., database-backed or hazard-aware) to apply
 * per-street inconvenience factors.
 * </p>
 */
@Component
@Primary
public class DefaultInconvenienceFactorProvider implements InconvenienceFactorProvider {

	private static final double NEUTRAL_FACTOR = 1.0;

	@Override
	public double getFactor(long edgeId, double baseCostSeconds, double lengthMeters) {
		return NEUTRAL_FACTOR;
	}
}
