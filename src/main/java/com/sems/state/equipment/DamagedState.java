// File: state/equipment/DamagedState.java — Maps to: State Machine Diagram
package com.sems.state.equipment;

import com.sems.model.Equipment;

/**
 * Equipment is DAMAGED — can go to maintenance for repair or be retired.
 * Cannot be borrowed or returned.
 */
public class DamagedState implements EquipmentState {

    @Override
    public void borrow(Equipment context) { /* Illegal — damaged */ }

    @Override
    public void returnItem(Equipment context) { /* Cannot return damaged directly */ }

    @Override
    public void reportDamage(Equipment context) { /* Already damaged */ }

    @Override
    public void scheduleMaintenance(Equipment context) {
        if (context == null) return;
        context.transitionTo(new InMaintenanceState());
    }

    @Override
    public String getStateName() { return "DAMAGED"; }
}
