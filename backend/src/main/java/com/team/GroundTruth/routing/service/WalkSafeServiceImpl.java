package com.team.GroundTruth.routing.service;

import com.team.GroundTruth.entity.maps.EdgeEntity;
import com.team.GroundTruth.entity.maps.WalkSafeEdgeCostEntity;
import com.team.GroundTruth.entity.maps.WalkSafeModifierEntity;
import com.team.GroundTruth.routing.repo.EdgeRepository;
import com.team.GroundTruth.routing.repo.WalkSafeEdgeCostRepository;
import com.team.GroundTruth.routing.repo.WalkSafeModifierRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of WalkSafeService for managing walk safety edge costs.
 */
@Service
@Transactional
public class WalkSafeServiceImpl implements WalkSafeService {

	private final EdgeRepository edgeRepository;
	private final WalkSafeModifierRepository modifierRepository;
	private final WalkSafeEdgeCostRepository costRepository;

	public WalkSafeServiceImpl(
			EdgeRepository edgeRepository,
			WalkSafeModifierRepository modifierRepository,
			WalkSafeEdgeCostRepository costRepository
	) {
		this.edgeRepository = Objects.requireNonNull(edgeRepository, "edgeRepository");
		this.modifierRepository = Objects.requireNonNull(modifierRepository, "modifierRepository");
		this.costRepository = Objects.requireNonNull(costRepository, "costRepository");
	}

	@Override
	public int initializeEdgeCosts() {
		List<EdgeEntity> walkEdges = edgeRepository.findAll().stream()
				.filter(e -> "walk".equals(e.getMode()))
				.toList();

		if (walkEdges.isEmpty()) {
			return 0;
		}

		long[] edgeIds = walkEdges.stream().mapToLong(EdgeEntity::getId).toArray();
		Map<Long, WalkSafeModifierEntity> modifiersByEdgeId = modifierRepository.findByEdgeIds(edgeIds).stream()
				.collect(Collectors.toMap(WalkSafeModifierEntity::getEdgeId, m -> m));

		List<WalkSafeEdgeCostEntity> costs = new ArrayList<>();
		for (EdgeEntity edge : walkEdges) {
			WalkSafeModifierEntity modifier = modifiersByEdgeId.get(edge.getId());
			double costSeconds = computeCost(edge.getCostSeconds(), modifier);
			costs.add(new WalkSafeEdgeCostEntity(edge.getId(), costSeconds));
		}

		costRepository.saveAll(costs);
		return costs.size();
	}

	@Override
	public int updatePopDensity(double lon, double lat, double radiusMeters, double value) {
		return updateModifier(lon, lat, radiusMeters, value, WalkSafeModifierEntity::setPopDensity);
	}

	@Override
	public int updateStreetlight(double lon, double lat, double radiusMeters, double value) {
		return updateModifier(lon, lat, radiusMeters, value, WalkSafeModifierEntity::setStreetlight);
	}

	@Override
	public int updateCrimeInArea(double lon, double lat, double radiusMeters, double value) {
		return updateModifier(lon, lat, radiusMeters, value, WalkSafeModifierEntity::setCrimeInArea);
	}

	private int updateModifier(
			double lon,
			double lat,
			double radiusMeters,
			double value,
			BiConsumer<WalkSafeModifierEntity, Double> setter
	) {
		List<Long> edgeIds = modifierRepository.findWalkEdgeIdsNearPoint(lon, lat, radiusMeters);
		if (edgeIds.isEmpty()) {
			return 0;
		}

		long[] edgeIdArray = edgeIds.stream().mapToLong(Long::longValue).toArray();
		Map<Long, WalkSafeModifierEntity> existingModifiers = modifierRepository.findByEdgeIds(edgeIdArray).stream()
				.collect(Collectors.toMap(WalkSafeModifierEntity::getEdgeId, m -> m));

		List<WalkSafeModifierEntity> toSave = new ArrayList<>();
		for (Long edgeId : edgeIds) {
			WalkSafeModifierEntity modifier = existingModifiers.get(edgeId);
			if (modifier == null) {
				modifier = new WalkSafeModifierEntity(edgeId);
			}
			setter.accept(modifier, value);
			toSave.add(modifier);
		}

		modifierRepository.saveAll(toSave);
		recalculateCosts(edgeIds);
		return edgeIds.size();
	}

	private void recalculateCosts(List<Long> edgeIds) {
		if (edgeIds.isEmpty()) {
			return;
		}

		long[] edgeIdArray = edgeIds.stream().mapToLong(Long::longValue).toArray();
		Map<Long, WalkSafeModifierEntity> modifiersByEdgeId = modifierRepository.findByEdgeIds(edgeIdArray).stream()
				.collect(Collectors.toMap(WalkSafeModifierEntity::getEdgeId, m -> m));

		Map<Long, EdgeEntity> edgesById = edgeRepository.findAllById(edgeIds).stream()
				.collect(Collectors.toMap(EdgeEntity::getId, e -> e));

		Map<Long, WalkSafeEdgeCostEntity> existingCosts = costRepository.findByEdgeIds(edgeIdArray).stream()
				.collect(Collectors.toMap(WalkSafeEdgeCostEntity::getEdgeId, c -> c));

		List<WalkSafeEdgeCostEntity> toSave = new ArrayList<>();
		for (Long edgeId : edgeIds) {
			EdgeEntity edge = edgesById.get(edgeId);
			if (edge == null) {
				continue;
			}
			WalkSafeModifierEntity modifier = modifiersByEdgeId.get(edgeId);
			double costSeconds = computeCost(edge.getCostSeconds(), modifier);

			WalkSafeEdgeCostEntity costEntity = existingCosts.get(edgeId);
			if (costEntity == null) {
				costEntity = new WalkSafeEdgeCostEntity(edgeId, costSeconds);
			} else {
				costEntity.setCostSeconds(costSeconds);
			}
			toSave.add(costEntity);
		}

		costRepository.saveAll(toSave);
	}

	/**
	 * Computes the safety-adjusted cost for an edge based on its modifiers.
	 * Higher crime and lower streetlight coverage increase cost.
	 * Higher population density decreases cost (more eyes on the street).
	 */
	private double computeCost(double baseCostSeconds, WalkSafeModifierEntity modifier) {
		if (modifier == null) {
			return baseCostSeconds;
		}

		double multiplier = 1.0;

		Double popDensity = modifier.getPopDensity();
		if (popDensity != null && popDensity > 0) {
			// Higher population density = safer = lower multiplier
			//popDesnity from 0-1, where 1 is super safe and 0 is sparse
			multiplier *= Math.min(1.5, (2 * (1 - popDensity))+0.3);
		}

		Double streetlight = modifier.getStreetlight();
		if (streetlight != null) {
			// Higher streetlight coverage = safer = lower multiplier
			// Assume streetlight is 0-1 scale
			//Check this
			multiplier *= Math.min(1.5, (2 * (1 - streetlight)) + 0.5);
		}

		Double crimeInArea = modifier.getCrimeInArea();
		if (crimeInArea != null && crimeInArea > 0) {
			// Higher crime = less safe = higher multiplier
			// Crime from 0-1 (normalized)
			multiplier *= 1.0 + (crimeInArea);
		}

		return Math.max(0.0, baseCostSeconds * multiplier);
	}
}