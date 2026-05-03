// File: service/NotificationService.java — Maps to: UC-11, FR18
package com.sems.service;

import com.sems.model.Notification;
import com.sems.model.User;
import com.sems.singleton.DatabaseManager;
import java.util.ArrayList;
import java.util.List;

/** Notification fetch/manage service — always returns safe defaults. */
public class NotificationService {

    private final DatabaseManager db;

    public NotificationService() {
        this.db = DatabaseManager.getInstance();
    }

    public List<Notification> getNotifications(User user) {
        try {
            return db.getNotificationsForUser(user);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public long getUnreadCount(User user) {
        try {
            return db.getUnreadNotificationCount(user);
        } catch (Exception e) {
            return 0;
        }
    }

    public void markAllAsRead(User user) {
        try {
            List<Notification> notifications = db.getNotificationsForUser(user);
            if (notifications != null) {
                notifications.forEach(n -> { if (n != null) n.markAsRead(); });
            }
        } catch (Exception e) {
            System.err.println("[NotificationService] Error: " + e.getMessage());
        }
    }
}
