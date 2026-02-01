package com.team.GroundTruth.entity.maps;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/**
 * JPA entity storing individual safety modifier values per edge.
 * These values are used to compute the final walk_safe_edge_costs.
 */
@Entity
@Table(name = "walk_safe_modifiers")
public class WalkSafeModifierEntity {

	@Id
	@Column(name = "edge_id")
	private Long edgeId;

	@Column(name = "pop_density")
	private Double popDensity;

	@Column(name = "streetlight")
	private Double streetlight;

	@Column(name = "crime_in_area")
	private Double crimeInArea;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	protected WalkSafeModifierEntity() {
	}

	public WalkSafeModifierEntity(Long edgeId) {
		this.edgeId = edgeId;
		this.updatedAt = OffsetDateTime.now();
	}

	public Long getEdgeId() {
		return edgeId;
	}

	public Double getPopDensity() {
		return popDensity;
	}

	public void setPopDensity(Double popDensity) {
		this.popDensity = popDensity;
		this.updatedAt = OffsetDateTime.now();
	}

	public Double getStreetlight() {
		return streetlight;
	}

	public void setStreetlight(Double streetlight) {
		this.streetlight = streetlight;
		this.updatedAt = OffsetDateTime.now();
	}

	public Double getCrimeInArea() {
		return crimeInArea;
	}

	public void setCrimeInArea(Double crimeInArea) {
		this.crimeInArea = crimeInArea;
		this.updatedAt = OffsetDateTime.now();
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}
}