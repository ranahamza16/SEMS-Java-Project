// File: singleton/DatabaseManager.java — Maps to: All UCs, NFR7
// Singleton Pattern: in-memory HashMap-based data store
// Crash-free: all operations null-safe, mock data pre-seeded on init
package com.sems.singleton;

import com.sems.factory.EquipmentFactory;
import com.sems.factory.UserFactory;
import com.sems.model.*;
import com.sems.model.enums.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Central in-memory data store. Thread-safe via ConcurrentHashMap.
 * Pre-seeds mock data on first instantiation.
 */
public class DatabaseManager {

    private static volatile DatabaseManager instance;

    private final Map<String, User> users;
    private final Map<String, Equipment> equipment;
    private final Map<String, BorrowRequest> borrowRequests;
    private final Map<String, ReturnRecord> returnRecords;
    private final Map<String, Notification> notifications;
    private final Map<String, DamageReport> damageReports;
    private final List<AuditLog> auditLogs;
    private boolean initialized;

    private DatabaseManager() {
        this.users = new ConcurrentHashMap<>();
        this.equipment = new ConcurrentHashMap<>();
        this.borrowRequests = new ConcurrentHashMap<>();
        this.returnRecords = new ConcurrentHashMap<>();
        this.notifications = new ConcurrentHashMap<>();
        this.damageReports = new ConcurrentHashMap<>();
        this.auditLogs = Collections.synchronizedList(new ArrayList<>());
        this.initialized = false;
        initMockData();
    }

    /** Thread-safe singleton access. */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    /** Reset for testing purposes. */
    public static synchronized void reset() {
        instance = null;
    }

    // ========== MOCK DATA ==========
    private void initMockData() {
        try {
            // Pre-seed users per CORE_USE_CASES doc
            User admin = UserFactory.create(UserRole.SPORTS_HEAD, "Admin User", "admin@sems.edu", "Admin123!");
            User ali = UserFactory.create(UserRole.STUDENT, "Ali Student", "ali@sems.edu", "Student1!");
            User sir = UserFactory.create(UserRole.TEACHER, "Sir Teacher", "sir@sems.edu", "Teacher1!");
            saveUser(admin);
            saveUser(ali);
            saveUser(sir);

            // Pre-seed equipment per CORE_USE_CASES doc
            Equipment football = EquipmentFactory.create("Football", "Ball", 5, EquipmentStatus.AVAILABLE);
            Equipment cricketBat = EquipmentFactory.create("Cricket Bat", "Bat", 3, EquipmentStatus.AVAILABLE);
            Equipment badminton = EquipmentFactory.create("Badminton Racket", "Racket", 2, EquipmentStatus.IN_MAINTENANCE);
            Equipment basketball = EquipmentFactory.create("Basketball", "Ball", 4, EquipmentStatus.AVAILABLE);
            Equipment tableTennis = EquipmentFactory.create("Table Tennis Paddle", "Paddle", 6, EquipmentStatus.AVAILABLE);
            Equipment volleyball = EquipmentFactory.create("Volleyball", "Ball", 3, EquipmentStatus.AVAILABLE);
            saveEquipment(football);
            saveEquipment(cricketBat);
            saveEquipment(badminton);
            saveEquipment(basketball);
            saveEquipment(tableTennis);
            saveEquipment(volleyball);

            // Pre-seed a pending borrow request for demo
            BorrowRequest pendingRequest = new BorrowRequest(ali, football, LocalDate.now().plusDays(3));
            saveBorrowRequest(pendingRequest);
            ali.addBorrowRequest(pendingRequest);

            // Pre-seed an active (overdue) borrow for return demo
            BorrowRequest overdueRequest = new BorrowRequest(ali, cricketBat, LocalDate.now().minusDays(2));
            overdueRequest.setStatus(BorrowStatus.ACTIVE);
            saveBorrowRequest(overdueRequest);
            ali.addBorrowRequest(overdueRequest);

            // Pre-seed notifications
            saveNotification(new Notification(ali, "Welcome to SEMS! You can borrow equipment from the catalog."));
            saveNotification(new Notification(ali, "Your borrow request for Football is pending approval."));
            saveNotification(new Notification(admin, "New borrow request from Ali Student for Football."));

            this.initialized = true;
            recordAudit("SYSTEM_INIT", "SYSTEM", "Mock data loaded successfully");
        } catch (Exception e) {
            System.err.println("[DatabaseManager] Mock data init error (non-fatal): " + e.getMessage());
            this.initialized = true; // Mark initialized even on partial failure
        }
    }

    public boolean isInitialized() { return initialized; }

    // ========== USER OPERATIONS ==========
    public void saveUser(User user) {
        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
            users.put(user.getEmail().toLowerCase(), user);
        }
    }

    public User findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        return users.get(email.trim().toLowerCase());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return users.containsKey(email.trim().toLowerCase());
    }

    // ========== EQUIPMENT OPERATIONS ==========
    public void saveEquipment(Equipment eq) {
        if (eq != null && eq.getEquipmentId() != null) {
            equipment.put(eq.getEquipmentId(), eq);
        }
    }

    public Equipment findEquipmentById(String id) {
        if (id == null) return null;
        return equipment.get(id);
    }

    public List<Equipment> getAllEquipment() {
        return new ArrayList<>(equipment.values());
    }

    public List<Equipment> getAvailableEquipment() {
        try {
            return equipment.values().stream()
                    .filter(eq -> eq != null && eq.isAvailable())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Equipment> searchEquipment(String query) {
        if (query == null || query.trim().isEmpty()) return getAllEquipment();
        String lowerQuery = query.trim().toLowerCase();
        try {
            return equipment.values().stream()
                    .filter(eq -> eq != null
                            && (eq.getEquipmentName().toLowerCase().contains(lowerQuery)
                            || eq.getCategory().getName().toLowerCase().contains(lowerQuery)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ========== BORROW REQUEST OPERATIONS ==========
    public void saveBorrowRequest(BorrowRequest request) {
        if (request != null && request.getRequestId() != null) {
            borrowRequests.put(request.getRequestId(), request);
        }
    }

    public BorrowRequest findBorrowRequest(String requestId) {
        if (requestId == null) return null;
        return borrowRequests.get(requestId);
    }

    public List<BorrowRequest> getAllBorrowRequests() {
        return new ArrayList<>(borrowRequests.values());
    }

    public List<BorrowRequest> getBorrowRequestsByUser(User user) {
        if (user == null) return new ArrayList<>();
        try {
            return borrowRequests.values().stream()
                    .filter(r -> r != null && r.getBorrower() != null
                            && r.getBorrower().getUserId().equals(user.getUserId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<BorrowRequest> getActiveBorrowsByUser(User user) {
        if (user == null) return new ArrayList<>();
        try {
            return borrowRequests.values().stream()
                    .filter(r -> r != null && r.getBorrower() != null
                            && r.getBorrower().getUserId().equals(user.getUserId())
                            && (r.getStatus() == BorrowStatus.ACTIVE || r.getStatus() == BorrowStatus.OVERDUE))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<BorrowRequest> getPendingRequests() {
        try {
            return borrowRequests.values().stream()
                    .filter(r -> r != null && r.getStatus() == BorrowStatus.PENDING)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ========== RETURN RECORD OPERATIONS ==========
    public void saveReturnRecord(ReturnRecord record) {
        if (record != null && record.getReturnId() != null) {
            returnRecords.put(record.getReturnId(), record);
        }
    }

    // ========== NOTIFICATION OPERATIONS ==========
    public void saveNotification(Notification notification) {
        if (notification != null && notification.getNotificationId() != null) {
            notifications.put(notification.getNotificationId(), notification);
        }
    }

    public List<Notification> getNotificationsForUser(User user) {
        if (user == null) return new ArrayList<>();
        try {
            return notifications.values().stream()
                    .filter(n -> n != null && n.getRecipient() != null
                            && n.getRecipient().getUserId().equals(user.getUserId()))
                    .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public long getUnreadNotificationCount(User user) {
        if (user == null) return 0;
        try {
            return notifications.values().stream()
                    .filter(n -> n != null && n.getRecipient() != null
                            && n.getRecipient().getUserId().equals(user.getUserId())
                            && !n.isRead())
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    // ========== DAMAGE REPORT OPERATIONS ==========
    public void saveDamageReport(DamageReport report) {
        if (report != null && report.getReportId() != null) {
            damageReports.put(report.getReportId(), report);
        }
    }

    // ========== AUDIT LOG ==========
    public void recordAudit(String action, String userId, String details) {
        try {
            auditLogs.add(new AuditLog(
                    action != null ? action : "UNKNOWN",
                    userId != null ? userId : "SYSTEM",
                    details != null ? details : ""
            ));
        } catch (Exception e) {
            System.err.println("[DatabaseManager] Audit log error: " + e.getMessage());
        }
    }

    public List<AuditLog> getAuditLogs() {
        return new ArrayList<>(auditLogs);
    }
}
