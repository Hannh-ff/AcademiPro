module com.center.academipro {
    requires javafx.fxml;
    requires java.sql;
    requires jdk.security.auth;
    requires mysql.connector.j;
    requires jdk.httpserver;
    requires javafx.web;

    opens com.center.academipro.models to javafx.base;


    opens com.center.academipro to javafx.fxml;
    opens com.center.academipro.view to javafx.fxml;
    opens com.center.academipro.controller to javafx.fxml; // Thêm dòng này ✅
    opens com.center.academipro.controller.student to javafx.fxml;
    opens com.center.academipro.controller.admin.courseManagement to javafx.fxml;
//    opens com.center.academipro.controller.admin to javafx.fxml;
    opens com.center.academipro.controller.admin.paymentManagement to javafx.fxml;
    opens com.center.academipro.controller.admin.teacherManagement to javafx.fxml;
    opens com.center.academipro.controller.teacher to javafx.fxml;
    opens com.center.academipro.controller.admin.studentManagement to javafx.fxml;
    opens com.center.academipro.controller.admin.classManagement to javafx.fxml;




    exports com.center.academipro;
    exports com.center.academipro.controller;
    exports com.center.academipro.controller.admin.courseManagement;
    exports com.center.academipro.controller.admin.studentManagement;
    exports com.center.academipro.controller.admin.teacherManagement;
    exports com.center.academipro.controller.teacher;
    exports com.center.academipro.controller.admin.classManagement to javafx.fxml;
    opens com.center.academipro.controller.student.classs to javafx.fxml;
    opens com.center.academipro.controller.student.course to javafx.fxml;
    opens com.center.academipro.controller.student.assignment to javafx.fxml;
}