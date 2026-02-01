package com.team.GroundTruth.entity.maps;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/**
 * JPA entity representing pre-computed walk safety edge costs based on statistical modifiers.
 */
@Entity
@Table(name = "walk_safe_edge_costs")
public class WalkSafeEdgeCostEntity {

	@Id
	@Column(name = "edge_id")
	private Long edgeId;

	@Column(name = "cost_seconds", nullable = false)
	private double costSeconds;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	protected WalkSafeEdgeCostEntity() {
	}

	public WalkSafeEdgeCostEntity(Long edgeId, double costSeconds) {
		this.edgeId = edgeId;
		this.costSeconds = costSeconds;
		this.updatedAt = OffsetDateTime.now();
	}

	public Long getEdgeId() {
		return edgeId;
	}

	public double getCostSeconds() {
		return costSeconds;
	}

	public void setCostSeconds(double costSeconds) {
		this.costSeconds = costSeconds;
		this.updatedAt = OffsetDateTime.now();
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}
}