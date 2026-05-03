// File: state/equipment/EquipmentState.java — State Pattern Interface
// Maps to: FR6, FR8, FR10, UC-05, UC-07, UC-08
package com.sems.state.equipment;

import com.sems.model.Equipment;

/**
 * State pattern interface for Equipment lifecycle.
 * Each concrete state defines legal transitions and throws
 * IllegalStateException for illegal ones (caught by callers).
 */
public interface EquipmentState {
    void borrow(Equipment context);
    void returnItem(Equipment context);
    void reportDamage(Equipment context);
    void scheduleMaintenance(Equipment context);
    String getStateName();
}
