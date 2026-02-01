package com.team.GroundTruth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for external geodata API clients.
 */
@Configuration
public class GeoDataConfig {

	@Value("${geodata.socrata.pedestrian-endpoint}")
	private String pedestrianEndpoint;

	@Value("${geodata.socrata.crime-endpoint}")
	private String crimeEndpoint;

	@Value("${geodata.overpass.endpoint}")
	private String overpassEndpoint;

	@Value("${geodata.request-delay-ms:1000}")
	private long requestDelayMs;

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
				.build();
	}

	public String getPedestrianEndpoint() {
		return pedestrianEndpoint;
	}

	public String getCrimeEndpoint() {
		return crimeEndpoint;
	}

	public String getOverpassEndpoint() {
		return overpassEndpoint;
	}

	public long getRequestDelayMs() {
		return requestDelayMs;
	}
}