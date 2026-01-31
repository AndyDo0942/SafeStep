package com.team.GroundTruth.domain.dto.user;

import com.team.GroundTruth.domain.entity.HazardReport.HazardReport;

import java.util.List;
import java.util.UUID;

/**
 * DTO representing a user and their associated hazard reports.
 *
 * @param id user identifier
 * @param username user's unique name
 * @param hazardReports hazard reports authored by the user
 */
public record UserDto(
        UUID id,
        String username,
        List<HazardReport> hazardReports
) {
}
