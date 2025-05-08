//package com.center.academipro.controller.student.course;
//
//import com.center.academipro.models.Course;
//import com.center.academipro.models.Payment;
//import com.center.academipro.controller.service.VNPayService;
//import com.center.academipro.utils.DBConnection;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.text.Text;
//import javafx.event.ActionEvent;
//import javafx.scene.control.Alert.AlertType;
//
//import java.sql.*;
//import java.time.LocalDateTime;
//import java.io.IOException;
//import java.net.URI;
//import java.util.HashMap;
//import java.util.Map;
//
//public class PaymentController {
//
//    // FXML components
//    @FXML private RadioButton cashPayment, onlinePayment;
//    @FXML private Label courseNameLabel, priceCourse, amountPayment, total;
//    @FXML private TextField userIdField;
//    @FXML private Label studentNameLabel;
//    @FXML private ImageView imageCourse;
//    @FXML private Text descripCourse;
//    @FXML private ComboBox<Course> courseComboBox;
//    @FXML private TableView<Payment> paymentTable;
//    @FXML private TableColumn<Payment, Integer> userIdCol, courseIdCol;
//    @FXML private TableColumn<Payment, Double> amountCol;
//    @FXML private TableColumn<Payment, String> methodCol, statusCol;
//
//    private ToggleGroup paymentMethodGroup;
//    private int currentUserId;
//    private int currentCourseId;
//    private Map<Integer, String> studentNames = new HashMap<>();
//    private ObservableList<Course> courses = FXCollections.observableArrayList();
//    private ObservableList<Payment> paymentHistory = FXCollections.observableArrayList();
//    private VNPayService vnPayService;
//
//    @FXML
//    public void initialize() {
//        // Khởi tạo VNPayService
//        vnPayService = new VNPayService();
//
//        // Phần còn lại giữ nguyên
//        paymentMethodGroup = new ToggleGroup();
//        cashPayment.setToggleGroup(paymentMethodGroup);
//        onlinePayment.setToggleGroup(paymentMethodGroup);
//        onlinePayment.setSelected(true);
//
//        loadStudentNames();
//        loadCourses();
//        loadPaymentHistory();
//        setupCourseComboBox();
//        setupPaymentTable();
//
//        userIdField.textProperty().addListener((obs, oldVal, newVal) -> handleUserIdInput());
//    }
//
//    private void loadStudentNames() {
//        String sql = "SELECT u.id as user_id, u.fullname as user_name FROM users u JOIN students s ON u.id = s.user_id";
//
//        try (Connection conn = DBConnection.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            studentNames.clear();
//            while (rs.next()) {
//                studentNames.put(rs.getInt("user_id"), rs.getString("user_name"));
//            }
//        } catch (SQLException e) {
//            showAlert(AlertType.ERROR, "Database Error", "Failed to load students: " + e.getMessage());
//        }
//    }
//
//    private void loadCourses() {
//        String sql = "SELECT id, course_name, description, image, price FROM courses";
//
//        try (Connection conn = DBConnection.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            courses.clear();
//            while (rs.next()) {
//                courses.add(new Course(
//                        rs.getInt("id"),
//                        rs.getString("course_name"),
//                        rs.getString("description"),
//                        rs.getString("image"),
//                        rs.getDouble("price")
//                ));
//            }
//        } catch (SQLException e) {
//            showAlert(AlertType.ERROR, "Database Error", "Failed to load courses: " + e.getMessage());
//        }
//    }
//
//    private void loadPaymentHistory() {
//        String sql = "SELECT id, user_id, course_id, amount, payment_method, status, transaction_id, payment_date FROM payments";
//        try (Connection conn = DBConnection.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            paymentHistory.add(new Payment(
//                    rs.getInt("user_id"),
//                    rs.getInt("course_id"),
//                    rs.getDouble("amount"),
//                    rs.getString("payment_method"),
//                    rs.getString("status"),
//                    "", // Mã giao dịch mặc định
//                    LocalDateTime.now() // Ngày thanh toán mặc định
//            ));
//            paymentTable.setItems(paymentHistory);
//        } catch (SQLException e) {
//            showAlert(AlertType.ERROR, "Database Error", "Failed to load payment history: " + e.getMessage());
//        }
//    }
//    private void setupCourseComboBox() {
//        courseComboBox.setItems(courses);
//        courseComboBox.setCellFactory(param -> new ListCell<Course>() {
//            @Override
//            protected void updateItem(Course item, boolean empty) {
//                super.updateItem(item, empty);
//                if (item == null || empty) {
//                    setText(null);
//                } else {
//                    setText(String.format("%s (%,.0f VND)", item.getCourseName(), item.getPrice()));
//                }
//            }
//        });
//
//        // Set how items appear in the dropdown button
//        courseComboBox.setButtonCell(new ListCell<Course>() {
//            @Override
//            protected void updateItem(Course item, boolean empty) {
//                super.updateItem(item, empty);
//                if (item == null || empty) {
//                    setText("Select a course");
//                } else {
//                    setText(String.format("%s (%,.0f VND)", item.getCourseName(), item.getPrice()));
//                }
//            }
//        });
//    }
//    private void setupPaymentTable() {
//        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
//        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
//        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
//        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
//        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
//    }
//
//    @FXML
//    private void handleUserIdInput() {
//        try {
//            String input = userIdField.getText().trim();
//            if (!input.isEmpty()) {
//                int userId = Integer.parseInt(input);
//                String name = studentNames.get(userId);
//
//                if (name != null) {
//                    studentNameLabel.setText(name);
//                    currentUserId = userId;
//                } else {
//                    studentNameLabel.setText("Student not found");
//                    currentUserId = 0;
//                }
//            }
//        } catch (NumberFormatException e) {
//            studentNameLabel.setText("Invalid ID format");
//            currentUserId = 0;
//        }
//    }
//
//    @FXML
//    private void handleCourseSelection(ActionEvent event) {
//        Course selectedCourse = courseComboBox.getValue();
//        if (selectedCourse != null) {
//            currentCourseId = selectedCourse.getId();
//            setCourseData(
//                    selectedCourse.getCourseName(),
//                    selectedCourse.getDescription(),
//                    selectedCourse.getImage(),
//                    selectedCourse.getPrice()
//            );
//        }
//    }
//
//    public void setCourseData(String name, String description, String imageUrl, double price) {
//        courseNameLabel.setText(name);
//        descripCourse.setText(description);
//        priceCourse.setText(String.format("%,.0f VND", price));
//        amountPayment.setText("1");
//        total.setText(String.format("%,.0f VND", price));
//        try {
//            imageCourse.setImage(new Image(imageUrl));
//        } catch (Exception e) {
//            imageCourse.setImage(null);
//            System.err.println("Error loading image: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    private void handlePayment(ActionEvent event) {
//        if (currentUserId == 0) {
//            showAlert(AlertType.WARNING, "Warning", "Please select a valid student");
//            return;
//        }
//
//        if (currentCourseId == 0) {
//            showAlert(AlertType.WARNING, "Warning", "Please select a course");
//            return;
//        }
//
//        if (cashPayment.isSelected()) {
//            handleCashPayment();
//        } else if (onlinePayment.isSelected()) {
//            handleOnlinePayment(event);
//        } else {
//            showAlert(AlertType.WARNING, "Warning", "Please select a payment method!");
//        }
//    }
//    private void handleCashPayment() {
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(
//                     "INSERT INTO payments (user_id, course_id, amount, payment_method, status, payment_date) VALUES (?, ?, ?, ?, ?, ?)")) {
//
//            double amount = parsePrice(total.getText());
//
//            pstmt.setInt(1, currentUserId);
//            pstmt.setInt(2, currentCourseId);
//            pstmt.setDouble(3, amount);
//            pstmt.setString(4, "Cash");
//            pstmt.setString(5, "Completed");
//            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
//
//            if (pstmt.executeUpdate() > 0) {
//                showAlert(AlertType.INFORMATION, "Success", "Cash payment successful!");
//                loadPaymentHistory();
//            }
//        } catch (Exception e) {
//            handleError("Cash Payment Error", e);
//        }
//    }
//
//    @FXML
//    private void handleOnlinePayment(ActionEvent event) {
//        try {
//            Course selectedCourse = courseComboBox.getValue();
//            if (selectedCourse == null) {
//                showAlert(AlertType.ERROR, "Error", "Please select a course first");
//                return;
//            }
//
//            double amount = selectedCourse.getPrice();
//            if (amount < 10000) {
//                showAlert(AlertType.ERROR, "Error", "Minimum payment amount is 10,000 VND");
//                return;
//            }
//
//            // Tạo mã giao dịch
//            String orderId = "ACADEMI" + System.currentTimeMillis();
//            String orderInfo = "Payment for course: " + selectedCourse.getCourseName();
//
//            // Lưu payment vào database với trạng thái Pending
//            savePaymentToDatabase(orderId, amount, "VNPay", "Pending");
//
//            // Tạo URL thanh toán VNPay
//            String paymentUrl = vnPayService.createPaymentUrl(
//                    amount,
//                    orderId,
//                    orderInfo,
//                    "127.0.0.1" // IP address (có thể thay bằng IP thực tế)
//            );
//
//            // Mở trình duyệt để thanh toán
//            openBrowser(paymentUrl);
//
//        } catch (Exception e) {
//            handleError("VNPay Payment Error", e);
//        }
//    }
//
//    private void savePaymentToDatabase(String orderId, double amount, String method, String status) {
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(
//                     "INSERT INTO payments (user_id, course_id, amount, payment_method, status, transaction_id, payment_date) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
//
//            pstmt.setInt(1, currentUserId);
//            pstmt.setInt(2, currentCourseId);
//            pstmt.setDouble(3, amount);
//            pstmt.setString(4, method);
//            pstmt.setString(5, status);
//            pstmt.setString(6, orderId);
//            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            handleError("Payment Save Error", e);
//        }
//    }
//
//    private double parsePrice(String priceText) {
//        return Double.parseDouble(priceText.replaceAll("[^\\d.]", ""));
//    }
//
//    private String getClientIp() {
//        try {
//            return java.net.InetAddress.getLocalHost().getHostAddress();
//        } catch (Exception e) {
//            return "127.0.0.1";
//        }
//    }
//
//    private void openBrowser(String url) {
//        try {
//            java.awt.Desktop.getDesktop().browse(URI.create(url));
//        } catch (IOException e) {
//            showAlert(AlertType.ERROR, "Error", "Cannot open browser: " + e.getMessage());
//        }
//    }
//
//
//
//    private void showAlert(AlertType type, String title, String message) {
//        Alert alert = new Alert(type, message, ButtonType.OK);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.showAndWait();
//    }
//
//    private void handleError(String title, Exception e) {
//        e.printStackTrace();
//        showAlert(AlertType.ERROR, title, e.getMessage());
//    }
//}