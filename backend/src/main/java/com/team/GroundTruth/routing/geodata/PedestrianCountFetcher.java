package com.team.GroundTruth.routing.geodata;

import com.fasterxml.jackson.databind.JsonNode;
import com.team.GroundTruth.config.GeoDataConfig;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Fetches pedestrian count data from NYC DOT Bi-Annual Pedestrian Counts via Socrata SODA API.
 */
@Component
public class PedestrianCountFetcher {

	private static final Logger LOG = LoggerFactory.getLogger(PedestrianCountFetcher.class);
	private static final double MAX_PEDESTRIAN_VOLUME = 10000.0;
	private static final double DEFAULT_VALUE = 0.5;

	private final WebClient webClient;
	private final GeoDataConfig config;

	public PedestrianCountFetcher(WebClient webClient, GeoDataConfig config) {
		this.webClient = Objects.requireNonNull(webClient, "webClient");
		this.config = Objects.requireNonNull(config, "config");
	}

	/**
	 * Fetches pedestrian count data for a location and returns a normalized value.
	 *
	 * @param lat latitude
	 * @param lon longitude
	 * @param radiusMeters search radius
	 * @return normalized value (0-1), higher means more pedestrian traffic
	 */
	public double fetch(double lat, double lon, double radiusMeters) {
		try {
			String query = String.format(
					"$where=within_circle(the_geom, %f, %f, %f)",
					lat, lon, radiusMeters
			);

			JsonNode response = webClient.get()
					.uri(config.getPedestrianEndpoint() + "?" + query)
					.retrieve()
					.bodyToMono(JsonNode.class)
					.block();

			if (response == null || !response.isArray() || response.isEmpty()) {
				LOG.debug("No pedestrian data found for location ({}, {})", lat, lon);
				return DEFAULT_VALUE;
			}

			double totalVolume = 0.0;
			for (JsonNode record : response) {
				JsonNode volumeNode = record.get("vol");
				if (volumeNode != null && volumeNode.isNumber()) {
					totalVolume += volumeNode.asDouble();
				}
			}

			double normalized = Math.min(1.0, totalVolume / MAX_PEDESTRIAN_VOLUME);
			LOG.debug("Pedestrian volume at ({}, {}): {} -> normalized: {}", lat, lon, totalVolume, normalized);
			return normalized;

		} catch (Exception e) {
			LOG.warn("Failed to fetch pedestrian data for ({}, {}): {}", lat, lon, e.getMessage());
			return DEFAULT_VALUE;
		}
	}
}