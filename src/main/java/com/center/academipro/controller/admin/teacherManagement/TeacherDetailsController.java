//package com.center.academipro.controller.admin.teacherManagement;
//
//import com.center.academipro.dao.CourseDAO;
//import com.center.academipro.models.Course;
//import com.center.academipro.models.Teacher;
//import com.center.academipro.utils.DBConnection;
//import com.center.academipro.utils.SceneSwitch;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.*;
//
//import java.net.URL;
//import java.sql.*;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.ResourceBundle;
//
//public class TeacherDetailsController implements Initializable {
//    @FXML
//    private DatePicker birthday;
//
//    @FXML
//    private ListView<Course> courseListView;
//
//    @FXML
//    private TextField email;
//
//    @FXML
//    private TextField fullName;
//
//    @FXML
//    private TextField phone;
//
//    @FXML
//    private TextField username;
//    private Connection connect;
//    private Statement statement;
//    private PreparedStatement prepare;
//    private int selectedTeacherId;
//    private CourseDAO courseDAO = new CourseDAO();
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        courseListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//
//        List<Course> courseList = courseDAO.getAllCourse();
//        courseListView.getItems().addAll(courseList);
//    }
//
//    public TeacherDetailsController() {
//        connect = new DBConnection().getConn();
//    }
//
//    public void setTeacher(Teacher teacher) {
//        selectedTeacherId = teacher.getTeacherId();
//        fullName.setText(teacher.getFullName());
//        username.setText(teacher.getUser());
//        phone.setText(teacher.getPhone());
//        email.setText(teacher.getEmail());
//        birthday.setValue(LocalDate.parse(teacher.getBirthday()));
//        List<Course> teacherCourses = courseDAO.getCoursesByTeacherId(selectedTeacherId);
//
//        for (Course course : courseListView.getItems()) {
//            for (Course teacherCourse : teacherCourses) {
//                if (course.getId() == teacherCourse.getId()) {
//                    courseListView.getSelectionModel().select(course);
//                    break;
//                }
//            }
//        }
//    }
//
//    public boolean addTeacher() {
//        String insertUserSQL = "INSERT INTO users (full_name, username, email) VALUES (?, ?, ?)";
//        String insertTeacherSQL = "INSERT INTO teachers (user_id, phone, birthday) VALUES (?, ?, ?)";
//        String insertTeacherCourseSQL = "INSERT INTO teacher_courses (teacher_id, course_id) VALUES (?, ?)";
//
//        try (Connection conn = new DBConnection().getConn()) {
//            conn.setAutoCommit(false);
//
//            String fullNameVal = fullName.getText().trim();
//            String usernameVal = username.getText().trim();
//            String emailVal = email.getText().trim();
//            String phoneVal = phone.getText().trim();
//            LocalDate birthdayVal = birthday.getValue();
//
//            if (birthdayVal == null || birthdayVal.isAfter(LocalDate.now())) {
//                showAlert("Ngày sinh không hợp lệ.", Alert.AlertType.ERROR);
//                return false;
//            }
//
//            List<Course> selectedCourses = courseListView.getSelectionModel().getSelectedItems();
//            if (selectedCourses == null || selectedCourses.isEmpty()) {
//                showAlert("Vui lòng chọn ít nhất một khóa học.", Alert.AlertType.ERROR);
//                return false;
//            }
//
//            int userId = insertUser(conn, insertUserSQL, fullNameVal, usernameVal, emailVal);
//            int teacherId = insertTeacher(conn, insertTeacherSQL, userId, phoneVal, birthdayVal);
//            insertTeacherCourses(conn, insertTeacherCourseSQL, teacherId, selectedCourses);
//
//            conn.commit();
//            showAlert("Thêm giáo viên thành công!", Alert.AlertType.INFORMATION);
//            return true;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert("Thêm giáo viên thất bại. ", Alert.AlertType.ERROR);
//            return false;
//        }
//    }
//
//    public boolean updateTeacher() {
//        String updateUserSQL = "UPDATE users SET full_name = ?, username = ?, email = ? WHERE id = ?";
//        String updateTeacherSQL = "UPDATE teachers SET phone = ?, birthday = ? WHERE teacher_id = ?";
//        String deleteTeacherCoursesSQL = "DELETE FROM teacher_courses WHERE teacher_id = ?";
//        String insertTeacherCourseSQL = "INSERT INTO teacher_courses (teacher_id, course_id) VALUES (?, ?)";
//
//        try (Connection conn = new DBConnection().getConn()) {
//            conn.setAutoCommit(false);
//
//            String fullNameVal = fullName.getText().trim();
//            String usernameVal = username.getText().trim();
//            String emailVal = email.getText().trim();
//            String phoneVal = phone.getText().trim();
//            LocalDate birthdayVal = birthday.getValue();
//
//            if (birthdayVal == null || birthdayVal.isAfter(LocalDate.now())) {
//                showAlert("Ngày sinh không hợp lệ.", Alert.AlertType.ERROR);
//                return false;
//            }
//
//            List<Course> selectedCourses = courseListView.getSelectionModel().getSelectedItems();
//            if (selectedCourses == null || selectedCourses.isEmpty()) {
//                showAlert("Vui lòng chọn ít nhất một khóa học.", Alert.AlertType.ERROR);
//                return false;
//            }
//
//            // Cập nhật thông tin người dùng
//            updateUser(conn, updateUserSQL, fullNameVal, usernameVal, emailVal);
//
//            // Cập nhật thông tin giáo viên
//            updateTeacherInfo(conn, updateTeacherSQL, phoneVal, birthdayVal);
//
//            // Xóa các khóa học cũ của giáo viên
//            deleteTeacherCourses(conn, deleteTeacherCoursesSQL);
//
//            // Thêm các khóa học mới mà giáo viên giảng dạy
//            insertTeacherCourses(conn, insertTeacherCourseSQL, selectedCourses);
//
//            conn.commit();
//            showAlert("Cập nhật giáo viên thành công!", Alert.AlertType.INFORMATION);
//            return true;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert("Cập nhật giáo viên thất bại.", Alert.AlertType.ERROR);
//            return false;
//        }
//    }
//
//    public void clearTeacher() {
//        fullName.clear();
//        username.clear();
//        email.clear();
//        phone.clear();
//        birthday.setValue(null);
//        courseListView.getSelectionModel().clearSelection();
//    }
//
//    private void updateUser(Connection conn, String sql, String fullName, String username, String email) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, fullName);
//            stmt.setString(2, username);
//            stmt.setString(3, email);
//            stmt.setInt(4, selectedTeacherId); // ID của người dùng đang được cập nhật
//            if (stmt.executeUpdate() == 0) {
//                throw new SQLException("Không thể cập nhật thông tin người dùng.");
//            }
//        }
//    }
//
//    private void updateTeacherInfo(Connection conn, String sql, String phone, LocalDate birthday) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, phone);
//            stmt.setDate(2, java.sql.Date.valueOf(birthday));
//            stmt.setInt(3, selectedTeacherId); // ID của giáo viên đang được cập nhật
//            if (stmt.executeUpdate() == 0) {
//                throw new SQLException("Không thể cập nhật thông tin giáo viên.");
//            }
//        }
//    }
//
//    private void deleteTeacherCourses(Connection conn, String sql) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, selectedTeacherId); // ID của giáo viên đang được cập nhật
//            stmt.executeUpdate();
//        }
//    }
//
//    private void insertTeacherCourses(Connection conn, String sql, List<Course> courses) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            for (Course course : courses) {
//                stmt.setInt(1, selectedTeacherId); // ID của giáo viên đang được cập nhật
//                stmt.setInt(2, course.getId());
//                stmt.addBatch();
//            }
//            stmt.executeBatch();
//        }
//    }
//
//
//    private int insertUser(Connection conn, String sql, String fullName, String username, String email) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setString(1, fullName);
//            stmt.setString(2, username);
//            stmt.setString(3, email);
//            if (stmt.executeUpdate() == 0) throw new SQLException("Không thể thêm user.");
//            try (ResultSet keys = stmt.getGeneratedKeys()) {
//                if (keys.next()) return keys.getInt(1);
//                throw new SQLException("Không lấy được ID user.");
//            }
//        }
//    }
//
//    private int insertTeacher(Connection conn, String sql, int userId, String phone, LocalDate birthday) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setInt(1, userId);
//            stmt.setString(2, phone);
//            stmt.setDate(3, java.sql.Date.valueOf(birthday));
//            if (stmt.executeUpdate() == 0) throw new SQLException("Không thể thêm teacher.");
//            try (ResultSet keys = stmt.getGeneratedKeys()) {
//                if (keys.next()) return keys.getInt(1);
//                throw new SQLException("Không lấy được ID teacher.");
//            }
//        }
//    }
//
//    private void insertTeacherCourses(Connection conn, String sql, int teacherId, List<Course> courses) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            for (Course course : courses) {
//                stmt.setInt(1, teacherId);
//                stmt.setInt(2, course.getId());
//                stmt.addBatch();
//            }
//            stmt.executeBatch();
//        }
//    }
//
//    private void showAlert(String message, Alert.AlertType type) {
//        Alert alert = new Alert(type);
//        alert.setTitle("Thông báo");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    public void handleCancel(ActionEvent actionEvent) {
//        SceneSwitch.backToView(actionEvent, "view/admin/teacherManagement/teacher-view.fxml");
//    }
//
//
//}
