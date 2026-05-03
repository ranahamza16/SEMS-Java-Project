// File: service/ReturnService.java — Maps to: UC-07, UC-16, FR8, FR17
// Crash-free: condition routing with safe fallbacks
package com.sems.service;

import com.sems.model.*;
import com.sems.model.enums.*;
import com.sems.singleton.DatabaseManager;
import com.sems.singleton.InventoryManager;
import com.sems.state.equipment.*;
import java.time.LocalDate;

/**
 * Processes equipment returns with condition-based routing.
 * GOOD → Available, NEEDS_INSPECTION/MINOR_REPAIR → Maintenance, MAJOR_REPAIR → Damaged.
 */
public class ReturnService {

    private final DatabaseManager db;

    public ReturnService() {
        this.db = DatabaseManager.getInstance();
    }

    /**
     * Process a return. Returns error message or null on success.
     * Per UC-07 flow: validates, routes by condition, creates records.
     */
    public String processReturn(String borrowRequestId, ConditionStatus condition, String notes) {
        try {
            if (borrowRequestId == null || borrowRequestId.isEmpty()) return "Invalid request.";

            BorrowRequest request = db.findBorrowRequest(borrowRequestId);
            if (request == null) return "Borrow request not found.";

            BorrowStatus currentStatus = request.getStatus();
            if (currentStatus != BorrowStatus.ACTIVE && currentStatus != BorrowStatus.OVERDUE) {
                return "This item has already been returned.";
            }

            if (condition == null) return "Please report equipment condition.";

            // Require notes for damage
            if (condition.requiresAttention() && (notes == null || notes.trim().isEmpty())) {
                return "Please describe the damage observed.";
            }

            Equipment equipment = request.getEquipment();
            if (equipment == null) return "Equipment record not found.";

            // Check for late return
            boolean isLate = request.getReturnDate() != null && LocalDate.now().isAfter(request.getReturnDate());

            // Complete the borrow request
            request.setStatus(BorrowStatus.COMPLETED);
            request.setActualReturnDate(LocalDate.now());

            // Route by condition (per CORE_USE_CASES State Machine)
            switch (condition) {
                case GOOD:
                    equipment.transitionTo(new AvailableState());
                    equipment.setQuantity(equipment.getQuantity() + 1);
                    break;
                case NEEDS_INSPECTION:
                case MINOR_REPAIR:
                    equipment.transitionTo(new InMaintenanceState());
                    break;
                case MAJOR_REPAIR:
                case WRITE_OFF:
                    equipment.transitionTo(new DamagedState());
                    // Create damage report
                    DamageReport report = new DamageReport(equipment, request.getBorrower(),
                            notes != null ? notes.trim() : "Major damage reported");
                    db.saveDamageReport(report);
                    break;
            }

            // Save return record
            ReturnRecord returnRecord = new ReturnRecord(request, condition, notes);
            db.saveReturnRecord(returnRecord);

            // Notify sports head
            String statusMsg = equipment.getEquipmentName() + " returned by " + request.getBorrowerName()
                    + ". Condition: " + condition.getDisplayName()
                    + (isLate ? " (LATE RETURN)" : "");
            notifySportsHead(statusMsg);

            // Audit log
            db.recordAudit("RETURN", request.getBorrower() != null ? request.getBorrower().getUserId() : "UNKNOWN",
                    "Returned " + equipment.getEquipmentName() + " — " + condition.getDisplayName());

            InventoryManager.getInstance().notifyObservers("RETURN_PROCESSED",
                    request.getBorrower());

            return null; // Success
        } catch (Exception e) {
            System.err.println("[ReturnService] Return error: " + e.getMessage());
            return "An error occurred processing the return. Please try again.";
        }
    }

    private void notifySportsHead(String message) {
        try {
            for (User u : db.getAllUsers()) {
                if (u != null && u.getRole() == UserRole.SPORTS_HEAD) {
                    db.saveNotification(new Notification(u, message));
                }
            }
        } catch (Exception e) {
            System.err.println("[ReturnService] Notification error: " + e.getMessage());
        }
    }
}
