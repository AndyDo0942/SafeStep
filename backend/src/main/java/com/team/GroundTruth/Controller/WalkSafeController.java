package com.team.GroundTruth.controller;

import com.team.GroundTruth.domain.dto.walksafe.InitializeResponseDto;
import com.team.GroundTruth.domain.dto.walksafe.LocationRequestDto;
import com.team.GroundTruth.domain.dto.walksafe.ModifierUpdateRequestDto;
import com.team.GroundTruth.domain.dto.walksafe.ModifierUpdateResponseDto;
import com.team.GroundTruth.routing.geodata.GeoDataProvider;
import com.team.GroundTruth.routing.geodata.GeoDataProviderImpl;
import com.team.GroundTruth.routing.repo.EdgeRepository;
import com.team.GroundTruth.routing.service.WalkSafeService;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing walk safety edge costs based on statistical modifiers.
 */
@RestController
@RequestMapping(path = "/walk-safe", produces = MediaType.APPLICATION_JSON_VALUE)
public class WalkSafeController {

	private static final Logger LOG = LoggerFactory.getLogger(WalkSafeController.class);
	private static final double DEFAULT_BULK_RADIUS = 100.0;

	private final WalkSafeService walkSafeService;
	private final GeoDataProviderImpl geoDataProvider;
	private final EdgeRepository edgeRepository;

	public WalkSafeController(
			WalkSafeService walkSafeService,
			GeoDataProviderImpl geoDataProvider,
			EdgeRepository edgeRepository
	) {
		this.walkSafeService = Objects.requireNonNull(walkSafeService, "walkSafeService");
		this.geoDataProvider = Objects.requireNonNull(geoDataProvider, "geoDataProvider");
		this.edgeRepository = Objects.requireNonNull(edgeRepository, "edgeRepository");
	}

	/**
	 * Initializes the walk_safe_edge_costs table by computing costs for all walk edges
	 * based on their current modifier values.
	 *
	 * @return number of edges initialized
	 */
	@PostMapping(path = "/initialize")
	public InitializeResponseDto initialize() {
		int count = walkSafeService.initializeEdgeCosts();
		return new InitializeResponseDto(count);
	}

	/**
	 * Updates population density modifier for edges near the given location.
	 *
	 * @param request the location and value
	 * @return number of edges updated
	 */
	@PostMapping(path = "/modifier/pop-density", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ModifierUpdateResponseDto updatePopDensity(@RequestBody ModifierUpdateRequestDto request) {
		Objects.requireNonNull(request, "request");
		int count = walkSafeService.updatePopDensity(
				request.lon(),
				request.lat(),
				request.radiusMetersOrDefault(),
				request.value()
		);
		return new ModifierUpdateResponseDto(count);
	}

	/**
	 * Updates streetlight coverage modifier for edges near the given location.
	 *
	 * @param request the location and value
	 * @return number of edges updated
	 */
	@PostMapping(path = "/modifier/streetlight", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ModifierUpdateResponseDto updateStreetlight(@RequestBody ModifierUpdateRequestDto request) {
		Objects.requireNonNull(request, "request");
		int count = walkSafeService.updateStreetlight(
				request.lon(),
				request.lat(),
				request.radiusMetersOrDefault(),
				request.value()
		);
		return new ModifierUpdateResponseDto(count);
	}

	/**
	 * Updates crime-in-area modifier for edges near the given location.
	 *
	 * @param request the location and value
	 * @return number of edges updated
	 */
	@PostMapping(path = "/modifier/crime-in-area", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ModifierUpdateResponseDto updateCrimeInArea(@RequestBody ModifierUpdateRequestDto request) {
		Objects.requireNonNull(request, "request");
		int count = walkSafeService.updateCrimeInArea(
				request.lon(),
				request.lat(),
				request.radiusMetersOrDefault(),
				request.value()
		);
		return new ModifierUpdateResponseDto(count);
	}

	/**
	 * Computes and updates population density from NYC DOT Pedestrian Counts API.
	 *
	 * @param request the location to query
	 * @return number of edges updated
	 */
	@PostMapping(path = "/compute/pop-density", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ModifierUpdateResponseDto computePopDensity(@RequestBody LocationRequestDto request) {
		Objects.requireNonNull(request, "request");
		double value = geoDataProvider.getPopDensity(
				request.lat(),
				request.lon(),
				request.radiusMetersOrDefault()
		);
		int count = walkSafeService.updatePopDensity(
				request.lon(),
				request.lat(),
				request.radiusMetersOrDefault(),
				value
		);
		return new ModifierUpdateResponseDto(count);
	}

	/**
	 * Computes and updates streetlight coverage from OpenStreetMap Overpass API.
	 *
	 * @param request the location to query
	 * @return number of edges updated
	 */
	@PostMapping(path = "/compute/streetlight", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ModifierUpdateResponseDto computeStreetlight(@RequestBody LocationRequestDto request) {
		Objects.requireNonNull(request, "request");
		double value = geoDataProvider.getStreetlightCoverage(
				request.lat(),
				request.lon(),
				request.radiusMetersOrDefault()
		);
		int count = walkSafeService.updateStreetlight(
				request.lon(),
				request.lat(),
				request.radiusMetersOrDefault(),
				value
		);
		return new ModifierUpdateResponseDto(count);
	}

	/**
	 * Computes and updates crime level from NYC Open Data SODA API.
	 *
	 * @param request the location to query
	 * @return number of edges updated
	 */
	@PostMapping(path = "/compute/crime", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ModifierUpdateResponseDto computeCrime(@RequestBody LocationRequestDto request) {
		Objects.requireNonNull(request, "request");
		double value = geoDataProvider.getCrimeLevel(
				request.lat(),
				request.lon(),
				request.radiusMetersOrDefault()
		);
		int count = walkSafeService.updateCrimeInArea(
				request.lon(),
				request.lat(),
				request.radiusMetersOrDefault(),
				value
		);
		return new ModifierUpdateResponseDto(count);
	}

	/**
	 * Computes and updates all three modifiers from external APIs for a location.
	 *
	 * @param request the location to query
	 * @return number of edges updated
	 */
	@PostMapping(path = "/compute/all", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ModifierUpdateResponseDto computeAll(@RequestBody LocationRequestDto request) {
		Objects.requireNonNull(request, "request");
		double lat = request.lat();
		double lon = request.lon();
		double radius = request.radiusMetersOrDefault();

		double popDensity = geoDataProvider.getPopDensity(lat, lon, radius);
		double streetlight = geoDataProvider.getStreetlightCoverage(lat, lon, radius);
		double crime = geoDataProvider.getCrimeLevel(lat, lon, radius);

		walkSafeService.updatePopDensity(lon, lat, radius, popDensity);
		walkSafeService.updateStreetlight(lon, lat, radius, streetlight);
		int count = walkSafeService.updateCrimeInArea(lon, lat, radius, crime);

		return new ModifierUpdateResponseDto(count);
	}

	/**
	 * Bulk initializes all walk edges by fetching geodata from external APIs.
	 * This is a long-running operation with rate limiting delays.
	 *
	 * @return number of edges processed
	 */
	@PostMapping(path = "/compute/initialize-all")
	public InitializeResponseDto initializeAllFromGeodata() {
		List<Object[]> centroids = edgeRepository.findWalkEdgeCentroids();
		LOG.info("Starting bulk initialization for {} walk edges", centroids.size());

		int processed = 0;
		for (Object[] centroid : centroids) {
			Long edgeId = ((Number) centroid[0]).longValue();
			double lat = ((Number) centroid[1]).doubleValue();
			double lon = ((Number) centroid[2]).doubleValue();

			try {
				double popDensity = geoDataProvider.getPopDensity(lat, lon, DEFAULT_BULK_RADIUS);
				geoDataProvider.delayForRateLimit();

				double streetlight = geoDataProvider.getStreetlightCoverage(lat, lon, DEFAULT_BULK_RADIUS);

				double crime = geoDataProvider.getCrimeLevel(lat, lon, DEFAULT_BULK_RADIUS);
				geoDataProvider.delayForRateLimit();

				walkSafeService.updatePopDensity(lon, lat, DEFAULT_BULK_RADIUS, popDensity);
				walkSafeService.updateStreetlight(lon, lat, DEFAULT_BULK_RADIUS, streetlight);
				walkSafeService.updateCrimeInArea(lon, lat, DEFAULT_BULK_RADIUS, crime);

				processed++;
				if (processed % 100 == 0) {
					LOG.info("Processed {} / {} edges", processed, centroids.size());
				}
			} catch (Exception e) {
				LOG.warn("Failed to process edge {}: {}", edgeId, e.getMessage());
			}
		}

		walkSafeService.initializeEdgeCosts();
		LOG.info("Bulk initialization complete. Processed {} edges", processed);
		return new InitializeResponseDto(processed);
	}
}