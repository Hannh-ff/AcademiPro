module com.center.academipro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.center.academipro to javafx.fxml;
    opens com.center.academipro.view to javafx.fxml;
    opens com.center.academipro.controller to javafx.fxml; // Thêm dòng này ✅

    exports com.center.academipro;
    exports com.center.academipro.controller;
    exports com.center.academipro.controller.course;
    opens com.center.academipro.controller.course to javafx.fxml;
}