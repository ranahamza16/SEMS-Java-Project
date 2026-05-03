module com.sems {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.sems to javafx.fxml;
    opens com.sems.ui to javafx.fxml;
    opens com.sems.ui.screens to javafx.fxml;
    opens com.sems.ui.components to javafx.fxml;
    opens com.sems.model to javafx.base;
    opens com.sems.model.enums to javafx.base;

    exports com.sems;
    exports com.sems.ui;
    exports com.sems.ui.screens;
    exports com.sems.ui.components;
    exports com.sems.model;
    exports com.sems.model.enums;
    exports com.sems.state.equipment;
    exports com.sems.factory;
    exports com.sems.observer;
    exports com.sems.singleton;
    exports com.sems.service;
}
