package com.team.GroundTruth.routing.geodata;

import com.fasterxml.jackson.databind.JsonNode;
import com.team.GroundTruth.config.GeoDataConfig;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Fetches crime data from NYC Open Data via Socrata SODA API.
 */
@Component
public class CrimeFetcher {

	private static final Logger LOG = LoggerFactory.getLogger(CrimeFetcher.class);
	private static final double DEFAULT_VALUE = 0.5;
	private static final int MAX_CRIMES_PER_AREA = 100;
	private static final int MONTHS_LOOKBACK = 6;

	private final WebClient webClient;
	private final GeoDataConfig config;

	public CrimeFetcher(WebClient webClient, GeoDataConfig config) {
		this.webClient = Objects.requireNonNull(webClient, "webClient");
		this.config = Objects.requireNonNull(config, "config");
	}

	/**
	 * Fetches crime incident count for a location and returns a normalized value.
	 *
	 * @param lat latitude
	 * @param lon longitude
	 * @param radiusMeters search radius
	 * @return normalized value (0-1), higher means more crime
	 */
	public double fetch(double lat, double lon, double radiusMeters) {
		try {
			LocalDate sixMonthsAgo = LocalDate.now().minusMonths(MONTHS_LOOKBACK);
			String dateFilter = sixMonthsAgo.format(DateTimeFormatter.ISO_LOCAL_DATE);

			String query = String.format(
					"$where=within_circle(lat_lon, %f, %f, %f) AND cmplnt_fr_dt >= '%s'&$limit=1000",
					lat, lon, radiusMeters, dateFilter
			);

			JsonNode response = webClient.get()
					.uri(config.getCrimeEndpoint() + "?" + query)
					.retrieve()
					.bodyToMono(JsonNode.class)
					.block();

			if (response == null || !response.isArray()) {
				LOG.debug("No crime data found for location ({}, {})", lat, lon);
				return DEFAULT_VALUE;
			}

			int count = response.size();
			double normalized = Math.min(1.0, (double) count / MAX_CRIMES_PER_AREA);

			LOG.debug("Crime incidents at ({}, {}): {} -> normalized: {}", lat, lon, count, normalized);
			return normalized;

		} catch (Exception e) {
			LOG.warn("Failed to fetch crime data for ({}, {}): {}", lat, lon, e.getMessage());
			return DEFAULT_VALUE;
		}
	}
}