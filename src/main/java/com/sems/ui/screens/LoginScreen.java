// File: ui/screens/LoginScreen.java — Maps to: UC-01, FR1
// Per GUI_LAYOUT_AND_COMPONENT_SPEC.md Screen 1 wireframe
// PALETA 5: #0080c8 button, #f8f7f9 background, #2b2d42 labels
// Crash-free: every click handler wrapped in try-catch, all inputs validated
package com.sems.ui.screens;

import com.sems.model.User;
import com.sems.service.AuthService;
import com.sems.ui.components.AlertBanner;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Login screen with email/password fields and validation.
 * Centered card layout per GUI spec wireframe.
 */
public class LoginScreen {

    private final AuthService authService;
    private final Runnable onLoginSuccess;
    private final Runnable onRegisterClick;
    private VBox root;
    private TextField emailField;
    private PasswordField passwordField;
    private Label emailError;
    private Label passError;
    private Button loginBtn;
    private VBox bannerContainer;

    public LoginScreen(Runnable onLoginSuccess, Runnable onRegisterClick) {
        this.authService = new AuthService();
        this.onLoginSuccess = onLoginSuccess;
        this.onRegisterClick = onRegisterClick;
        buildUI();
    }

    private void buildUI() {
        try {
            // Title
            Label logoLabel = new Label("🏀");
            logoLabel.setStyle("-fx-font-size: 48px;");

            Label titleLabel = new Label("Sports Equipment System");
            titleLabel.getStyleClass().add("heading-1");

            Label subtitleLabel = new Label("SIGN IN");
            subtitleLabel.getStyleClass().add("heading-2");
            subtitleLabel.setStyle("-fx-padding: 8 0 0 0;");

            // Email
            Label emailLabel = new Label("Email Address");
            emailLabel.getStyleClass().add("body-text");
            emailField = new TextField();
            emailField.setPromptText("Enter your email");
            emailField.getStyleClass().add("text-field");
            emailField.setMaxWidth(340);
            emailError = new Label();
            emailError.getStyleClass().add("error-text");
            emailError.setVisible(false);
            emailError.setManaged(false);

            // Password
            Label passLabel = new Label("Password");
            passLabel.getStyleClass().add("body-text");
            passwordField = new PasswordField();
            passwordField.setPromptText("Enter your password");
            passwordField.getStyleClass().add("password-field");
            passwordField.setMaxWidth(340);
            passError = new Label();
            passError.getStyleClass().add("error-text");
            passError.setVisible(false);
            passError.setManaged(false);

            // Login button
            loginBtn = new Button("SIGN IN");
            loginBtn.getStyleClass().add("btn-primary");
            loginBtn.setMaxWidth(340);
            loginBtn.setOnAction(e -> handleLogin());

            // Enter key on password field
            passwordField.setOnAction(e -> handleLogin());
            emailField.setOnAction(e -> passwordField.requestFocus());

            // Register link
            Hyperlink registerLink = new Hyperlink("Don't have an account? Register");
            registerLink.getStyleClass().add("hyperlink");
            registerLink.setOnAction(e -> {
                try {
                    if (onRegisterClick != null) onRegisterClick.run();
                } catch (Exception ex) {
                    System.err.println("[LoginScreen] Register nav error: " + ex.getMessage());
                }
            });

            // Banner container for errors
            bannerContainer = new VBox();
            bannerContainer.setMaxWidth(340);

            // Card
            VBox card = new VBox(12);
            card.getStyleClass().add("login-card");
            card.setPadding(new Insets(40));
            card.setMaxWidth(420);
            card.setAlignment(Pos.CENTER);
            card.getChildren().addAll(
                    logoLabel, titleLabel, subtitleLabel,
                    new Separator(),
                    emailLabel, emailField, emailError,
                    passLabel, passwordField, passError,
                    loginBtn, registerLink, bannerContainer
            );

            // Root
            root = new VBox();
            root.getStyleClass().add("login-root");
            root.setAlignment(Pos.CENTER);
            root.getChildren().add(card);
            VBox.setVgrow(root, Priority.ALWAYS);
        } catch (Exception e) {
            System.err.println("[LoginScreen] UI build error: " + e.getMessage());
            root = new VBox(new Label("Login screen failed to load. Please restart."));
            root.setAlignment(Pos.CENTER);
        }
    }

    /** Handle login — fully crash-safe with validation. */
    private void handleLogin() {
        try {
            clearErrors();

            // Validate email
            String email = (emailField != null) ? emailField.getText() : null;
            if (email == null || email.trim().isEmpty()) {
                showFieldError(emailError, "Email is required.");
                return;
            }
            if (!AuthService.isValidEmail(email.trim())) {
                showFieldError(emailError, "Please enter a valid email address.");
                return;
            }

            // Validate password
            String password = (passwordField != null) ? passwordField.getText() : null;
            if (password == null || password.isEmpty()) {
                showFieldError(passError, "Password is required.");
                return;
            }

            // Disable button to prevent double-click
            if (loginBtn != null) {
                loginBtn.setDisable(true);
                loginBtn.setText("Signing in...");
            }

            // Authenticate
            User user = authService.authenticate(email.trim(), password);

            if (user != null) {
                // Success — navigate to dashboard
                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }
            } else {
                showBanner("Invalid email or password. Please try again.", AlertBanner.Type.ERROR);
                resetButton();
            }
        } catch (Exception e) {
            System.err.println("[LoginScreen] Login error: " + e.getMessage());
            showBanner("An error occurred. Please try again.", AlertBanner.Type.ERROR);
            resetButton();
        }
    }

    private void clearErrors() {
        try {
            if (emailError != null) { emailError.setVisible(false); emailError.setManaged(false); }
            if (passError != null) { passError.setVisible(false); passError.setManaged(false); }
            if (bannerContainer != null) bannerContainer.getChildren().clear();
        } catch (Exception e) { /* ignore */ }
    }

    private void showFieldError(Label errorLabel, String message) {
        try {
            if (errorLabel != null) {
                errorLabel.setText(message);
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        } catch (Exception e) { /* ignore */ }
    }

    private void showBanner(String message, AlertBanner.Type type) {
        try {
            if (bannerContainer != null) {
                bannerContainer.getChildren().clear();
                bannerContainer.getChildren().add(new AlertBanner(message, type));
            }
        } catch (Exception e) { /* ignore */ }
    }

    private void resetButton() {
        try {
            if (loginBtn != null) {
                loginBtn.setDisable(false);
                loginBtn.setText("SIGN IN");
            }
        } catch (Exception e) { /* ignore */ }
    }

    public VBox getRoot() {
        return root != null ? root : new VBox(new Label("Error loading login screen."));
    }
}
