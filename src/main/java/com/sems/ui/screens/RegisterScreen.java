// File: ui/screens/RegisterScreen.java — Maps to: UC-01 Registration Sub-flow
// Crash-free: all inputs validated, try-catch on submit
package com.sems.ui.screens;

import com.sems.model.enums.UserRole;
import com.sems.service.AuthService;
import com.sems.ui.components.AlertBanner;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RegisterScreen {

    private final AuthService authService;
    private final Runnable onBackToLogin;
    private VBox root;

    public RegisterScreen(Runnable onBackToLogin) {
        this.authService = new AuthService();
        this.onBackToLogin = onBackToLogin;
        buildUI();
    }

    private void buildUI() {
        try {
            Label title = new Label("CREATE ACCOUNT");
            title.getStyleClass().add("heading-1");

            Label subtitle = new Label("Register as Student or Teacher");
            subtitle.getStyleClass().add("caption");

            TextField nameField = new TextField();
            nameField.setPromptText("Full Name");
            nameField.setMaxWidth(340);

            TextField emailField = new TextField();
            emailField.setPromptText("Email Address");
            emailField.setMaxWidth(340);

            ComboBox<String> roleBox = new ComboBox<>();
            roleBox.getItems().addAll("Student", "Teacher");
            roleBox.setPromptText("Select Role");
            roleBox.setMaxWidth(340);

            PasswordField passField = new PasswordField();
            passField.setPromptText("Password (min 8 characters)");
            passField.setMaxWidth(340);

            PasswordField confirmField = new PasswordField();
            confirmField.setPromptText("Confirm Password");
            confirmField.setMaxWidth(340);

            VBox bannerContainer = new VBox();
            bannerContainer.setMaxWidth(340);

            Button registerBtn = new Button("REGISTER");
            registerBtn.getStyleClass().add("btn-primary");
            registerBtn.setMaxWidth(340);

            registerBtn.setOnAction(e -> {
                try {
                    bannerContainer.getChildren().clear();

                    // Parse role safely
                    UserRole role = null;
                    String selectedRole = roleBox.getValue();
                    if (selectedRole != null) {
                        role = UserRole.fromString(selectedRole.toUpperCase());
                    }

                    String error = authService.register(
                            nameField.getText(),
                            emailField.getText(),
                            passField.getText(),
                            confirmField.getText(),
                            role
                    );

                    if (error != null) {
                        bannerContainer.getChildren().add(new AlertBanner(error, AlertBanner.Type.ERROR));
                    } else {
                        bannerContainer.getChildren().add(
                                new AlertBanner("Registration successful! You can now sign in.", AlertBanner.Type.SUCCESS));
                        // Clear fields
                        nameField.clear();
                        emailField.clear();
                        roleBox.setValue(null);
                        passField.clear();
                        confirmField.clear();
                    }
                } catch (Exception ex) {
                    bannerContainer.getChildren().add(
                            new AlertBanner("Registration failed. Please try again.", AlertBanner.Type.ERROR));
                }
            });

            Hyperlink backLink = new Hyperlink("← Back to Sign In");
            backLink.setOnAction(e -> {
                try { if (onBackToLogin != null) onBackToLogin.run(); } catch (Exception ex) { /* safe */ }
            });

            VBox card = new VBox(12);
            card.getStyleClass().add("login-card");
            card.setPadding(new Insets(40));
            card.setMaxWidth(420);
            card.setAlignment(Pos.CENTER);
            card.getChildren().addAll(
                    title, subtitle, new Separator(),
                    nameField, emailField, roleBox,
                    passField, confirmField,
                    registerBtn, bannerContainer, backLink
            );

            root = new VBox();
            root.getStyleClass().add("login-root");
            root.setAlignment(Pos.CENTER);
            root.getChildren().add(card);
        } catch (Exception e) {
            root = new VBox(new Label("Registration screen failed to load."));
            root.setAlignment(Pos.CENTER);
        }
    }

    public VBox getRoot() {
        return root != null ? root : new VBox(new Label("Error loading register screen."));
    }
}
