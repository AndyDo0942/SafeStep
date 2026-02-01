package com.team.GroundTruth.domain.dto.routing;

import java.util.List;
import java.util.Map;

/**
 * Response body containing full route details and GeoJSON geometry.
 *
 * @param distanceMeters total distance in meters
 * @param durationSeconds total duration in seconds
 * @param pathNodeIds ordered node ids
 * @param pathEdgeIds ordered edge ids
 * @param routeGeojson GeoJSON feature representing the route
 */
public record RouteResponseDto(
		double distanceMeters,
		double durationSeconds,
		List<Long> pathNodeIds,
		List<Long> pathEdgeIds,
		GeoJsonFeature routeGeojson
) {
	/**
	 * GeoJSON feature wrapper.
	 *
	 * @param type feature type
	 * @param properties feature properties
	 * @param geometry geometry payload
	 */
	public record GeoJsonFeature(String type, Map<String, Object> properties, GeoJsonGeometry geometry) {
	}

	/**
	 * GeoJSON geometry payload.
	 *
	 * @param type geometry type
	 * @param coordinates coordinate list in [lon, lat] order
	 */
	public record GeoJsonGeometry(String type, List<List<Double>> coordinates) {
	}
}
