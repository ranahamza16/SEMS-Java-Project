package com.sems.ui.screens;

import com.sems.model.BorrowRequest;
import com.sems.model.User;
import com.sems.service.BorrowService;
import com.sems.singleton.SessionManager;
import com.sems.ui.components.AlertBanner;
import com.sems.ui.components.StatusBadge;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.util.function.Consumer;

public class MyBorrowsScreen {

    private final BorrowService borrowService;
    private final Consumer<String> navigateTo;
    private VBox root;
    private VBox borrowList;
    private VBox bannerContainer;

    public MyBorrowsScreen(Consumer<String> navigateTo) {
        this.borrowService = new BorrowService();
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
            Label title = new Label("My Borrows");
            title.getStyleClass().add("heading-2");
            header.getChildren().addAll(backBtn, title);

            // Banner container
            bannerContainer = new VBox(8);

            // Active Borrows Section
            Label sectionTitle = new Label("📋 Active Borrows");
            sectionTitle.getStyleClass().add("heading-3");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f8f7f9;");
            
            borrowList = new VBox(12);
            borrowList.setPadding(new Insets(8));
            scrollPane.setContent(borrowList);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            root.getChildren().addAll(header, bannerContainer, sectionTitle, scrollPane);

            loadBorrows();
        } catch (Exception e) {
            System.err.println("[MyBorrowsScreen] Build error: " + e.getMessage());
            root = new VBox(new Label("My Borrows screen failed to load."));
        }
    }

    private void loadBorrows() {
        try {
            borrowList.getChildren().clear();
            User user = SessionManager.getInstance().getCurrentUser();
            if (user == null) {
                showBanner("Session expired. Please log in.", AlertBanner.Type.ERROR);
                return;
            }

            List<BorrowRequest> activeBorrows = borrowService.getActiveBorrows(user);
            if (activeBorrows == null || activeBorrows.isEmpty()) {
                borrowList.getChildren().add(new Label("You have no active borrows."));
                return;
            }

            for (BorrowRequest req : activeBorrows) {
                if (req == null) continue;
                borrowList.getChildren().add(createBorrowCard(req));
            }
        } catch (Exception e) {
            borrowList.getChildren().add(new Label("Error loading active borrows."));
        }
    }

    private VBox createBorrowCard(BorrowRequest req) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16));

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(req.getEquipmentName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2b2d42;");
        
        StatusBadge badge = new StatusBadge(req.getStatus().getDisplayName());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button returnBtn = new Button("📤 Return");
        returnBtn.getStyleClass().add("btn-primary");
        returnBtn.setOnAction(e -> safeNavigate("return"));

        topRow.getChildren().addAll(nameLabel, badge, spacer, returnBtn);

        HBox detailsRow = new HBox(24);
        Label borrowDate = new Label("Borrowed: " + req.getRequestDate());
        borrowDate.setStyle("-fx-text-fill: #2b2d42;");
        
        Label dueDate = new Label("Due: " + req.getReturnDate());
        if (req.isOverdue()) {
            dueDate.setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;");
            dueDate.setText(dueDate.getText() + " (OVERDUE)");
        } else {
            dueDate.setStyle("-fx-text-fill: #2b2d42;");
        }

        detailsRow.getChildren().addAll(borrowDate, dueDate);

        card.getChildren().addAll(topRow, detailsRow);
        return card;
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
            System.err.println("[MyBorrowsScreen] Navigation error: " + e.getMessage());
        }
    }

    public VBox getRoot() {
        return root != null ? root : new VBox(new Label("Error loading My Borrows screen."));
    }
}
