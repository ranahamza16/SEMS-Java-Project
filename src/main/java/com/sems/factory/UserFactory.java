// File: factory/UserFactory.java — Maps to: UC-01, FR1
// Factory Pattern: decouples user creation from calling code
package com.sems.factory;

import com.sems.model.*;
import com.sems.model.enums.UserRole;

/**
 * Factory for creating User subclasses based on role.
 * Adding new roles requires only adding a case here — no changes to services.
 */
public class UserFactory {

    /**
     * Creates a User subclass based on the given role.
     * Crash-safe: defaults to Student if role is null or unrecognized.
     */
    public static User create(UserRole role, String name, String email, String passwordHash) {
        String safeName = (name != null) ? name.trim() : "User";
        String safeEmail = (email != null) ? email.trim().toLowerCase() : "";
        String safePass = (passwordHash != null) ? passwordHash : "";

        if (role == null) {
            return new Student(safeName, safeEmail, safePass);
        }

        return switch (role) {
            case SPORTS_HEAD -> new SportsHead(safeName, safeEmail, safePass);
            case TEACHER -> new Teacher(safeName, safeEmail, safePass);
            case STUDENT -> new Student(safeName, safeEmail, safePass);
        };
    }
}
