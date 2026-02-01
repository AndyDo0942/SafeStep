package com.team.GroundTruth.services.hazard_report_service;

import com.team.GroundTruth.domain.HazardReportRequest;
import com.team.GroundTruth.domain.entity.Hazard.Hazard;
import com.team.GroundTruth.domain.entity.HazardReport.HazardReport;
import com.team.GroundTruth.domain.entity.User.User;
import com.team.GroundTruth.exception.HazardReportNotFoundException;
import com.team.GroundTruth.repository.HazardReportRepository;
import com.team.GroundTruth.repository.UserRepository;
import com.team.GroundTruth.services.hazard_analysis_service.HazardAnalysisResult;
import com.team.GroundTruth.services.hazard_analysis_service.HazardAnalysisService;
import com.team.GroundTruth.services.hazard_analysis_service.HazardScore;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link HazardReportService}.
 */
@Service
public class HazardReportServiceImpl implements HazardReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazardReportServiceImpl.class);

    /**
     * Repository used to persist and load hazard reports.
     */
    private final HazardReportRepository hazardReportRepository;
    /**
     * Repository used to load users for report ownership.
     */
    private final UserRepository userRepository;
    /**
     * AI service used to analyze report images.
     */
    private final HazardAnalysisService hazardAnalysisService;

    /**
     * Creates the service with its repository dependencies.
     *
     * @param hazardReportRepository repository for hazard report persistence
     * @param userRepository repository for user lookups
     */
    public HazardReportServiceImpl(
            HazardReportRepository hazardReportRepository,
            UserRepository userRepository,
            HazardAnalysisService hazardAnalysisService
    ) {
        this.hazardReportRepository = hazardReportRepository;
        this.userRepository = userRepository;
        this.hazardAnalysisService = hazardAnalysisService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HazardReport createHazardReport(HazardReportRequest request) {
        User user = userRepository.findById(request.userId()).orElse(null);

        HazardReport report = new HazardReport();
        report.setUser(user);
        report.setImageUrl(request.imageUrl());
        report.setLatitude(request.latitude());
        report.setLongitude(request.longitude());
        report.setHazards(new ArrayList<>());

        HazardReport savedReport = hazardReportRepository.save(report);

        try {
            HazardAnalysisResult result = hazardAnalysisService.analyzeImage(request.imageUrl());
            if (result != null && result.hazards() != null && !result.hazards().isEmpty()) {
                List<Hazard> hazards = new ArrayList<>();
                for (HazardScore score : result.hazards()) {
                    if (score == null || score.hazardType() == null) {
                        continue;
                    }
                    Hazard hazard = new Hazard();
                    hazard.setReport(savedReport);
                    hazard.setLabel(score.hazardType());
                    hazard.setConfidence(score.severityScore());
                    hazards.add(hazard);
                }
                savedReport.setHazards(hazards);
                savedReport = hazardReportRepository.save(savedReport);
            }
        } catch (RuntimeException ex) {
            LOGGER.warn("AI hazard analysis failed for report {}", savedReport.getId(), ex);
        }

        return savedReport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HazardReport> getHazardReports() {
        return hazardReportRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HazardReport updateHazardReport(UUID id, HazardReportRequest updateHazardReportRequest) {
        HazardReport existingReport = hazardReportRepository.findById(id)
                .orElseThrow(() -> new HazardReportNotFoundException(id));
        User user = userRepository.findById(updateHazardReportRequest.userId()).orElse(null);

        existingReport.setUser(user);
        existingReport.setImageUrl(updateHazardReportRequest.imageUrl());
        existingReport.setLatitude(updateHazardReportRequest.latitude());
        existingReport.setLongitude(updateHazardReportRequest.longitude());

        return hazardReportRepository.save(existingReport);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteHazardReport(UUID id) {
        if (!hazardReportRepository.existsById(id)) {
            throw new HazardReportNotFoundException(id);
        }
        hazardReportRepository.deleteById(id);
    }
}
