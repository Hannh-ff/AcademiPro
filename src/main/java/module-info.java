module com.example.demo3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.center.academipro to javafx.fxml;
    exports com.center.academipro;
}