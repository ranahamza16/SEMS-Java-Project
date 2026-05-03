// File: observer/NotificationObserver.java — Maps to: UC-11, FR18
package com.sems.observer;

import com.sems.model.Notification;
import com.sems.model.User;
import com.sems.singleton.DatabaseManager;

/**
 * Creates Notification entries when system events occur.
 * Crash-safe: silently handles null data.
 */
public class NotificationObserver implements SystemObserver {

    @Override
    public void update(String event, Object data) {
        try {
            if (event == null || data == null) return;

            if (data instanceof User user) {
                String message = formatMessage(event);
                Notification notification = new Notification(user, message);
                DatabaseManager.getInstance().saveNotification(notification);
            }
        } catch (Exception e) {
            System.err.println("[NotificationObserver] Error processing event: " + e.getMessage());
        }
    }

    private String formatMessage(String event) {
        if (event == null) return "System notification";
        return switch (event) {
            case "BORROW_SUBMITTED" -> "Your borrow request has been submitted and is awaiting approval.";
            case "BORROW_APPROVED" -> "Your borrow request has been approved! Pick up your equipment.";
            case "BORROW_REJECTED" -> "Your borrow request has been rejected.";
            case "RETURN_PROCESSED" -> "Equipment return has been recorded successfully.";
            case "OVERDUE_ALERT" -> "You have overdue equipment. Please return immediately.";
            default -> "System notification: " + event;
        };
    }
}
