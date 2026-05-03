// File: state/equipment/InMaintenanceState.java — Maps to: State Machine Diagram
package com.sems.state.equipment;

import com.sems.model.Equipment;

/**
 * Equipment is IN_MAINTENANCE — can only transition to Available (repair complete)
 * or Retired (beyond repair). No borrowing or returning.
 */
public class InMaintenanceState implements EquipmentState {

    @Override
    public void borrow(Equipment context) { /* Illegal — in maintenance */ }

    @Override
    public void returnItem(Equipment context) {
        // Repair complete → back to available
        if (context == null) return;
        context.transitionTo(new AvailableState());
    }

    @Override
    public void reportDamage(Equipment context) {
        if (context == null) return;
        context.transitionTo(new DamagedState());
    }

    @Override
    public void scheduleMaintenance(Equipment context) { /* Already in maintenance */ }

    @Override
    public String getStateName() { return "IN_MAINTENANCE"; }
}
