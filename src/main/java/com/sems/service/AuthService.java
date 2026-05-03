// File: service/AuthService.java — Maps to: UC-01, FR1
// Crash-free: all methods return safe results, never throw uncaught exceptions
package com.sems.service;

import com.sems.factory.UserFactory;
import com.sems.model.User;
import com.sems.model.enums.UserRole;
import com.sems.singleton.DatabaseManager;
import com.sems.singleton.SessionManager;

/**
 * Authentication service — handles login and registration.
 * Password comparison is plain-text for demo (no hashing library required).
 */
public class AuthService {

    private final DatabaseManager db;

    public AuthService() {
        this.db = DatabaseManager.getInstance();
    }

    /**
     * Authenticate user by email and password.
     * Returns User on success, null on failure (never throws).
     */
    public User authenticate(String email, String password) {
        try {
            if (email == null || email.trim().isEmpty()) return null;
            if (password == null || password.isEmpty()) return null;

            User user = db.findUserByEmail(email.trim().toLowerCase());
            if (user == null) return null;
            if (user.isSuspended()) return null;

            // Plain-text comparison for demo
            if (!password.equals(user.getPasswordHash())) return null;

            // Start session
            SessionManager.getInstance().startSession(user);
            db.recordAudit("LOGIN_SUCCESS", user.getUserId(), "User logged in: " + user.getEmail());
            return user;
        } catch (Exception e) {
            System.err.println("[AuthService] Authentication error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Register a new user. Returns error message string or null on success.
     * Crash-safe: validates everything before creating user.
     */
    public String register(String name, String email, String password, String confirmPassword, UserRole role) {
        try {
            // Validate name
            if (name == null || name.trim().isEmpty()) return "Name is required.";
            if (name.trim().length() < 2) return "Name must be at least 2 characters.";

            // Validate email
            if (email == null || email.trim().isEmpty()) return "Email is required.";
            if (!isValidEmail(email.trim())) return "Please enter a valid email address.";
            if (db.emailExists(email.trim())) return "Email already registered.";

            // Validate password
            if (password == null || password.isEmpty()) return "Password is required.";
            if (password.length() < 8) return "Password must be at least 8 characters.";
            if (confirmPassword == null || !password.equals(confirmPassword)) return "Passwords do not match.";

            // Validate role
            if (role == null) return "Please select your role.";
            if (role == UserRole.SPORTS_HEAD) return "Sports Head accounts are pre-configured.";

            // Create and save user
            User newUser = UserFactory.create(role, name.trim(), email.trim(), password);
            db.saveUser(newUser);
            db.recordAudit("REGISTER", newUser.getUserId(), "New user registered: " + email);

            return null; // Success
        } catch (Exception e) {
            System.err.println("[AuthService] Registration error: " + e.getMessage());
            return "Registration failed. Please try again.";
        }
    }

    /** Logout current user — always safe. */
    public void logout() {
        try {
            SessionManager.getInstance().endSession();
        } catch (Exception e) {
            System.err.println("[AuthService] Logout error: " + e.getMessage());
        }
    }

    /** Email validation regex — defensive, never throws. */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        try {
            return email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        } catch (Exception e) {
            return false;
        }
    }
}
