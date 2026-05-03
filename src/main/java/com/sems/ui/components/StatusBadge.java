// File: ui/components/StatusBadge.java — Reusable status label
package com.sems.ui.components;

import javafx.scene.control.Label;
import javafx.geometry.Insets;

/**
 * Color-coded status badge. Automatically maps status name to CSS class.
 * Crash-safe: handles null/empty status gracefully.
 */
public class StatusBadge extends Label {

    public StatusBadge(String statusText) {
        String safeText = (statusText != null && !statusText.isEmpty()) ? statusText : "UNKNOWN";
        setText(safeText.toUpperCase());
        setPadding(new Insets(4, 12, 4, 12));

        String cssClass = "badge-" + safeText.toLowerCase().replace(" ", "_").replace("-", "_");
        getStyleClass().addAll("badge", cssClass);
    }
}
