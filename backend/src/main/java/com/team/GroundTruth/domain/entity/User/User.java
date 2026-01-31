package com.team.GroundTruth.domain.entity.User;


import com.team.GroundTruth.domain.entity.HazardReport.HazardReport;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity representing an application user.
 */
@Entity
@Table(name="users")
public class User {

    /**
     * Primary key for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Unique username used for identification.
     */
    @Column(nullable = false, name="username", unique=true, length = 50)
    private String username;

    /**
     * Hazard reports authored by the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HazardReport> reports = new ArrayList<>();

    /**
     * Creates an empty user entity for JPA.
     */
    public User() {}

    /**
     * Creates a user entity with the provided data.
     *
     * @param id user id
     * @param username unique username
     * @param reports hazard reports authored by the user
     */
    public User(UUID id, String username, List<HazardReport> reports) {
        this.id = id;
        this.username = username;
        this.reports = reports;
    }

    /**
     * Returns the user id.
     *
     * @return user id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the user id.
     *
     * @param id user id
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Returns the username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the hazard reports authored by the user.
     *
     * @return hazard reports
     */
    public List<HazardReport> getReports() {
        return reports;
    }

    /**
     * Sets the hazard reports for the user.
     *
     * @param reports hazard reports
     */
    public void setReports(List<HazardReport> reports) {
        this.reports = reports;
    }
}
