package com.team.GroundTruth.routing.repo;

import com.team.GroundTruth.entity.maps.WalkSafeModifierEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for walk safety modifiers.
 */
public interface WalkSafeModifierRepository extends JpaRepository<WalkSafeModifierEntity, Long> {

	/**
	 * Finds edges that intersect with a point within a given radius.
	 *
	 * @param lon longitude of the point
	 * @param lat latitude of the point
	 * @param radiusMeters search radius in meters
	 * @return edge ids that intersect the search area
	 */
	@Query(value = """
			SELECT e.id
			FROM edges e
			WHERE e.mode = 'walk'
			  AND ST_DWithin(
			      e.geom::geography,
			      ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
			      :radiusMeters
			  )
			""", nativeQuery = true)
	List<Long> findWalkEdgeIdsNearPoint(
			@Param("lon") double lon,
			@Param("lat") double lat,
			@Param("radiusMeters") double radiusMeters
	);

	/**
	 * Finds modifiers for the given edge ids.
	 *
	 * @param edgeIds edge ids to look up
	 * @return matching modifiers
	 */
	@Query(value = """
			SELECT *
			FROM walk_safe_modifiers
			WHERE edge_id = ANY(:edgeIds)
			""", nativeQuery = true)
	List<WalkSafeModifierEntity> findByEdgeIds(@Param("edgeIds") long[] edgeIds);
}