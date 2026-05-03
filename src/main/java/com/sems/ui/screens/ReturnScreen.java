// File: ui/screens/ReturnScreen.java — Maps to: UC-07, UC-16, FR8, FR17
// Per GUI_LAYOUT_AND_COMPONENT_SPEC.md Screen 4 wireframe
// Active borrows list + condition radio group + notes + submit
// Crash-free: every interaction wrapped, all inputs validated
package com.sems.ui.screens;

import com.sems.model.BorrowRequest;
import com.sems.model.User;
import com.sems.model.enums.BorrowStatus;
import com.sems.model.enums.ConditionStatus;
import com.sems.service.BorrowService;
import com.sems.service.ReturnService;
import com.sems.singleton.SessionManager;
import com.sems.ui.components.AlertBanner;
import com.sems.ui.components.StatusBadge;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class ReturnScreen {

    private final BorrowService borrowService;
    private final ReturnService returnService;
    private final Runnable onBack;
    private VBox root;
    private VBox borrowListContainer;
    private VBox conditionForm;
    private Label returningLabel;
    private ToggleGroup conditionGroup;
    private TextArea notesArea;
    private Label notesError;
    private Button submitBtn;
    private VBox bannerContainer;
    private BorrowRequest selectedBorrow;

    public ReturnScreen(Runnable onBack) {
        this.borrowService = new BorrowService();
        this.returnService = new ReturnService();
        this.onBack = onBack;
        buildUI();
    }

    private void buildUI() {
        try {
            root = new VBox(20);
            root.setPadding(new Insets(24));
            root.setStyle("-fx-background-color: #f8f7f9;");

            // Header
            HBox header = new HBox(12);
            header.setAlignment(Pos.CENTER_LEFT);
            Button backBtn = new Button("← Back");
            backBtn.getStyleClass().add("btn-secondary");
            backBtn.setOnAction(e -> { try { if (onBack != null) onBack.run(); } catch (Exception ex) { /* safe */ } });
            Label title = new Label("Return Equipment");
            title.getStyleClass().add("heading-2");
            header.getChildren().addAll(backBtn, title);

            // Banner container
            bannerContainer = new VBox(8);

            // Active borrows list
            Label borrowsTitle = new Label("Your Active Borrows");
            borrowsTitle.getStyleClass().add("heading-3");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f8f7f9;");

            borrowListContainer = new VBox(8);
            borrowListContainer.setPadding(new Insets(8));
            scrollPane.setContent(borrowListContainer);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            loadActiveBorrows();

            // Condition form (hidden initially)
            conditionForm = buildConditionForm();
            conditionForm.setVisible(false);
            conditionForm.setManaged(false);

            root.getChildren().addAll(header, bannerContainer, borrowsTitle, scrollPane, new Separator(), conditionForm);
        } catch (Exception e) {
            System.err.println("[ReturnScreen] Build error: " + e.getMessage());
            root = new VBox(new Label("Return screen failed to load."));
        }
    }

    private void loadActiveBorrows() {
        try {
            borrowListContainer.getChildren().clear();

            User user = SessionManager.getInstance().getCurrentUser();
            if (user == null) {
                borrowListContainer.getChildren().add(new Label("Please log in to view borrows."));
                return;
            }

            List<BorrowRequest> activeBorrows = borrowService.getActiveBorrows(user);
            if (activeBorrows == null || activeBorrows.isEmpty()) {
                borrowListContainer.getChildren().add(new Label("No active borrows to return."));
                return;
            }

            for (BorrowRequest req : activeBorrows) {
                if (req == null) continue;
                borrowListContainer.getChildren().add(createBorrowRow(req));
            }
        } catch (Exception e) {
            borrowListContainer.getChildren().add(new Label("Error loading active borrows."));
        }
    }

    private HBox createBorrowRow(BorrowRequest req) {
        HBox row = new HBox(12);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(12));
        row.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("📦");
        icon.setStyle("-fx-font-size: 20px;");

        VBox info = new VBox(4);
        Label nameLabel = new Label(req.getEquipmentName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2b2d42;");
        Label dueLabel = new Label("Due: " + req.getReturnDate());
        dueLabel.getStyleClass().add("caption");
        info.getChildren().addAll(nameLabel, dueLabel);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Status badge
        String badgeText = req.isOverdue() ? "OVERDUE" : "ACTIVE";
        StatusBadge badge = new StatusBadge(badgeText);

        Button selectBtn = new Button("Return This");
        selectBtn.getStyleClass().add("btn-primary");
        selectBtn.setStyle("-fx-font-size: 12px; -fx-padding: 6 16;");
        selectBtn.setOnAction(e -> {
            try {
                selectBorrow(req);
            } catch (Exception ex) {
                showBanner("Error selecting item.", AlertBanner.Type.ERROR);
            }
        });

        row.getChildren().addAll(icon, info, badge, selectBtn);
        return row;
    }

    private void selectBorrow(BorrowRequest req) {
        try {
            this.selectedBorrow = req;
            if (returningLabel != null) {
                returningLabel.setText(req != null ? req.getEquipmentName() : "None");
            }
            conditionForm.setVisible(true);
            conditionForm.setManaged(true);
            clearBanner();

            if (req != null && req.isOverdue()) {
                showBanner("⚠ This item is overdue. Please return it immediately.", AlertBanner.Type.WARNING);
            }
        } catch (Exception e) {
            showBanner("Error selecting item.", AlertBanner.Type.ERROR);
        }
    }

    private VBox buildConditionForm() {
        VBox form = new VBox(12);
        form.getStyleClass().add("card");
        form.setPadding(new Insets(20));

        Label formTitle = new Label("Condition Report");
        formTitle.getStyleClass().add("heading-3");

        Label retLabel = new Label("Returning:");
        returningLabel = new Label("None");
        returningLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0080c8;");

        Label condLabel = new Label("Equipment Condition *");
        condLabel.getStyleClass().add("body-text");

        // Radio buttons for condition
        conditionGroup = new ToggleGroup();
        RadioButton goodBtn = new RadioButton("Good Condition");
        goodBtn.setToggleGroup(conditionGroup);
        goodBtn.setUserData(ConditionStatus.GOOD);
        goodBtn.setSelected(true);

        RadioButton inspectBtn = new RadioButton("Needs Inspection");
        inspectBtn.setToggleGroup(conditionGroup);
        inspectBtn.setUserData(ConditionStatus.NEEDS_INSPECTION);

        RadioButton minorBtn = new RadioButton("Minor Repair");
        minorBtn.setToggleGroup(conditionGroup);
        minorBtn.setUserData(ConditionStatus.MINOR_REPAIR);

        RadioButton majorBtn = new RadioButton("Major Repair");
        majorBtn.setToggleGroup(conditionGroup);
        majorBtn.setUserData(ConditionStatus.MAJOR_REPAIR);

        GridPane radioGrid = new GridPane();
        radioGrid.setHgap(24);
        radioGrid.setVgap(8);
        radioGrid.add(goodBtn, 0, 0);
        radioGrid.add(inspectBtn, 1, 0);
        radioGrid.add(minorBtn, 0, 1);
        radioGrid.add(majorBtn, 1, 1);

        // Notes area
        Label notesLabel = new Label("Notes (required if damaged)");
        notesLabel.getStyleClass().add("body-text");
        notesArea = new TextArea();
        notesArea.setPromptText("Describe any damage observed...");
        notesArea.setPrefRowCount(3);
        notesArea.setMaxWidth(500);
        notesArea.setDisable(true);

        notesError = new Label();
        notesError.getStyleClass().add("error-text");
        notesError.setVisible(false);
        notesError.setManaged(false);

        // Enable/disable notes based on condition
        conditionGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            try {
                if (newVal != null && newVal.getUserData() instanceof ConditionStatus cs) {
                    boolean needsNote = cs != ConditionStatus.GOOD;
                    notesArea.setDisable(!needsNote);
                    if (!needsNote) notesArea.clear();
                }
            } catch (Exception e) { /* safe */ }
        });

        // Submit and Cancel
        submitBtn = new Button("SUBMIT RETURN");
        submitBtn.getStyleClass().add("btn-primary");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-secondary");
        cancelBtn.setOnAction(e -> {
            try {
                conditionForm.setVisible(false);
                conditionForm.setManaged(false);
                selectedBorrow = null;
                clearBanner();
            } catch (Exception ex) { /* safe */ }
        });

        HBox actionRow = new HBox(12, submitBtn, cancelBtn);

        submitBtn.setOnAction(e -> handleSubmitReturn());

        form.getChildren().addAll(formTitle, retLabel, returningLabel, new Separator(),
                condLabel, radioGrid, notesLabel, notesArea, notesError, actionRow);
        return form;
    }

    private void handleSubmitReturn() {
        try {
            notesError.setVisible(false);
            notesError.setManaged(false);

            if (selectedBorrow == null) {
                showBanner("Please select an item to return.", AlertBanner.Type.ERROR);
                return;
            }

            // Get condition
            ConditionStatus condition = ConditionStatus.GOOD;
            Toggle selected = conditionGroup.getSelectedToggle();
            if (selected != null && selected.getUserData() instanceof ConditionStatus cs) {
                condition = cs;
            }

            // Validate notes for damaged items
            String notes = notesArea.getText();
            if (condition.requiresAttention() && (notes == null || notes.trim().isEmpty())) {
                notesError.setText("Please describe the damage observed.");
                notesError.setVisible(true);
                notesError.setManaged(true);
                return;
            }

            // Prevent double-click
            submitBtn.setDisable(true);
            submitBtn.setText("Processing...");

            String error = returnService.processReturn(selectedBorrow.getRequestId(), condition, notes);

            if (error != null) {
                showBanner(error, AlertBanner.Type.ERROR);
            } else {
                String msg = "Return processed successfully. Equipment marked as " + condition.getDisplayName() + ".";
                showBanner(msg, AlertBanner.Type.SUCCESS);
                conditionForm.setVisible(false);
                conditionForm.setManaged(false);
                selectedBorrow = null;
                loadActiveBorrows();
            }

            submitBtn.setDisable(false);
            submitBtn.setText("SUBMIT RETURN");
        } catch (Exception e) {
            System.err.println("[ReturnScreen] Submit error: " + e.getMessage());
            showBanner("An error occurred. Please try again.", AlertBanner.Type.ERROR);
            if (submitBtn != null) { submitBtn.setDisable(false); submitBtn.setText("SUBMIT RETURN"); }
        }
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
        try { if (bannerContainer != null) bannerContainer.getChildren().clear(); } catch (Exception e) { /* safe */ }
    }

    public VBox getRoot() {
        return root != null ? root : new VBox(new Label("Error loading return screen."));
    }
}
