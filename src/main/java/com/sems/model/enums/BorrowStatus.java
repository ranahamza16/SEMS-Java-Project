// File: model/enums/BorrowStatus.java
// Maps to: UC-05, UC-07, FR6, FR15 — Borrow request lifecycle
package com.sems.model.enums;

public enum BorrowStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    ACTIVE("Active Borrow"),
    OVERDUE("Overdue"),
    COMPLETED("Completed");

    private final String displayName;

    BorrowStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : name();
    }

    public static BorrowStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return BorrowStatus.valueOf(value.trim().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
