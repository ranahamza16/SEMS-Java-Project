// File: model/enums/EquipmentStatus.java
// Maps to: UC-05, UC-07, FR2, FR4, FR6, FR8 — Equipment lifecycle states
package com.sems.model.enums;

public enum EquipmentStatus {
    AVAILABLE("Available"),
    BORROWED("Borrowed"),
    IN_MAINTENANCE("In Maintenance"),
    DAMAGED("Damaged"),
    RETIRED("Retired");

    private final String displayName;

    EquipmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : name();
    }

    public static EquipmentStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return EquipmentStatus.valueOf(value.trim().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
