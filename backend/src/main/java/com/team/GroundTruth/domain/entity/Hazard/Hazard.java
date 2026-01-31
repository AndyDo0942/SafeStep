package com.team.GroundTruth.domain.entity.Hazard;

import com.team.GroundTruth.domain.entity.HazardReport.HazardReport;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing a classified hazard within a report.
 */
@Entity
@Table(name = "hazards")
public class Hazard {
    /**
     * Primary key for the hazard.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Parent report that produced this hazard.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private HazardReport report;

    /**
     * Classification label for the hazard (e.g., pothole, ice).
     */
    @Column(name = "label", nullable = false, length = 50)
    private String label;

    /**
     * Model confidence score for the label.
     */
    @Column(name = "confidence")
    private Double confidence;

    /**
     * Timestamp when the hazard record was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Creates an empty hazard for JPA.
     */
    public Hazard() {}

    /**
     * Sets the creation timestamp on first persist.
     */
    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    /**
     * Returns the hazard id.
     *
     * @return hazard id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the hazard id.
     *
     * @param id hazard id
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Returns the parent hazard report.
     *
     * @return hazard report
     */
    public HazardReport getReport() {
        return report;
    }

    /**
     * Sets the parent hazard report.
     *
     * @param report hazard report
     */
    public void setReport(HazardReport report) {
        this.report = report;
    }

    /**
     * Returns the hazard label produced by classification.
     *
     * @return hazard label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the hazard label produced by classification.
     *
     * @param label hazard label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the classification confidence score.
     *
     * @return confidence score
     */
    public Double getConfidence() {
        return confidence;
    }

    /**
     * Sets the classification confidence score.
     *
     * @param confidence confidence score
     */
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    /**
     * Returns the hazard creation timestamp.
     *
     * @return creation timestamp
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the hazard creation timestamp.
     *
     * @param createdAt creation timestamp
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
