// File: service/EquipmentService.java — Maps to: UC-02, UC-03, UC-04, UC-19
package com.sems.service;

import com.sems.model.Equipment;
import com.sems.singleton.DatabaseManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Equipment catalog operations — search, filter, check availability.
 * All methods return safe defaults (empty lists, false) on failure.
 */
public class EquipmentService {

    private final DatabaseManager db;

    public EquipmentService() {
        this.db = DatabaseManager.getInstance();
    }

    public List<Equipment> getAllEquipment() {
        try {
            return db.getAllEquipment();
        } catch (Exception e) {
            System.err.println("[EquipmentService] Error fetching equipment: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Equipment> getAvailableEquipment() {
        try {
            return db.getAvailableEquipment();
        } catch (Exception e) {
            System.err.println("[EquipmentService] Error fetching available: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Equipment> searchEquipment(String query) {
        try {
            return db.searchEquipment(query);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Equipment findById(String id) {
        try {
            return db.findEquipmentById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean checkAvailability(Equipment equipment) {
        try {
            return equipment != null && equipment.isAvailable();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addEquipment(com.sems.model.Equipment eq) {
        try {
            if (eq != null) {
                db.saveEquipment(eq);
                db.recordAudit("EQUIPMENT_ADDED", "SPORTS_HEAD", "Added " + eq.getEquipmentName());
                return true;
            }
        } catch (Exception e) {
            System.err.println("[EquipmentService] Add error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateEquipment(com.sems.model.Equipment eq) {
        try {
            if (eq != null && db.findEquipmentById(eq.getEquipmentId()) != null) {
                db.saveEquipment(eq);
                db.recordAudit("EQUIPMENT_UPDATED", "SPORTS_HEAD", "Updated " + eq.getEquipmentName());
                return true;
            }
        } catch (Exception e) {
            System.err.println("[EquipmentService] Update error: " + e.getMessage());
        }
        return false;
    }

    public boolean retireEquipment(String id) {
        try {
            Equipment eq = db.findEquipmentById(id);
            if (eq != null) {
                eq.transitionTo(new com.sems.state.equipment.RetiredState());
                db.saveEquipment(eq);
                db.recordAudit("EQUIPMENT_RETIRED", "SPORTS_HEAD", "Retired " + eq.getEquipmentName());
                return true;
            }
        } catch (Exception e) {
            System.err.println("[EquipmentService] Retire error: " + e.getMessage());
        }
        return false;
    }
}
