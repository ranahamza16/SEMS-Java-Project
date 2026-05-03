package com.sems.ui.screens;

import com.sems.factory.EquipmentFactory;
import com.sems.model.Equipment;
import com.sems.model.enums.EquipmentStatus;
import com.sems.service.EquipmentService;
import com.sems.ui.components.AlertBanner;
import com.sems.ui.components.StatusBadge;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.util.function.Consumer;

public class EquipmentManagementScreen {

    private final EquipmentService equipmentService;
    private final Consumer<String> navigateTo;
    private VBox root;
    private VBox listContainer;
    private VBox bannerContainer;
    
    // Form fields
    private TextField nameField;
    private TextField categoryField;
    private Spinner<Integer> qtySpinner;
    private ComboBox<EquipmentStatus> statusCombo;

    public EquipmentManagementScreen(Consumer<String> navigateTo) {
        this.equipmentService = new EquipmentService();
        this.navigateTo = navigateTo;
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
            backBtn.setOnAction(e -> safeNavigate("dashboard"));
            Label title = new Label("Equipment Management");
            title.getStyleClass().add("heading-2");
            header.getChildren().addAll(backBtn, title);

            bannerContainer = new VBox(8);

            // Add Equipment Form
            VBox addForm = buildAddForm();

            // Equipment List
            Label listTitle = new Label("📦 All Equipment");
            listTitle.getStyleClass().add("heading-3");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f8f7f9;");
            
            listContainer = new VBox(8);
            listContainer.setPadding(new Insets(8));
            scrollPane.setContent(listContainer);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            root.getChildren().addAll(header, bannerContainer, addForm, listTitle, scrollPane);

            loadEquipment();
        } catch (Exception e) {
            System.err.println("[EquipmentManagementScreen] Build error: " + e.getMessage());
            root = new VBox(new Label("Equipment Management screen failed to load."));
        }
    }

    private VBox buildAddForm() {
        VBox form = new VBox(12);
        form.getStyleClass().add("card");
        form.setPadding(new Insets(16));

        Label formTitle = new Label("Add New Equipment");
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2b2d42;");

        HBox row1 = new HBox(12);
        row1.setAlignment(Pos.CENTER_LEFT);
        
        nameField = new TextField();
        nameField.setPromptText("Equipment Name");
        nameField.setPrefWidth(200);

        categoryField = new TextField();
        categoryField.setPromptText("Category");
        categoryField.setPrefWidth(150);

        qtySpinner = new Spinner<>(1, 100, 1);
        qtySpinner.setPrefWidth(80);

        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(EquipmentStatus.values());
        statusCombo.setValue(EquipmentStatus.AVAILABLE);

        Button addBtn = new Button("➕ Add Equipment");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> handleAddEquipment());

        row1.getChildren().addAll(
            new Label("Name:"), nameField,
            new Label("Cat:"), categoryField,
            new Label("Qty:"), qtySpinner,
            new Label("Status:"), statusCombo,
            addBtn
        );

        form.getChildren().addAll(formTitle, row1);
        return form;
    }

    private void handleAddEquipment() {
        try {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            int qty = qtySpinner.getValue();
            EquipmentStatus status = statusCombo.getValue();

            if (name.isEmpty() || category.isEmpty()) {
                showBanner("Name and category are required.", AlertBanner.Type.ERROR);
                return;
            }

            Equipment eq = EquipmentFactory.create(name, category, qty, status);
            if (equipmentService.addEquipment(eq)) {
                showBanner("Equipment added successfully.", AlertBanner.Type.SUCCESS);
                nameField.clear();
                categoryField.clear();
                qtySpinner.getValueFactory().setValue(1);
                loadEquipment();
            } else {
                showBanner("Failed to add equipment.", AlertBanner.Type.ERROR);
            }
        } catch (Exception e) {
            showBanner("Error adding equipment.", AlertBanner.Type.ERROR);
        }
    }

    private void loadEquipment() {
        try {
            listContainer.getChildren().clear();
            List<Equipment> items = equipmentService.getAllEquipment();
            
            if (items == null || items.isEmpty()) {
                listContainer.getChildren().add(new Label("No equipment found."));
                return;
            }

            for (Equipment eq : items) {
                if (eq == null) continue;
                listContainer.getChildren().add(createEquipmentRow(eq));
            }
        } catch (Exception e) {
            listContainer.getChildren().add(new Label("Error loading equipment."));
        }
    }

    private HBox createEquipmentRow(Equipment eq) {
        HBox row = new HBox(16);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(12));
        row.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(eq.getEquipmentName() + " (" + eq.getCategory().getName() + ")");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2b2d42; -fx-font-size: 14px;");
        nameLabel.setPrefWidth(250);

        Label qtyLabel = new Label("Qty: " + eq.getQuantity());
        qtyLabel.setStyle("-fx-text-fill: #2b2d42;");
        qtyLabel.setPrefWidth(60);

        StatusBadge badge = new StatusBadge(eq.getStatus().getDisplayName());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button retireBtn = new Button("Retire/Delete");
        retireBtn.getStyleClass().add("btn-danger");
        retireBtn.setDisable(eq.getStatus() == EquipmentStatus.RETIRED);
        retireBtn.setOnAction(e -> {
            try {
                if (equipmentService.retireEquipment(eq.getEquipmentId())) {
                    showBanner(eq.getEquipmentName() + " retired.", AlertBanner.Type.SUCCESS);
                    loadEquipment();
                } else {
                    showBanner("Failed to retire equipment.", AlertBanner.Type.ERROR);
                }
            } catch (Exception ex) {
                showBanner("Error retiring equipment.", AlertBanner.Type.ERROR);
            }
        });

        row.getChildren().addAll(nameLabel, qtyLabel, badge, spacer, retireBtn);
        return row;
    }

    private void showBanner(String msg, AlertBanner.Type type) {
        try {
            if (bannerContainer != null) {
                bannerContainer.getChildren().clear();
                bannerContainer.getChildren().add(new AlertBanner(msg, type));
            }
        } catch (Exception e) { /* safe */ }
    }

    private void safeNavigate(String target) {
        try {
            if (navigateTo != null && target != null) navigateTo.accept(target);
        } catch (Exception e) {
            System.err.println("[EquipmentManagementScreen] Nav error: " + e.getMessage());
        }
    }

    public VBox getRoot() {
        return root != null ? root : new VBox(new Label("Error loading Equipment Management screen."));
    }
}
