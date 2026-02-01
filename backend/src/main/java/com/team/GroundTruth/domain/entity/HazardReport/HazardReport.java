package com.team.GroundTruth.domain.entity.HazardReport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.GroundTruth.domain.entity.Hazard.Hazard;
import com.team.GroundTruth.domain.entity.User.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity representing a user-submitted hazard report with an image.
 */
@Entity
@Table(name = "hazard_reports")
public class HazardReport {
    /**
     * Primary key for the hazard report.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * User who submitted the report.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * URL of the uploaded image used for classification.
     */
    @Column(name="image_url")
    private String imageUrl;

    /**
     * Timestamp when the report was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name="longitude", updatable = false)
    private Float longitude;

    @Column(name="latitude", updatable = false)
    private Float latitude;

    /**
     * Hazards detected within the report.
     */
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hazard> hazards = new ArrayList<>();

    /**
     * Creates an empty hazard report for JPA.
     */
    public HazardReport() {}

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
     * Returns the hazard report id.
     *
     * @return report id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the hazard report id.
     *
     * @param id report id
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Returns the user who authored the report.
     *
     * @return authoring user
     */
    @JsonIgnore
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who authored the report.
     *
     * @param user authoring user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the image URL associated with the report.
     *
     * @return image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the image URL associated with the report.
     *
     * @param imageUrl image URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Returns the longitude for the report.
     *
     * @return longitude
     */
    public Float getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude for the report.
     *
     * @param longitude longitude
     */
    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the latitude for the report.
     *
     * @return latitude
     */
    public Float getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude for the report.
     *
     * @param latitude latitude
     */
    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the report creation timestamp.
     *
     * @return creation timestamp
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the report creation timestamp.
     *
     * @param createdAt creation timestamp
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns hazards detected in this report.
     *
     * @return hazards list
     */
    public List<Hazard> getHazards() {
        return hazards;
    }

    /**
     * Sets hazards detected in this report.
     *
     * @param hazards hazards list
     */
    public void setHazards(List<Hazard> hazards) {
        this.hazards = hazards;
    }
}
