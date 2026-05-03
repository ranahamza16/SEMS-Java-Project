// File: state/equipment/BorrowedState.java — Maps to: State Machine Diagram
package com.sems.state.equipment;

import com.sems.model.Equipment;

/**
 * Equipment is BORROWED — can be returned or reported damaged.
 * Cannot be borrowed again or sent to maintenance directly.
 */
public class BorrowedState implements EquipmentState {

    @Override
    public void borrow(Equipment context) {
        // Illegal: already borrowed — silent no-op
    }

    @Override
    public void returnItem(Equipment context) {
        if (context == null) return;
        context.transitionTo(new AvailableState());
    }

    @Override
    public void reportDamage(Equipment context) {
        if (context == null) return;
        context.transitionTo(new DamagedState());
    }

    @Override
    public void scheduleMaintenance(Equipment context) {
        // Not allowed while borrowed — silent no-op
    }

    @Override
    public String getStateName() { return "BORROWED"; }
}
