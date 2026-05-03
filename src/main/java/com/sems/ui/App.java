// File: ui/App.java — JavaFX Application class
// Maps to: GUI_LAYOUT_AND_COMPONENT_SPEC.md Section 4 Navigation Flow
// Manages screen navigation: Login → Dashboard → Borrow/Return → Back
// Crash-free: CSS loading with fallback, navigation with null checks
package com.sems.ui;

import com.sems.singleton.DatabaseManager;
import com.sems.singleton.SessionManager;
import com.sems.ui.screens.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main JavaFX Application — manages scene and screen navigation.
 * All screen transitions are crash-safe with try-catch and fallbacks.
 */
public class App extends Application {

    private Stage primaryStage;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        try {
            this.primaryStage = stage;

            // Initialize database (loads mock data)
            DatabaseManager.getInstance();

            // Start with login screen
            StackPane placeholder = new StackPane();
            scene = new Scene(placeholder, 1200, 750);

            // Load CSS — crash-safe with fallback
            loadCSS();

            // Navigate to login
            navigateToLogin();

            stage.setTitle("Sports Equipment Management System — SEMS");
            stage.setScene(scene);
            stage.setMinWidth(1024);
            stage.setMinHeight(650);
            stage.show();
        } catch (Exception e) {
            System.err.println("[App] CRITICAL START ERROR: " + e.getMessage());
            e.printStackTrace();
            // Emergency fallback — show a basic window
            try {
                StackPane fallback = new StackPane(new javafx.scene.control.Label(
                        "Application failed to start. Error: " + e.getMessage()));
                stage.setScene(new Scene(fallback, 600, 400));
                stage.setTitle("SEMS — Error");
                stage.show();
            } catch (Exception e2) {
                System.err.println("[App] Complete failure: " + e2.getMessage());
            }
        }
    }

    /** Load CSS — falls back gracefully if file not found. */
    private void loadCSS() {
        try {
            var cssUrl = getClass().getResource("/styles/global.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("[App] CSS file not found — running with default styles.");
            }
        } catch (Exception e) {
            System.err.println("[App] CSS load error (non-fatal): " + e.getMessage());
        }
    }

    /** Navigate to login screen. */
    private void navigateToLogin() {
        try {
            // Ensure session is cleared
            SessionManager.getInstance().endSession();

            LoginScreen loginScreen = new LoginScreen(
                    this::navigateToDashboard,  // onLoginSuccess
                    this::navigateToRegister     // onRegisterClick
            );
            setRoot(loginScreen.getRoot());
        } catch (Exception e) {
            System.err.println("[App] Login nav error: " + e.getMessage());
        }
    }

    /** Navigate to register screen. */
    private void navigateToRegister() {
        try {
            RegisterScreen registerScreen = new RegisterScreen(this::navigateToLogin);
            setRoot(registerScreen.getRoot());
        } catch (Exception e) {
            System.err.println("[App] Register nav error: " + e.getMessage());
            navigateToLogin(); // Fallback
        }
    }

    /** Navigate to dashboard. */
    private void navigateToDashboard() {
        try {
            if (!SessionManager.getInstance().isLoggedIn()) {
                navigateToLogin();
                return;
            }

            DashboardScreen dashboard = new DashboardScreen(target -> {
                try {
                    switch (target != null ? target : "") {
                        case "borrow" -> navigateToBorrow();
                        case "return" -> navigateToReturn();
                        case "myBorrows" -> navigateToMyBorrows();
                        case "equipmentManagement" -> navigateToEquipmentManagement();
                        case "login" -> navigateToLogin();
                        default -> navigateToDashboard();
                    }
                } catch (Exception e) {
                    System.err.println("[App] Dashboard nav error: " + e.getMessage());
                }
            });
            setRoot(dashboard.getRoot());
        } catch (Exception e) {
            System.err.println("[App] Dashboard nav error: " + e.getMessage());
            navigateToLogin(); // Fallback
        }
    }

    /** Navigate to borrow request screen. */
    private void navigateToBorrow() {
        try {
            if (!SessionManager.getInstance().isLoggedIn()) {
                navigateToLogin();
                return;
            }
            BorrowRequestScreen borrowScreen = new BorrowRequestScreen(this::navigateToDashboard);
            setRoot(borrowScreen.getRoot());
        } catch (Exception e) {
            System.err.println("[App] Borrow nav error: " + e.getMessage());
            navigateToDashboard();
        }
    }

    /** Navigate to return screen. */
    private void navigateToReturn() {
        try {
            if (!SessionManager.getInstance().isLoggedIn()) {
                navigateToLogin();
                return;
            }
            ReturnScreen returnScreen = new ReturnScreen(this::navigateToDashboard);
            setRoot(returnScreen.getRoot());
        } catch (Exception e) {
            System.err.println("[App] Return nav error: " + e.getMessage());
            navigateToDashboard();
        }
    }

    /** Navigate to My Borrows screen. */
    private void navigateToMyBorrows() {
        try {
            if (!SessionManager.getInstance().isLoggedIn()) {
                navigateToLogin();
                return;
            }
            MyBorrowsScreen myBorrowsScreen = new MyBorrowsScreen(target -> {
                if ("return".equals(target)) navigateToReturn();
                else navigateToDashboard();
            });
            setRoot(myBorrowsScreen.getRoot());
        } catch (Exception e) {
            System.err.println("[App] MyBorrows nav error: " + e.getMessage());
            navigateToDashboard();
        }
    }

    /** Navigate to Equipment Management screen. */
    private void navigateToEquipmentManagement() {
        try {
            if (!SessionManager.getInstance().isLoggedIn() || !SessionManager.getInstance().isAdmin()) {
                navigateToDashboard();
                return;
            }
            EquipmentManagementScreen equipmentManagementScreen = new EquipmentManagementScreen(target -> navigateToDashboard());
            setRoot(equipmentManagementScreen.getRoot());
        } catch (Exception e) {
            System.err.println("[App] EquipmentManagement nav error: " + e.getMessage());
            navigateToDashboard();
        }
    }

    /** Set the scene root — crash-safe. */
    private void setRoot(Pane newRoot) {
        try {
            if (scene != null && newRoot != null) {
                scene.setRoot(newRoot);
            }
        } catch (Exception e) {
            System.err.println("[App] SetRoot error: " + e.getMessage());
        }
    }
}
