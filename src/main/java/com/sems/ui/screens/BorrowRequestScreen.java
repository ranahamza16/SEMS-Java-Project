// File: ui/screens/BorrowRequestScreen.java — Maps to: UC-05, FR6
// Per GUI_LAYOUT_AND_COMPONENT_SPEC.md Screen 3 wireframe
// Equipment grid + search + borrow form with DatePicker
// Crash-free: every interaction wrapped in try-catch
package com.sems.ui.screens;

import com.sems.model.Equipment;
import com.sems.model.User;
import com.sems.service.BorrowService;
import com.sems.service.EquipmentService;
import com.sems.singleton.SessionManager;
import com.sems.ui.components.AlertBanner;
import com.sems.ui.components.StatusBadge;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.List;

public class BorrowRequestScreen {

    private final EquipmentService equipmentService;
    private final BorrowService borrowService;
    private final Runnable onBack;
    private VBox root;
    private FlowPane equipmentGrid;
    private VBox borrowForm;
    private Label selectedItemLabel;
    private DatePicker returnDatePicker;
    private VBox bannerContainer;
    private Equipment selectedEquipment;
    private Button submitBtn;

    public BorrowRequestScreen(Runnable onBack) {
        this.equipmentService = new EquipmentService();
        this.borrowService = new BorrowService();
        this.onBack = onBack;
        buildUI();
    }

    private void buildUI() {
        try {
            root = new VBox(16);
            root.setPadding(new Insets(24));
            root.setStyle("-fx-background-color: #f8f7f9;");

            // Header
            HBox header = new HBox(12);
            header.setAlignment(Pos.CENTER_LEFT);
            Button backBtn = new Button("← Back");
            backBtn.getStyleClass().add("btn-secondary");
            backBtn.setOnAction(e -> { try { if (onBack != null) onBack.run(); } catch (Exception ex) { /* safe */ } });
            Label title = new Label("Equipment Borrowing Request");
            title.getStyleClass().add("heading-2");
            header.getChildren().addAll(backBtn, title);

            // Search row
            HBox searchRow = new HBox(8);
            searchRow.setAlignment(Pos.CENTER_LEFT);
            TextField searchField = new TextField();
            searchField.setPromptText("Search equipment...");
            searchField.setPrefWidth(300);
            Button searchBtn = new Button("🔍 Search");
            searchBtn.getStyleClass().add("btn-primary");
            searchBtn.setStyle("-fx-font-size: 12px; -fx-padding: 8 16;");
            Button showAllBtn = new Button("Show All");
            showAllBtn.getStyleClass().add("btn-secondary");
            showAllBtn.setStyle("-fx-font-size: 12px; -fx-padding: 8 16;");
            searchRow.getChildren().addAll(searchField, searchBtn, showAllBtn);

            // Equipment grid inside a ScrollPane
            equipmentGrid = new FlowPane(16, 16);
            equipmentGrid.setPadding(new Insets(8, 0, 8, 0));

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f8f7f9;");
            scrollPane.setContent(equipmentGrid);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            // Banner container for messages
            bannerContainer = new VBox(8);

            // Borrow form (initially hidden)
            borrowForm = buildBorrowForm();
            borrowForm.setVisible(false);
            borrowForm.setManaged(false);

            // Search handlers
            searchBtn.setOnAction(e -> {
                try { loadEquipment(searchField.getText()); } catch (Exception ex) { showBanner("Search failed.", AlertBanner.Type.ERROR); }
            });
            searchField.setOnAction(e -> {
                try { loadEquipment(searchField.getText()); } catch (Exception ex) { showBanner("Search failed.", AlertBanner.Type.ERROR); }
            });
            showAllBtn.setOnAction(e -> {
                try { searchField.clear(); loadEquipment(null); } catch (Exception ex) { /* safe */ }
            });

            root.getChildren().addAll(header, searchRow, bannerContainer, scrollPane, new Separator(), borrowForm);

            // Load initial equipment
            loadEquipment(null);
        } catch (Exception e) {
            System.err.println("[BorrowRequestScreen] Build error: " + e.getMessage());
            root = new VBox(new Label("Borrow screen failed to load."));
        }
    }

    private void loadEquipment(String query) {
        try {
            equipmentGrid.getChildren().clear();
            List<Equipment> items;
            if (query != null && !query.trim().isEmpty()) {
                items = equipmentService.searchEquipment(query.trim());
            } else {
                items = equipmentService.getAllEquipment();
            }

            if (items == null || items.isEmpty()) {
                equipmentGrid.getChildren().add(new Label("No equipment found."));
                return;
            }

            for (Equipment eq : items) {
                if (eq == null) continue;
                equipmentGrid.getChildren().add(createEquipmentCard(eq));
            }
        } catch (Exception e) {
            equipmentGrid.getChildren().add(new Label("Error loading equipment."));
        }
    }

    private VBox createEquipmentCard(Equipment eq) {
        VBox card = new VBox(8);
        card.getStyleClass().add("equipment-card");
        card.setPrefWidth(180);
        card.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(eq.getEquipmentName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2b2d42;");
        nameLabel.setWrapText(true);

        Label categoryLabel = new Label(eq.getCategory().getName());
        categoryLabel.getStyleClass().add("caption");

        StatusBadge badge = new StatusBadge(eq.getStatus().getDisplayName());

        Label qtyLabel = new Label("Qty: " + eq.getQuantity());
        qtyLabel.getStyleClass().add("caption");

        Button borrowBtn = new Button("Borrow");
        borrowBtn.getStyleClass().add("btn-primary");
        borrowBtn.setStyle("-fx-font-size: 12px; -fx-padding: 6 16;");
        borrowBtn.setDisable(!eq.isAvailable());

        borrowBtn.setOnAction(e -> {
            try {
                selectEquipment(eq);
            } catch (Exception ex) {
                showBanner("Error selecting equipment.", AlertBanner.Type.ERROR);
            }
        });

        card.getChildren().addAll(nameLabel, categoryLabel, badge, qtyLabel, borrowBtn);
        return card;
    }

    private void selectEquipment(Equipment eq) {
        try {
            this.selectedEquipment = eq;
            if (selectedItemLabel != null) {
                selectedItemLabel.setText(eq != null ? eq.getEquipmentName() : "None");
            }
            if (borrowForm != null) {
                borrowForm.setVisible(true);
                borrowForm.setManaged(true);
            }
            clearBanner();

            // Check availability
            if (eq != null && eq.isAvailable()) {
                showBanner("✅ " + eq.getEquipmentName() + " is available. Select a return date.", AlertBanner.Type.SUCCESS);
            } else {
                showBanner("Equipment is currently unavailable.", AlertBanner.Type.WARNING);
            }
        } catch (Exception e) {
            showBanner("Error selecting equipment.", AlertBanner.Type.ERROR);
        }
    }

    private VBox buildBorrowForm() {
        VBox form = new VBox(12);
        form.getStyleClass().add("card");
        form.setPadding(new Insets(20));

        Label formTitle = new Label("Borrow Request Form");
        formTitle.getStyleClass().add("heading-3");

        Label selLabel = new Label("Selected Item:");
        selLabel.getStyleClass().add("body-text");
        selectedItemLabel = new Label("None");
        selectedItemLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0080c8;");

        Label dateLabel = new Label("Return Date:");
        dateLabel.getStyleClass().add("body-text");
        returnDatePicker = new DatePicker();
        returnDatePicker.setPromptText("Select return date");
        returnDatePicker.setValue(LocalDate.now().plusDays(7));

        Label dateError = new Label();
        dateError.getStyleClass().add("error-text");
        dateError.setVisible(false);
        dateError.setManaged(false);

        submitBtn = new Button("SUBMIT REQUEST");
        submitBtn.getStyleClass().add("btn-primary");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-secondary");
        cancelBtn.setOnAction(e -> {
            try {
                borrowForm.setVisible(false);
                borrowForm.setManaged(false);
                selectedEquipment = null;
                clearBanner();
            } catch (Exception ex) { /* safe */ }
        });

        HBox actionRow = new HBox(12, submitBtn, cancelBtn);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        submitBtn.setOnAction(e -> {
            try {
                dateError.setVisible(false);
                dateError.setManaged(false);

                // Validate
                if (selectedEquipment == null) {
                    showBanner("Please select equipment first.", AlertBanner.Type.ERROR);
                    return;
                }

                LocalDate returnDate = returnDatePicker.getValue();
                if (returnDate == null) {
                    dateError.setText("Please select a return date.");
                    dateError.setVisible(true);
                    dateError.setManaged(true);
                    return;
                }

                if (!returnDate.isAfter(LocalDate.now())) {
                    dateError.setText("Return date must be in the future.");
                    dateError.setVisible(true);
                    dateError.setManaged(true);
                    return;
                }

                // Prevent double-click
                submitBtn.setDisable(true);
                submitBtn.setText("Submitting...");

                User currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser == null) {
                    showBanner("Session expired. Please log in again.", AlertBanner.Type.ERROR);
                    submitBtn.setDisable(false);
                    submitBtn.setText("SUBMIT REQUEST");
                    return;
                }

                String error = borrowService.submitRequest(currentUser, selectedEquipment, returnDate);
                if (error != null) {
                    showBanner(error, AlertBanner.Type.ERROR);
                    submitBtn.setDisable(false);
                    submitBtn.setText("SUBMIT REQUEST");
                } else {
                    showBanner("Request submitted successfully! Awaiting approval.", AlertBanner.Type.SUCCESS);
                    borrowForm.setVisible(false);
                    borrowForm.setManaged(false);
                    selectedEquipment = null;
                    loadEquipment(null);
                    submitBtn.setDisable(false);
                    submitBtn.setText("SUBMIT REQUEST");
                }
            } catch (Exception ex) {
                System.err.println("[BorrowRequestScreen] Submit error: " + ex.getMessage());
                showBanner("An error occurred. Please try again.", AlertBanner.Type.ERROR);
                if (submitBtn != null) { submitBtn.setDisable(false); submitBtn.setText("SUBMIT REQUEST"); }
            }
        });

        form.getChildren().addAll(formTitle, selLabel, selectedItemLabel, dateLabel, returnDatePicker, dateError, actionRow);
        return form;
    }

    private void showBanner(String msg, AlertBanner.Type type) {
        try {
            if (bannerContainer != null) {
                bannerContainer.getChildren().clear();
                bannerContainer.getChildren().add(new AlertBanner(msg, type));
            }
        } catch (Exception e) { /* safe */ }
    }

    private void clearBanner() {
        try {
            if (bannerContainer != null) bannerContainer.getChildren().clear();
        } catch (Exception e) { /* safe */ }
    }

    public VBox getRoot() {
        return root != null ? root : new VBox(new Label("Error loading borrow screen."));
    }
}
