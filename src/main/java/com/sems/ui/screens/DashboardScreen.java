// File: ui/screens/DashboardScreen.java — Maps to: UC-10, FR14
// Per GUI_LAYOUT_AND_COMPONENT_SPEC.md Screen 2 wireframe
// BorderPane: sidebar left, topbar top, content center
// Crash-free: all data loading wrapped in try-catch, safe defaults
package com.sems.ui.screens;

import com.sems.model.*;
import com.sems.model.enums.BorrowStatus;
import com.sems.model.enums.UserRole;
import com.sems.service.*;
import com.sems.singleton.SessionManager;
import com.sems.ui.components.AlertBanner;
import com.sems.ui.components.StatusBadge;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DashboardScreen {

    private final DashboardService dashboardService;
    private final BorrowService borrowService;
    private final NotificationService notificationService;
    private final AuthService authService;
    private final Consumer<String> navigateTo;
    private BorderPane root;

    public DashboardScreen(Consumer<String> navigateTo) {
        this.dashboardService = new DashboardService();
        this.borrowService = new BorrowService();
        this.notificationService = new NotificationService();
        this.authService = new AuthService();
        this.navigateTo = navigateTo;
        buildUI();
    }

    private void buildUI() {
        try {
            root = new BorderPane();
            root.setStyle("-fx-background-color: #f8f7f9;");

            // === SIDEBAR ===
            VBox sidebar = buildSidebar();
            root.setLeft(sidebar);

            // === TOP BAR ===
            HBox topBar = buildTopBar();
            root.setTop(topBar);

            // === CONTENT ===
            ScrollPane contentScroll = new ScrollPane();
            contentScroll.setFitToWidth(true);
            contentScroll.getStyleClass().add("scroll-pane");

            VBox content = buildContent();
            contentScroll.setContent(content);
            root.setCenter(contentScroll);
        } catch (Exception e) {
            System.err.println("[DashboardScreen] Build error: " + e.getMessage());
            root = new BorderPane(new Label("Dashboard failed to load. Please restart."));
        }
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(16, 8, 16, 8));

        Label brand = new Label("■ SEMS");
        brand.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 0 0 16 8;");

        Button dashBtn = createSidebarButton("🏠  Dashboard", true);
        Button equipBtn = createSidebarButton("📦  Equipment", false);
        if (SessionManager.getInstance().isAdmin()) {
            equipBtn.setOnAction(e -> safeNavigate("equipmentManagement"));
        } else {
            equipBtn.setOnAction(e -> safeNavigate("borrow"));
        }

        Button borrowBtn = createSidebarButton("📋  My Borrows", false);
        borrowBtn.setOnAction(e -> safeNavigate("myBorrows"));

        Button returnBtn = createSidebarButton("📤  Return", false);
        returnBtn.setOnAction(e -> safeNavigate("return"));

        Button historyBtn = createSidebarButton("📊  History", false);
        historyBtn.setOnAction(e -> safeNavigate("myBorrows")); // Route history to myBorrows for now

        // Admin-only section
        VBox adminSection = new VBox(4);
        if (SessionManager.getInstance().isAdmin()) {
            Label adminLabel = new Label("ADMIN");
            adminLabel.setStyle("-fx-text-fill: #92dce5; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 16 0 4 8;");
            Button pendingBtn = createSidebarButton("⏳  Pending Requests", false);
            pendingBtn.setOnAction(e -> safeNavigate("dashboard"));
            adminSection.getChildren().addAll(adminLabel, pendingBtn);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("🚪  Logout");
        logoutBtn.getStyleClass().addAll("sidebar-btn");
        logoutBtn.setStyle("-fx-text-fill: #EF9A9A;");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> {
            try {
                authService.logout();
                safeNavigate("login");
            } catch (Exception ex) {
                safeNavigate("login");
            }
        });

        sidebar.getChildren().addAll(brand, dashBtn, equipBtn, borrowBtn, returnBtn, historyBtn, adminSection, spacer, logoutBtn);
        return sidebar;
    }

    private Button createSidebarButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-btn");
        if (active) btn.getStyleClass().add("sidebar-btn-active");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private HBox buildTopBar() {
        HBox topBar = new HBox();
        topBar.getStyleClass().add("topbar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPrefHeight(64);
        topBar.setSpacing(16);

        Label searchLabel = new Label("Search...");
        searchLabel.setStyle("-fx-text-fill: #90A4AE; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Notification badge
        long unread = 0;
        try {
            User user = SessionManager.getInstance().getCurrentUser();
            if (user != null) unread = notificationService.getUnreadCount(user);
        } catch (Exception e) { /* safe default */ }

        Button notifBadge = new Button("🔔 " + unread);
        notifBadge.setStyle("-fx-font-size: 14px; -fx-text-fill: #2b2d42; -fx-padding: 8; -fx-background-color: transparent; -fx-cursor: hand;");
        notifBadge.setOnAction(e -> {
            try {
                User u = SessionManager.getInstance().getCurrentUser();
                if (u != null) {
                    notificationService.markAllAsRead(u);
                    safeNavigate("dashboard");
                }
            } catch (Exception ex) { }
        });

        // User info
        String userName = "User";
        String userRole = "";
        try {
            User user = SessionManager.getInstance().getCurrentUser();
            if (user != null) {
                userName = user.getName();
                userRole = user.getRole() != null ? user.getRole().getDisplayName() : "";
            }
        } catch (Exception e) { /* safe default */ }

        Label userLabel = new Label("👤 " + userName);
        userLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2b2d42;");

        Label roleLabel = new Label(userRole);
        roleLabel.getStyleClass().add("caption");

        VBox userInfo = new VBox(2, userLabel, roleLabel);
        userInfo.setAlignment(Pos.CENTER_RIGHT);

        topBar.getChildren().addAll(searchLabel, spacer, notifBadge, userInfo);
        return topBar;
    }

    private VBox buildContent() {
        VBox content = new VBox(24);
        content.setPadding(new Insets(24));

        try {
            User user = SessionManager.getInstance().getCurrentUser();
            Map<String, Object> summary = dashboardService.getSummary(user);

            // Header
            Label header = new Label("Dashboard Overview");
            header.getStyleClass().add("heading-2");

            // Stats row
            HBox statsRow = new HBox(16);
            statsRow.getChildren().addAll(
                    createStatCard("📦", "Equipment Available", String.valueOf(summary.getOrDefault("availableEquipment", 0))),
                    createStatCard("📋", "My Active Borrows", String.valueOf(summary.getOrDefault("activeBorrows", 0)))
            );
            if (SessionManager.getInstance().isAdmin()) {
                statsRow.getChildren().add(createStatCard("⏳", "Pending Requests", String.valueOf(summary.getOrDefault("pendingRequests", 0))));
            }
            statsRow.getChildren().add(createStatCard("🔔", "Notifications", String.valueOf(summary.getOrDefault("unreadNotifications", 0L))));

            content.getChildren().addAll(header, statsRow);

            // === PENDING REQUESTS (SportsHead only) ===
            if (SessionManager.getInstance().isAdmin()) {
                content.getChildren().add(buildPendingRequestsSection());
            }

            // === RECENT NOTIFICATIONS ===
            content.getChildren().add(buildNotificationsSection(user));

            // === QUICK ACTIONS ===
            content.getChildren().add(buildQuickActions());

        } catch (Exception e) {
            content.getChildren().add(new AlertBanner("Unable to load dashboard data.", AlertBanner.Type.WARNING));
        }

        return content;
    }

    private VBox createStatCard(String icon, String title, String value) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0080c8;");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("caption");

        VBox card = new VBox(8, iconLabel, valueLabel, titleLabel);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);
        return card;
    }

    private VBox buildPendingRequestsSection() {
        VBox section = new VBox(12);
        try {
            Label sectionTitle = new Label("⏳ Pending Borrow Requests");
            sectionTitle.getStyleClass().add("heading-3");
            section.getChildren().add(sectionTitle);

            List<BorrowRequest> pendingRequests = borrowService.getPendingRequests();
            
            VBox requestsContainer = new VBox(10);
            
            if (pendingRequests == null || pendingRequests.isEmpty()) {
                Label noRequests = new Label("No pending requests");
                noRequests.setStyle("-fx-text-fill: #999;");
                requestsContainer.getChildren().add(noRequests);
            } else {
                for (BorrowRequest request : pendingRequests) {
                    if (request == null) continue;
                    HBox requestCard = createRequestCard(request);
                    requestsContainer.getChildren().add(requestCard);
                }
            }
            section.getChildren().add(requestsContainer);
        } catch (Exception e) {
            section.getChildren().add(new Label("Unable to load pending requests."));
        }
        return section;
    }

    private HBox createRequestCard(BorrowRequest req) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(12));

        Label info = new Label(req.getBorrowerName() + " → " + req.getEquipmentName()
                + "\nRequested: " + req.getRequestDate() + " | Return: " + req.getReturnDate());
        info.setStyle("-fx-font-size: 14px; -fx-text-fill: #2b2d42;");
        info.setWrapText(true);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button approveBtn = new Button("✅ Approve");
        approveBtn.getStyleClass().add("btn-primary");
        approveBtn.setStyle("-fx-font-size: 12px; -fx-padding: 6 12;");

        Button rejectBtn = new Button("❌ Reject");
        rejectBtn.getStyleClass().add("btn-danger");
        rejectBtn.setStyle("-fx-font-size: 12px; -fx-padding: 6 12;");

        approveBtn.setOnAction(e -> {
            try {
                approveBtn.setDisable(true);
                rejectBtn.setDisable(true);
                String err = borrowService.approveRequest(req.getRequestId());
                if (err != null) {
                    info.setText("Error: " + err);
                } else {
                    info.setText("✅ Approved: " + req.getEquipmentName());
                    info.setStyle("-fx-text-fill: #2E7D32;");
                }
            } catch (Exception ex) {
                info.setText("Error approving request.");
            }
        });

        rejectBtn.setOnAction(e -> {
            try {
                approveBtn.setDisable(true);
                rejectBtn.setDisable(true);
                String err = borrowService.rejectRequest(req.getRequestId(), "Rejected by admin");
                if (err != null) {
                    info.setText("Error: " + err);
                } else {
                    info.setText("❌ Rejected: " + req.getEquipmentName());
                    info.setStyle("-fx-text-fill: #C62828;");
                }
            } catch (Exception ex) {
                info.setText("Error rejecting request.");
            }
        });

        row.getChildren().addAll(info, approveBtn, rejectBtn);
        return row;
    }

    private VBox buildNotificationsSection(User user) {
        VBox section = new VBox(8);
        try {
            Label sectionTitle = new Label("🔔 Recent Notifications");
            sectionTitle.getStyleClass().add("heading-3");
            section.getChildren().add(sectionTitle);

            List<Notification> notifications = notificationService.getNotifications(user);
            if (notifications == null || notifications.isEmpty()) {
                section.getChildren().add(new Label("No notifications."));
            } else {
                int count = 0;
                for (Notification n : notifications) {
                    if (n == null || count >= 5) continue;
                    HBox item = createNotificationCard(n);
                    section.getChildren().add(item);
                    count++;
                }
            }
        } catch (Exception e) {
            section.getChildren().add(new Label("Unable to load notifications."));
        }
        return section;
    }

    private HBox createNotificationCard(Notification notification) {
        HBox card = new HBox(10);
        card.getStyleClass().add(notification.isRead() ? "notification-item" : "notification-item-unread");
        card.setPadding(new Insets(12));
        
        // Icon
        Label icon = new Label("🔔");
        icon.setStyle("-fx-font-size: 18px;");
        
        // Text
        Label message = new Label(notification.getMessage());
        message.setStyle("-fx-text-fill: #2b2d42; -fx-font-weight: bold;");
        message.setWrapText(true);
        message.setMaxWidth(500);
        
        // Timestamp
        Label timestamp = new Label(formatTime(notification.getTimestamp()));
        timestamp.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
        
        VBox textContainer = new VBox(5, message, timestamp);
        HBox.setHgrow(textContainer, Priority.ALWAYS);
        card.getChildren().addAll(icon, textContainer);
        
        return card;
    }

    private String formatTime(java.time.LocalDateTime time) {
        if (time == null) return "";
        return time.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }

    private HBox buildQuickActions() {
        HBox actions = new HBox(16);
        actions.setPadding(new Insets(8, 0, 0, 0));

        Button borrowBtn = new Button("📦 Browse Equipment");
        borrowBtn.getStyleClass().add("btn-primary");
        borrowBtn.setOnAction(e -> safeNavigate("borrow"));

        Button returnBtn = new Button("📤 Return Equipment");
        returnBtn.getStyleClass().add("btn-secondary");
        returnBtn.setOnAction(e -> safeNavigate("return"));

        actions.getChildren().addAll(borrowBtn, returnBtn);
        return actions;
    }

    private void safeNavigate(String target) {
        try {
            if (navigateTo != null && target != null) navigateTo.accept(target);
        } catch (Exception e) {
            System.err.println("[DashboardScreen] Navigation error: " + e.getMessage());
        }
    }

    public BorderPane getRoot() {
        return root != null ? root : new BorderPane(new Label("Dashboard error."));
    }
}
