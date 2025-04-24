module com.center.academipro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.security.auth;
    requires mysql.connector.j;
    requires java.desktop;

    opens com.center.academipro to javafx.fxml;
    opens com.center.academipro.view to javafx.fxml;
    opens com.center.academipro.controller to javafx.fxml; // Thêm dòng này ✅
    opens com.center.academipro.controller.student to javafx.fxml;
    opens com.center.academipro.controller.admin.courseManagement to javafx.fxml;
//    opens com.center.academipro.controller.admin to javafx.fxml;
    opens com.center.academipro.controller.admin.teacherManagement to javafx.fxml;
    opens com.center.academipro.controller.teacher to javafx.fxml;
    opens com.center.academipro.controller.admin.studentManagement to javafx.fxml;



    exports com.center.academipro;
    exports com.center.academipro.controller;
    exports com.center.academipro.controller.admin.courseManagement;
    exports com.center.academipro.controller.admin.studentManagement;
    exports com.center.academipro.controller.admin.teacherManagement;
    exports com.center.academipro.controller.teacher;
}