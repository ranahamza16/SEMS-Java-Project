// File: model/enums/ConditionStatus.java
// Maps to: UC-07, UC-16, FR8, FR12, FR17 — Return condition reporting
package com.sems.model.enums;

public enum ConditionStatus {
    GOOD("Good Condition"),
    NEEDS_INSPECTION("Needs Inspection"),
    MINOR_REPAIR("Minor Repair"),
    MAJOR_REPAIR("Major Repair"),
    WRITE_OFF("Write Off");

    private final String displayName;

    ConditionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : name();
    }

    /** Returns true if equipment needs maintenance or damage tracking after return. */
    public boolean requiresAttention() {
        return this != GOOD;
    }

    public static ConditionStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return ConditionStatus.valueOf(value.trim().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
