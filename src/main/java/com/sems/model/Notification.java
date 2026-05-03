// File: model/Notification.java — Maps to: UC-11, FR18
package com.sems.model;

import java.time.LocalDateTime;
import java.util.UUID;

/** Notification aggregation with User — user can exist without notification. */
public class Notification {
    private final String notificationId;
    private final User recipient;
    private final String message;
    private final LocalDateTime timestamp;
    private boolean read;

    public Notification(User recipient, String message) {
        this.notificationId = UUID.randomUUID().toString();
        this.recipient = recipient;
        this.message = (message != null) ? message : "";
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    public String getNotificationId() { return notificationId != null ? notificationId : ""; }
    public User getRecipient() { return recipient; }
    public String getMessage() { return message != null ? message : ""; }
    public LocalDateTime getTimestamp() { return timestamp != null ? timestamp : LocalDateTime.now(); }
    public boolean isRead() { return read; }
    public void markAsRead() { this.read = true; }

    @Override
    public String toString() { return getMessage(); }
}
