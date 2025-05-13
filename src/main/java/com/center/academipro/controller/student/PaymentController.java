package com.center.academipro.controller.student;

import com.center.academipro.models.Course;
import com.center.academipro.models.Payment;
import com.center.academipro.models.Student;
import com.center.academipro.session.SessionCourse;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;
import java.time.LocalDateTime;

public class PaymentController {

    @FXML
    public TableColumn<Payment, String> studenNameCol;
    @FXML
    public TableColumn<Payment, String> courseNameCol;
    @FXML
    public TableColumn<Payment, LocalDateTime> dateCol;
    @FXML
    private TableView<Payment> paymentTable;
    @FXML
    private RadioButton cashPayment;

    @FXML
    private RadioButton onlinePayment;

    @FXML
    private Label courseName;

    @FXML
    private Label priceCourse;

    @FXML
    private Label amountPayment;

    @FXML
    private Label total;

    @FXML
    private ImageView imageCourse;

    @FXML
    private Text descripCousre;

    private ToggleGroup paymentMethodGroup;
    private int studentId;
    private int courseId;
    ObservableList<Payment> info = FXCollections.observableArrayList();

    public void initialize() {
        paymentMethodGroup = new ToggleGroup();
        cashPayment.setToggleGroup(paymentMethodGroup);
        onlinePayment.setToggleGroup(paymentMethodGroup);

        studenNameCol.setCellValueFactory(cell -> cell.getValue().studentNameProperty());
        courseNameCol.setCellValueFactory(cell -> cell.getValue().courseNameProperty());
        dateCol.setCellValueFactory(cell -> cell.getValue().paymentDateProperty());

    }

    public void setStudentAndCourse(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;

        getCourseInfo();
        getStudentInfo();

    }

    public void getCourseInfo() {
        String sql = "SELECT course_name, price, description, image FROM courses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                courseName.setText(rs.getString("course_name"));
                double price = rs.getDouble("price");
                priceCourse.setText(String.valueOf(price));
                total.setText(String.valueOf(price)); // hiển thị tổng luôn ở đây
                descripCousre.setText(rs.getString("description"));
                String imagePath = rs.getString("image");
                if (imagePath != null && !imagePath.isEmpty()) {
                    imageCourse.setImage(new Image("file:" + imagePath));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getStudentInfo() {
        info.clear();

        System.out.println("Student ID: " + studentId);
        System.out.println("Course ID: " + courseId);

        String sql = "SELECT u.fullname AS student_name, c.course_name " +
                "FROM students s " +
                "JOIN users u ON s.user_id = u.id " +
                "JOIN courses c ON c.id = ? " +
                "WHERE s.user_id  = ?";
        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setInt(2, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String studentName = rs.getString("student_name");
                String courseName = rs.getString("course_name");
                LocalDateTime date = LocalDateTime.now();

                info.add(new Payment(studentName, courseName, date));
                System.out.println("Student Name: " + studentName + ", Course: " + courseName + ", Date: " + date);

            }

            paymentTable.setItems(info);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getStudentIdByUserId(int userId) {
        String sql = "SELECT id FROM students WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }

    @FXML
    private void handlePayment(ActionEvent actionEvent) {
        if (cashPayment.isSelected()) {
            payByCash(actionEvent);
        } else if (onlinePayment.isSelected()) {
            payOnline(); // Nếu sau này bạn cần thanh toán online
        } else {
            showAlert(AlertType.WARNING, "Payment Method", "Please select a payment method!");
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        SceneSwitch.returnToView(actionEvent, "view/student/course/list-courses.fxml");
    }

    private void payByCash(ActionEvent actionEvent) {
        try {
            int userId = SessionManager.getInstance().getUserId();
            int studentId = getStudentIdByUserId(userId);

            if (studentId == -1) {
                showAlert(AlertType.ERROR, "Error", "Student information not found for this user.");
                return;
            }

            Connection conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Kiểm tra đã đăng ký chưa
            String checkSql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, studentId);
            checkStmt.setInt(2, courseId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(AlertType.WARNING, "Duplicate Enrollment", "You have already enrolled in this course.");
                conn.close();
                return;
            }

            // Thực hiện thanh toán
            String paymentSql = "INSERT INTO payments (student_id, course_id, amount, payment_method, payment_status, payment_date) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement paymentStmt = conn.prepareStatement(paymentSql);
            paymentStmt.setInt(1, studentId);
            paymentStmt.setInt(2, courseId);
            paymentStmt.setBigDecimal(3, new java.math.BigDecimal(total.getText()));
            paymentStmt.setString(4, "Cash");
            paymentStmt.setString(5, "Completed");
            paymentStmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            int paymentResult = paymentStmt.executeUpdate();

            // Thêm vào bảng enrollments
            String enrollSql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";
            PreparedStatement enrollStmt = conn.prepareStatement(enrollSql);
            enrollStmt.setInt(1, studentId);
            enrollStmt.setInt(2, courseId);
            int enrollResult = enrollStmt.executeUpdate();

            // Thêm vào bảng purchase_history
            String historySql = "INSERT INTO purchase_history (user_id, course_id, purchase_date) VALUES (?, ?, ?)";
            PreparedStatement historyStmt = conn.prepareStatement(historySql);
            historyStmt.setInt(1, userId);
            historyStmt.setInt(2, courseId);
            historyStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            int historyResult = historyStmt.executeUpdate();

            conn.commit(); // Mọi thứ thành công

            if (paymentResult > 0 && enrollResult > 0) {
                showAlert(AlertType.INFORMATION, "Success", "Payment and enrollment completed!");
                SessionCourse.setCourseId(courseId);
                SceneSwitch.returnToView(actionEvent, "view/student/class/list-classes.fxml");
            } else {
                showAlert(AlertType.ERROR, "Failed", "Operation failed. Please try again.");
            }

            conn.close();
        } catch (SQLIntegrityConstraintViolationException ex) {
            showAlert(AlertType.WARNING, "Duplicate Entry", "You have already enrolled in this course.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
        }
    }

    private void payOnline() {
        showAlert(AlertType.INFORMATION, "Coming Soon", "Online payment not implemented yet.");
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


}

