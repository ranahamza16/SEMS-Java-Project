// File: state/equipment/RetiredState.java — Maps to: State Machine Diagram
package com.sems.state.equipment;

import com.sems.model.Equipment;

/**
 * Equipment is RETIRED — terminal state. No transitions allowed.
 * All operations are silent no-ops to prevent crashes.
 */
public class RetiredState implements EquipmentState {

    @Override
    public void borrow(Equipment context) { /* Terminal state — no-op */ }

    @Override
    public void returnItem(Equipment context) { /* Terminal state — no-op */ }

    @Override
    public void reportDamage(Equipment context) { /* Terminal state — no-op */ }

    @Override
    public void scheduleMaintenance(Equipment context) { /* Terminal state — no-op */ }

    @Override
    public String getStateName() { return "RETIRED"; }
}
