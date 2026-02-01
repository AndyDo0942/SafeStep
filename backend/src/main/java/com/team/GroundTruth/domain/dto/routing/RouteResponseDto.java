package com.team.GroundTruth.domain.dto.routing;

import java.util.List;
import java.util.Map;

/**
 * Response containing a single route with GeoJSON geometry.
 *
 * @param distanceMeters total distance in meters
 * @param durationSeconds total travel time in seconds
 * @param pathNodeIds ordered list of node IDs in the path
 * @param pathEdgeIds ordered list of edge IDs in the path
 * @param geoJson GeoJSON feature representing the route geometry
 */
public record RouteResponseDto(
		double distanceMeters,
		double durationSeconds,
		List<Long> pathNodeIds,
		List<Long> pathEdgeIds,
		GeoJsonFeature geoJson
) {
	/**
	 * GeoJSON Feature object.
	 *
	 * @param type feature type (always "Feature")
	 * @param properties feature properties
	 * @param geometry feature geometry
	 */
	public record GeoJsonFeature(
			String type,
			Map<String, Object> properties,
			GeoJsonGeometry geometry
	) {
	}

	/**
	 * GeoJSON Geometry object.
	 *
	 * @param type geometry type (e.g., "LineString")
	 * @param coordinates list of coordinate pairs [lon, lat]
	 */
	public record GeoJsonGeometry(
			String type,
			List<List<Double>> coordinates
	) {
	}
}