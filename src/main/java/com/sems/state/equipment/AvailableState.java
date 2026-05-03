// File: state/equipment/AvailableState.java — Maps to: State Machine Diagram
package com.sems.state.equipment;

import com.sems.model.Equipment;

/**
 * Equipment is AVAILABLE — can be borrowed, reported damaged, or sent to maintenance.
 * Cannot be returned (not currently borrowed).
 */
public class AvailableState implements EquipmentState {

    @Override
    public void borrow(Equipment context) {
        if (context == null) return;
        context.transitionTo(new BorrowedState());
    }

    @Override
    public void returnItem(Equipment context) {
        // Illegal: can't return something that isn't borrowed — silent no-op
    }

    @Override
    public void reportDamage(Equipment context) {
        if (context == null) return;
        context.transitionTo(new DamagedState());
    }

    @Override
    public void scheduleMaintenance(Equipment context) {
        if (context == null) return;
        context.transitionTo(new InMaintenanceState());
    }

    @Override
    public String getStateName() { return "AVAILABLE"; }
}
