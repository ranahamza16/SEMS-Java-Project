// File: ui/components/AlertBanner.java — Reusable alert banner
package com.sems.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Alert banner for success/error/info/warning messages.
 * Per GUI_LAYOUT_AND_COMPONENT_SPEC.md Section 5.
 */
public class AlertBanner extends HBox {

    public enum Type { SUCCESS, ERROR, INFO, WARNING }

    public AlertBanner(String message, Type type) {
        String safeMessage = (message != null) ? message : "Notification";
        Type safeType = (type != null) ? type : Type.INFO;

        String icon = switch (safeType) {
            case SUCCESS -> "✅";
            case ERROR -> "❌";
            case WARNING -> "⚠";
            case INFO -> "ℹ";
        };

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px;");

        Label msgLabel = new Label(safeMessage);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setMargin(msgLabel, new Insets(0, 0, 0, 4));

        getChildren().addAll(iconLabel, msgLabel);
        setPadding(new Insets(12, 16, 12, 16));
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().addAll("alert-banner", "alert-" + safeType.name().toLowerCase());
    }
}
