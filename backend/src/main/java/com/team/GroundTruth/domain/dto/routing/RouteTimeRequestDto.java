package com.team.GroundTruth.domain.dto.routing;

import com.team.GroundTruth.routing.model.Location;
import com.team.GroundTruth.routing.model.TravelMode;

/**
 * Request body for travel-time routing.
 *
 * @param start start coordinate
 * @param end end coordinate
 * @param mode travel mode
 * @param radiusMeters optional search radius in meters
 */
public record RouteTimeRequestDto(LocationDto start, LocationDto end, TravelMode mode, Double radiusMeters) {

	/**
	 * Coordinate payload for routing requests.
	 *
	 * @param lat latitude in decimal degrees
	 * @param lon longitude in decimal degrees
	 */
	public record LocationDto(double lat, double lon) {
		/**
		 * Converts this DTO to the routing model.
		 *
		 * @return routing location
		 */
		public Location toLocation() {
			return new Location(lat, lon);
		}
	}
}
