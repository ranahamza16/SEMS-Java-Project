// File: model/enums/UserRole.java
// Maps to: UC-01, FR1 — Role-based access control
package com.sems.model.enums;

/**
 * User roles in the Sports Equipment Management System.
 * SPORTS_HEAD is pre-seeded; STUDENT and TEACHER can self-register.
 */
public enum UserRole {
    SPORTS_HEAD("Sports Head"),
    STUDENT("Student"),
    TEACHER("Teacher");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : name();
    }

    /**
     * Safe parse — returns null instead of throwing on invalid input.
     */
    public static UserRole fromString(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return UserRole.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
