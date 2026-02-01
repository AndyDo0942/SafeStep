package com.team.GroundTruth.routing.geodata;

import com.team.GroundTruth.config.GeoDataConfig;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of GeoDataProvider that fetches data from external APIs.
 */
@Service
public class GeoDataProviderImpl implements GeoDataProvider {

	private static final Logger LOG = LoggerFactory.getLogger(GeoDataProviderImpl.class);

	private final PedestrianCountFetcher pedestrianFetcher;
	private final StreetlampFetcher streetlampFetcher;
	private final CrimeFetcher crimeFetcher;
	private final GeoDataConfig config;

	public GeoDataProviderImpl(
			PedestrianCountFetcher pedestrianFetcher,
			StreetlampFetcher streetlampFetcher,
			CrimeFetcher crimeFetcher,
			GeoDataConfig config
	) {
		this.pedestrianFetcher = Objects.requireNonNull(pedestrianFetcher, "pedestrianFetcher");
		this.streetlampFetcher = Objects.requireNonNull(streetlampFetcher, "streetlampFetcher");
		this.crimeFetcher = Objects.requireNonNull(crimeFetcher, "crimeFetcher");
		this.config = Objects.requireNonNull(config, "config");
	}

	@Override
	public double getPopDensity(double lat, double lon, double radiusMeters) {
		return pedestrianFetcher.fetch(lat, lon, radiusMeters);
	}

	@Override
	public double getStreetlightCoverage(double lat, double lon, double radiusMeters) {
		return streetlampFetcher.fetch(lat, lon, radiusMeters);
	}

	@Override
	public double getCrimeLevel(double lat, double lon, double radiusMeters) {
		return crimeFetcher.fetch(lat, lon, radiusMeters);
	}

	/**
	 * Adds a delay between API requests to avoid rate limiting.
	 */
	public void delayForRateLimit() {
		try {
			Thread.sleep(config.getRequestDelayMs());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOG.warn("Rate limit delay interrupted");
		}
	}
}