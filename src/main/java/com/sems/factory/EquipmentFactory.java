// File: factory/EquipmentFactory.java — Maps to: FR5, UC-19
// Factory Pattern: creates Equipment with correct initial state
package com.sems.factory;

import com.sems.model.Equipment;
import com.sems.model.EquipmentCategory;
import com.sems.model.enums.EquipmentStatus;
import com.sems.state.equipment.*;

/**
 * Factory for creating Equipment with the correct initial EquipmentState.
 */
public class EquipmentFactory {

    /**
     * Creates equipment with proper category and initial state.
     * Crash-safe: defaults to AVAILABLE with quantity 0 on bad input.
     */
    public static Equipment create(String name, String categoryName, int quantity, EquipmentStatus initialStatus) {
        String safeName = (name != null) ? name.trim() : "Unknown Equipment";
        String safeCat = (categoryName != null) ? categoryName.trim() : "General";
        int safeQty = Math.max(0, quantity);

        EquipmentCategory category = new EquipmentCategory(safeCat, safeCat + " equipment");
        Equipment equipment = new Equipment(safeName, category, safeQty);

        // Set initial state based on status
        EquipmentStatus status = (initialStatus != null) ? initialStatus : EquipmentStatus.AVAILABLE;
        switch (status) {
            case BORROWED -> equipment.transitionTo(new BorrowedState());
            case IN_MAINTENANCE -> equipment.transitionTo(new InMaintenanceState());
            case DAMAGED -> equipment.transitionTo(new DamagedState());
            case RETIRED -> equipment.transitionTo(new RetiredState());
            default -> equipment.transitionTo(new AvailableState());
        }

        return equipment;
    }
}
