// File: observer/OverdueAlertObserver.java — Maps to: UC-12, FR15
package com.sems.observer;

import com.sems.model.BorrowRequest;
import com.sems.model.Notification;
import com.sems.model.enums.BorrowStatus;
import com.sems.singleton.DatabaseManager;

/**
 * Monitors for overdue borrows and creates alerts.
 * Crash-safe: all operations wrapped in try-catch.
 */
public class OverdueAlertObserver implements SystemObserver {

    @Override
    public void update(String event, Object data) {
        try {
            if (!"OVERDUE_CHECK".equals(event) || data == null) return;

            if (data instanceof BorrowRequest request) {
                if (request.isOverdue() && request.getStatus() == BorrowStatus.ACTIVE) {
                    request.setStatus(BorrowStatus.OVERDUE);
                    if (request.getBorrower() != null) {
                        Notification alert = new Notification(
                                request.getBorrower(),
                                "OVERDUE: " + request.getEquipmentName() + " was due on " + request.getReturnDate()
                        );
                        DatabaseManager.getInstance().saveNotification(alert);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[OverdueAlertObserver] Error: " + e.getMessage());
        }
    }
}
