package com.team.GroundTruth.routing.geodata;

import com.fasterxml.jackson.databind.JsonNode;
import com.team.GroundTruth.config.GeoDataConfig;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Fetches streetlamp data from OpenStreetMap via Overpass API.
 */
@Component
public class StreetlampFetcher {

	private static final Logger LOG = LoggerFactory.getLogger(StreetlampFetcher.class);
	private static final double DEFAULT_VALUE = 0.5;
	private static final double LAMPS_PER_1000M2_MAX = 5.0;

	private final WebClient webClient;
	private final GeoDataConfig config;

	public StreetlampFetcher(WebClient webClient, GeoDataConfig config) {
		this.webClient = Objects.requireNonNull(webClient, "webClient");
		this.config = Objects.requireNonNull(config, "config");
	}

	/**
	 * Fetches streetlamp count for a location and returns a normalized value.
	 *
	 * @param lat latitude
	 * @param lon longitude
	 * @param radiusMeters search radius
	 * @return normalized value (0-1), higher means more streetlights
	 */
	public double fetch(double lat, double lon, double radiusMeters) {
		try {
			String overpassQuery = String.format(
					"[out:json][timeout:25];node[\"highway\"=\"street_lamp\"](around:%f,%f,%f);out count;",
					radiusMeters, lat, lon
			);

			JsonNode response = webClient.post()
					.uri(config.getOverpassEndpoint())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.bodyValue("data=" + overpassQuery)
					.retrieve()
					.bodyToMono(JsonNode.class)
					.block();

			if (response == null) {
				LOG.debug("No response from Overpass API for location ({}, {})", lat, lon);
				return DEFAULT_VALUE;
			}

			int count = 0;
			JsonNode elements = response.get("elements");
			if (elements != null && elements.isArray()) {
				count = elements.size();
			}

			// If count query, check for count in tags
			JsonNode remarksNode = response.get("remark");
			if (remarksNode != null && remarksNode.isTextual()) {
				String remark = remarksNode.asText();
				if (remark.contains("count:")) {
					try {
						count = Integer.parseInt(remark.replaceAll(".*count:\\s*(\\d+).*", "$1"));
					} catch (NumberFormatException ignored) {
					}
				}
			}

			// Normalize: lamps per 1000 square meters
			double areaSqM = Math.PI * radiusMeters * radiusMeters;
			double lampsPerArea = (count / areaSqM) * 1000.0;
			double normalized = Math.min(1.0, lampsPerArea / LAMPS_PER_1000M2_MAX);

			LOG.debug("Streetlamps at ({}, {}): {} -> normalized: {}", lat, lon, count, normalized);
			return normalized;

		} catch (Exception e) {
			LOG.warn("Failed to fetch streetlamp data for ({}, {}): {}", lat, lon, e.getMessage());
			return DEFAULT_VALUE;
		}
	}
}