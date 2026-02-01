package com.team.GroundTruth.routing.repo;

import com.team.GroundTruth.entity.maps.WalkSafeEdgeCostEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for walk safety edge costs.
 */
public interface WalkSafeEdgeCostRepository extends JpaRepository<WalkSafeEdgeCostEntity, Long> {

	/**
	 * Finds walk safety costs for the given edge ids.
	 *
	 * @param edgeIds edge ids to look up
	 * @return matching walk safety costs
	 */
	@Query(value = """
			SELECT *
			FROM walk_safe_edge_costs
			WHERE edge_id = ANY(:edgeIds)
			""", nativeQuery = true)
	List<WalkSafeEdgeCostEntity> findByEdgeIds(@Param("edgeIds") long[] edgeIds);
}