// File: model/DamageReport.java — Maps to: UC-09, FR12, FR13
package com.sems.model;

import java.time.LocalDateTime;
import java.util.UUID;

/** Records equipment damage — association with Equipment + User. */
public class DamageReport {
    private final String reportId;
    private final Equipment equipment;
    private final User reporter;
    private final String description;
    private final LocalDateTime timestamp;

    public DamageReport(Equipment equipment, User reporter, String description) {
        this.reportId = UUID.randomUUID().toString();
        this.equipment = equipment;
        this.reporter = reporter;
        this.description = (description != null) ? description.trim() : "No description provided";
        this.timestamp = LocalDateTime.now();
    }

    public String getReportId() { return reportId != null ? reportId : ""; }
    public Equipment getEquipment() { return equipment; }
    public User getReporter() { return reporter; }
    public String getDescription() { return description != null ? description : ""; }
    public LocalDateTime getTimestamp() { return timestamp != null ? timestamp : LocalDateTime.now(); }
}
