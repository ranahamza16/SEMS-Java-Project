// File: Main.java — Application entry point
// Maps to: ARCHITECTURE_AND_DESIGN_MAPPING.md — Main.java
// Launches JavaFX via separate launcher class (required for module system compatibility)
package com.sems;

import com.sems.ui.App;
import javafx.application.Application;

/**
 * Entry point — launches the JavaFX Application.
 * Separate from App.java to avoid JavaFX module issues.
 */
public class Main {
    public static void main(String[] args) {
        try {
            Application.launch(App.class, args);
        } catch (Exception e) {
            System.err.println("FATAL: Application failed to launch.");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
