// File: model/User.java
// Maps to: UC-01, FR1 — Abstract base class per UML Class Diagram
// Crash-free: null-safe getters, defensive borrowHistory access
package com.sems.model;

import com.sems.model.enums.UserRole;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Abstract base user class. SportsHead, Student, and Teacher extend this.
 * UML: Abstract class with inheritance hierarchy.
 */
public abstract class User {

    private final String userId;
    private String name;
    private String email;
    private String passwordHash;
    private UserRole role;
    private boolean suspended;
    private final List<BorrowRequest> borrowHistory;

    protected User(String name, String email, String passwordHash, UserRole role) {
        this.userId = UUID.randomUUID().toString();
        this.name = (name != null) ? name.trim() : "";
        this.email = (email != null) ? email.trim().toLowerCase() : "";
        this.passwordHash = (passwordHash != null) ? passwordHash : "";
        this.role = (role != null) ? role : UserRole.STUDENT;
        this.suspended = false;
        this.borrowHistory = new ArrayList<>();
    }

    // --- Getters (null-safe) ---
    public String getUserId() { return userId != null ? userId : ""; }
    public String getName() { return name != null ? name : ""; }
    public String getEmail() { return email != null ? email : ""; }
    public String getPasswordHash() { return passwordHash != null ? passwordHash : ""; }
    public UserRole getRole() { return role != null ? role : UserRole.STUDENT; }
    public boolean isSuspended() { return suspended; }

    /** Returns unmodifiable copy — safe for iteration. */
    public List<BorrowRequest> getBorrowHistory() {
        return Collections.unmodifiableList(borrowHistory != null ? borrowHistory : new ArrayList<>());
    }

    // --- Setters (validated) ---
    public void setName(String name) { this.name = (name != null) ? name.trim() : this.name; }
    public void setEmail(String email) { this.email = (email != null) ? email.trim().toLowerCase() : this.email; }
    public void setPasswordHash(String hash) { this.passwordHash = (hash != null) ? hash : this.passwordHash; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }

    /** Safe add — never adds null to history. */
    public void addBorrowRequest(BorrowRequest request) {
        if (request != null && borrowHistory != null) {
            borrowHistory.add(request);
        }
    }

    @Override
    public String toString() {
        return getName() + " (" + getRole().getDisplayName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof User)) return false;
        return userId != null && userId.equals(((User) o).userId);
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}
