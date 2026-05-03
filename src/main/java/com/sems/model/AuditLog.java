// File: model/AuditLog.java — Maps to: NFR10, All UCs
package com.sems.model;

import java.time.LocalDateTime;
import java.util.UUID;

/** Immutable audit log entry for all system transactions. */
public class AuditLog {
    private final String logId;
    private final String action;
    private final String userId;
    private final String details;
    private final LocalDateTime timestamp;

    public AuditLog(String action, String userId, String details) {
        this.logId = UUID.randomUUID().toString();
        this.action = (action != null) ? action : "UNKNOWN";
        this.userId = (userId != null) ? userId : "SYSTEM";
        this.details = (details != null) ? details : "";
        this.timestamp = LocalDateTime.now();
    }

    public String getLogId() { return logId != null ? logId : ""; }
    public String getAction() { return action != null ? action : ""; }
    public String getUserId() { return userId != null ? userId : ""; }
    public String getDetails() { return details != null ? details : ""; }
    public LocalDateTime getTimestamp() { return timestamp != null ? timestamp : LocalDateTime.now(); }

    @Override
    public String toString() {
        return "[" + getTimestamp() + "] " + getAction() + " by " + getUserId() + ": " + getDetails();
    }
}
