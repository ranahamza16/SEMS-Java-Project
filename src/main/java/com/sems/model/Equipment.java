// File: model/Equipment.java — Maps to: UC-02, UC-04, UC-05, UC-07, FR2, FR4, FR5
// State Pattern: delegates borrow/return/damage to currentState
// Crash-free: null-safe state delegation, defensive transitions
package com.sems.model;

import com.sems.model.enums.EquipmentStatus;
import com.sems.state.equipment.AvailableState;
import com.sems.state.equipment.EquipmentState;
import java.util.UUID;

/**
 * Core domain entity — represents a borrowable piece of sports equipment.
 * Uses State pattern for lifecycle management.
 */
public class Equipment {

    private final String equipmentId;
    private String equipmentName;
    private final EquipmentCategory category; // Composition
    private EquipmentStatus status;
    private int quantity;
    private EquipmentState currentState;

    public Equipment(String equipmentName, EquipmentCategory category, int quantity) {
        this.equipmentId = UUID.randomUUID().toString();
        this.equipmentName = (equipmentName != null) ? equipmentName.trim() : "Unknown Equipment";
        this.category = (category != null) ? category : new EquipmentCategory("General", "General equipment");
        this.quantity = Math.max(0, quantity);
        this.status = EquipmentStatus.AVAILABLE;
        this.currentState = new AvailableState();
    }

    // --- Getters (null-safe) ---
    public String getEquipmentId() { return equipmentId != null ? equipmentId : ""; }
    public String getEquipmentName() { return equipmentName != null ? equipmentName : "Unknown"; }
    public EquipmentCategory getCategory() {
        return category != null ? category : new EquipmentCategory("General", "");
    }
    public EquipmentStatus getStatus() { return status != null ? status : EquipmentStatus.AVAILABLE; }
    public int getQuantity() { return Math.max(0, quantity); }
    public EquipmentState getCurrentState() { return currentState; }

    // --- Setters ---
    public void setEquipmentName(String name) { if (name != null) this.equipmentName = name.trim(); }
    public void setStatus(EquipmentStatus status) { if (status != null) this.status = status; }
    public void setQuantity(int quantity) { this.quantity = Math.max(0, quantity); }

    /**
     * Transition to a new state — safe, never sets null state.
     * Fallback: remains in current state if newState is null.
     */
    public void transitionTo(EquipmentState newState) {
        if (newState != null) {
            this.currentState = newState;
            // Sync enum status with state name
            try {
                EquipmentStatus parsed = EquipmentStatus.fromString(newState.getStateName());
                if (parsed != null) {
                    this.status = parsed;
                }
            } catch (Exception e) {
                // Status sync failed — non-critical, keep current status
            }
        }
    }

    /** Delegate to state — crash-safe. */
    public void borrow() {
        if (currentState != null) {
            currentState.borrow(this);
        }
    }

    /** Delegate to state — crash-safe. */
    public void returnItem() {
        if (currentState != null) {
            currentState.returnItem(this);
        }
    }

    /** Delegate to state — crash-safe. */
    public void reportDamage() {
        if (currentState != null) {
            currentState.reportDamage(this);
        }
    }

    /** Delegate to state — crash-safe. */
    public void scheduleMaintenance() {
        if (currentState != null) {
            currentState.scheduleMaintenance(this);
        }
    }

    public boolean isAvailable() {
        return getStatus() == EquipmentStatus.AVAILABLE && getQuantity() > 0;
    }

    @Override
    public String toString() {
        return getEquipmentName() + " [" + getStatus().getDisplayName() + "] (Qty: " + getQuantity() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Equipment)) return false;
        return equipmentId != null && equipmentId.equals(((Equipment) o).equipmentId);
    }

    @Override
    public int hashCode() {
        return equipmentId != null ? equipmentId.hashCode() : 0;
    }
}
