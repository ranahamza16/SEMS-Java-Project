// File: singleton/SessionManager.java — Maps to: UC-01, NFR3, NFR5
// Singleton Pattern: one active user session at a time
package com.sems.singleton;

import com.sems.model.User;
import com.sems.model.enums.UserRole;

/**
 * Manages the active user session. Only one user logged in at a time.
 * Crash-safe: isLoggedIn() returns false if session is null.
 */
public class SessionManager {

    private static volatile SessionManager instance;
    private User currentUser;

    private SessionManager() {
        this.currentUser = null;
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public static synchronized void reset() {
        instance = null;
    }

    /** Start session — validates user is not null. */
    public void startSession(User user) {
        if (user != null) {
            this.currentUser = user;
            DatabaseManager.getInstance().recordAudit("LOGIN", user.getUserId(), "Session started for " + user.getName());
        }
    }

    /** End session — always safe to call, even if already logged out. */
    public void endSession() {
        if (currentUser != null) {
            DatabaseManager.getInstance().recordAudit("LOGOUT", currentUser.getUserId(), "Session ended");
        }
        this.currentUser = null;
    }

    /** Returns null if no active session — callers must check. */
    public User getCurrentUser() {
        return currentUser;
    }

    /** Safe boolean check — never throws. */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /** Check if current user has a specific role. Returns false if not logged in. */
    public boolean hasRole(UserRole role) {
        return currentUser != null && role != null && currentUser.getRole() == role;
    }

    /** Check if current user is SportsHead (admin). */
    public boolean isAdmin() {
        return hasRole(UserRole.SPORTS_HEAD);
    }
}
