// File: service/BorrowService.java — Maps to: UC-05, UC-06, UC-12, UC-14, FR6
// Crash-free: all business rules validated, never throws uncaught
package com.sems.service;

import com.sems.model.*;
import com.sems.model.enums.BorrowStatus;
import com.sems.model.enums.EquipmentStatus;
import com.sems.singleton.DatabaseManager;
import com.sems.singleton.InventoryManager;
import com.sems.state.equipment.BorrowedState;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Core borrow workflow — submit, approve, reject requests.
 * All methods return result strings for UI display.
 */
public class BorrowService {

    private final DatabaseManager db;
    private final InventoryManager inventory;

    public BorrowService() {
        this.db = DatabaseManager.getInstance();
        this.inventory = InventoryManager.getInstance();
    }

    /**
     * Submit a borrow request. Returns error message or null on success.
     * Validates: user not null, equipment available, no overdue items, future return date.
     */
    public String submitRequest(User user, Equipment equipment, LocalDate returnDate) {
        try {
            if (user == null) return "User session invalid. Please log in again.";
            if (equipment == null) return "Please select equipment to borrow.";
            if (returnDate == null) return "Please select a return date.";
            if (!returnDate.isAfter(LocalDate.now())) return "Return date must be in the future.";
            if (equipment.getStatus() != EquipmentStatus.AVAILABLE) return "Equipment is currently unavailable.";
            if (equipment.getQuantity() <= 0) return "Equipment out of stock.";
            if (hasOverdue(user)) return "You have overdue items. Return them first.";

            // Create and save request
            BorrowRequest request = new BorrowRequest(user, equipment, returnDate);
            request.setStatus(BorrowStatus.PENDING);
            db.saveBorrowRequest(request);
            user.addBorrowRequest(request);

            // Notify sports head
            notifySportsHead("New borrow request from " + user.getName() + " for " + equipment.getEquipmentName());
            
            // explicitly notify the borrower
            db.saveNotification(new Notification(user, "Your borrow request for " + equipment.getEquipmentName() + " is pending approval."));
            
            inventory.notifyObservers("BORROW_SUBMITTED", user);

            db.recordAudit("BORROW_REQUEST", user.getUserId(),
                    "Submitted request for " + equipment.getEquipmentName());

            return null; // Success
        } catch (Exception e) {
            System.err.println("[BorrowService] Submit error: " + e.getMessage());
            return "An error occurred. Please try again.";
        }
    }

    /**
     * Approve a borrow request (SportsHead action). Returns error or null on success.
     */
    public String approveRequest(String requestId) {
        try {
            if (requestId == null || requestId.isEmpty()) return "Invalid request ID.";
            BorrowRequest request = db.findBorrowRequest(requestId);
            if (request == null) return "Request not found.";
            if (request.getStatus() != BorrowStatus.PENDING) return "Request is no longer pending.";

            Equipment eq = request.getEquipment();
            if (eq == null) return "Equipment not found.";
            if (!eq.isAvailable()) return "Equipment is no longer available.";

            // Transition states
            request.setStatus(BorrowStatus.ACTIVE);
            eq.transitionTo(new BorrowedState());
            eq.setQuantity(eq.getQuantity() - 1);

            // Notify borrower
            if (request.getBorrower() != null) {
                db.saveNotification(new Notification(request.getBorrower(),
                        "Your request for " + eq.getEquipmentName() + " has been approved!"));
                inventory.notifyObservers("BORROW_APPROVED", request.getBorrower());
            }

            db.recordAudit("BORROW_APPROVED", "SPORTS_HEAD", "Approved request: " + requestId);
            return null; // Success
        } catch (Exception e) {
            System.err.println("[BorrowService] Approve error: " + e.getMessage());
            return "Error approving request. Please try again.";
        }
    }

    /**
     * Reject a borrow request (SportsHead action). Returns error or null on success.
     */
    public String rejectRequest(String requestId, String reason) {
        try {
            if (requestId == null || requestId.isEmpty()) return "Invalid request ID.";
            BorrowRequest request = db.findBorrowRequest(requestId);
            if (request == null) return "Request not found.";
            if (request.getStatus() != BorrowStatus.PENDING) return "Request is no longer pending.";

            request.setStatus(BorrowStatus.REJECTED);

            String safeReason = (reason != null && !reason.trim().isEmpty()) ? reason.trim() : "No reason provided";
            if (request.getBorrower() != null) {
                db.saveNotification(new Notification(request.getBorrower(),
                        "Your request for " + request.getEquipmentName() + " was rejected. Reason: " + safeReason));
                inventory.notifyObservers("BORROW_REJECTED", request.getBorrower());
            }

            db.recordAudit("BORROW_REJECTED", "SPORTS_HEAD", "Rejected: " + requestId + " — " + safeReason);
            return null;
        } catch (Exception e) {
            return "Error rejecting request.";
        }
    }

    /** Get active borrows for a user — always returns safe list. */
    public List<BorrowRequest> getActiveBorrows(User user) {
        try {
            return db.getActiveBorrowsByUser(user);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /** Get all borrow history for a user. */
    public List<BorrowRequest> getBorrowHistory(User user) {
        try {
            return db.getBorrowRequestsByUser(user);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /** Get pending requests (for SportsHead dashboard). */
    public List<BorrowRequest> getPendingRequests() {
        try {
            return db.getPendingRequests();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /** Check if user has any overdue items — safe, returns false on error. */
    public boolean hasOverdue(User user) {
        try {
            if (user == null) return false;
            List<BorrowRequest> active = db.getActiveBorrowsByUser(user);
            if (active == null || active.isEmpty()) return false;
            return active.stream().anyMatch(r -> r != null && r.isOverdue());
        } catch (Exception e) {
            return false;
        }
    }

    /** Notify all sports heads about an event. */
    private void notifySportsHead(String message) {
        try {
            List<User> allUsers = db.getAllUsers();
            if (allUsers != null) {
                for (User u : allUsers) {
                    if (u != null && u.getRole() == com.sems.model.enums.UserRole.SPORTS_HEAD) {
                        db.saveNotification(new Notification(u, message));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[BorrowService] Notification error: " + e.getMessage());
        }
    }
}
