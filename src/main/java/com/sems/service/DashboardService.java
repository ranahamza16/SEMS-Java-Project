// File: service/DashboardService.java — Maps to: UC-10, FR14
package com.sems.service;

import com.sems.model.User;
import com.sems.model.enums.UserRole;
import com.sems.singleton.DatabaseManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard summary data — returns safe map of stats.
 */
public class DashboardService {

    private final DatabaseManager db;

    public DashboardService() {
        this.db = DatabaseManager.getInstance();
    }

    /** Returns a map of dashboard stats — never null, never throws. */
    public Map<String, Object> getSummary(User user) {
        Map<String, Object> summary = new HashMap<>();
        try {
            summary.put("totalEquipment", db.getAllEquipment().size());
            summary.put("availableEquipment", db.getAvailableEquipment().size());
            summary.put("pendingRequests", db.getPendingRequests().size());

            if (user != null) {
                summary.put("activeBorrows", db.getActiveBorrowsByUser(user).size());
                summary.put("totalBorrows", db.getBorrowRequestsByUser(user).size());
                summary.put("unreadNotifications", db.getUnreadNotificationCount(user));
                summary.put("role", user.getRole() != null ? user.getRole().getDisplayName() : "User");
                summary.put("userName", user.getName());
            } else {
                summary.put("activeBorrows", 0);
                summary.put("totalBorrows", 0);
                summary.put("unreadNotifications", 0L);
                summary.put("role", "Guest");
                summary.put("userName", "Guest");
            }
        } catch (Exception e) {
            System.err.println("[DashboardService] Error building summary: " + e.getMessage());
        }
        return summary;
    }
}
