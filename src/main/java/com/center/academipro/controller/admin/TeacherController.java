package com.center.academipro.controller.admin;

import com.center.academipro.models.Teacher;
import com.center.academipro.utils.DBConnection;
import com.sun.security.auth.callback.TextCallbackHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class TeacherController {
    @FXML
    private TableView<Teacher> tableView_Teacher;

    @FXML
    private TableColumn<Teacher, Date> teacherBirth;

    @FXML
    private TableColumn<Teacher, String> teacherCourse;

    @FXML
    private TableColumn<Teacher, String> teacherEmail;

    @FXML
    private TableColumn<Teacher, Integer> teacherId;

    @FXML
    private TableColumn<Teacher, String> teacherName;

    @FXML
    private TableColumn<Teacher, String> teacherPhone;

    @FXML
    private TableColumn<Teacher, String> teacherUser;
    @FXML
    private TableColumn<Teacher, Void> teacherAction;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    public void initialize(){
        showTeacherList();
        setUpActionColumn();
    }

    public TeacherController(){
        connect = new DBConnection().getConn();
    }
    private void setUpActionColumn(){
        if(teacherAction==null){
            teacherAction= new TableColumn<>("Action");
        }
        teacherAction.setCellFactory(param -> new TableCell<>(){
          private final Button updateBtn = new Button("Update");
          private final Button deleteBtn = new Button("Delete");
          private final HBox btnBox = new HBox(10,updateBtn,deleteBtn);
            {
                updateBtn.setOnAction(event ->{
                    Teacher teacher = getTableView().getItems().get(getIndex());
                    handleUpdate(teacher);
                });
                deleteBtn.setOnAction(event ->{
                    Teacher teacher = getTableView().getItems().get(getIndex());
                    handleDelete(teacher);
                });
                updateBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #a18cd1, #fbc2eb);-fx-text-fill:white");
                deleteBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #ff1e56, #ff4b2b);-fx-text-fill:white");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBox);
                }
            }
        });
    }
    private void handleUpdate(Teacher teacher){
        if(teacher == null){
            System.out.println("Teacher is null.Cannot open details view");
            return;
        }
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/admin/update-teacher.fxml"));
            Parent root = loader.load();
            TeacherDetailsController controller =loader.getController();
            controller.setTeacher(teacher);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Teacher Details");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void handleDelete(Teacher teacher){
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this teacher ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String getUserIdSql = "SELECT user_id FROM teachers WHERE id = ?";
                String deleteTeacherCoursesSql = "DELETE FROM teacher_courses WHERE teacher_id = ?";
                String deleteTeacherSql = "DELETE FROM teachers WHERE id = ?";
                String deleteUserSql = "DELETE FROM users WHERE id = ?";

                try {
                    connect.setAutoCommit(false);

                    int userId = -1;

                    // Lấy user_id
                    try (PreparedStatement getUserIdStmt = connect.prepareStatement(getUserIdSql)) {
                        getUserIdStmt.setInt(1, teacher.getTeacherId());
                        ResultSet rs = getUserIdStmt.executeQuery();
                        if (rs.next()) {
                            userId = rs.getInt("user_id");
                        } else {
                            throw new SQLException("Không tìm thấy user_id cho giáo viên.");
                        }
                    }

                    // 1. Xóa trong bảng trung gian
                    try (PreparedStatement ps1 = connect.prepareStatement(deleteTeacherCoursesSql)) {
                        ps1.setInt(1, teacher.getTeacherId());
                        ps1.executeUpdate();
                    }

                    // 2. Xóa trong bảng teachers
                    try (PreparedStatement ps2 = connect.prepareStatement(deleteTeacherSql)) {
                        ps2.setInt(1, teacher.getTeacherId());
                        ps2.executeUpdate();
                    }

                    // 3. Xóa trong bảng users
                    try (PreparedStatement ps3 = connect.prepareStatement(deleteUserSql)) {
                        ps3.setInt(1, userId);
                        ps3.executeUpdate();
                    }

                    connect.commit();
                    showAlert("Đã xóa giáo viên và tài khoản người dùng thành công.", Alert.AlertType.INFORMATION);
                    tableView_Teacher.getItems().remove(teacher); // hoặc gọi lại showTeacherList();

                } catch (SQLException e) {
                    try {
                        connect.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                    showAlert("Lỗi khi xóa: " + e.getMessage(), Alert.AlertType.ERROR);
                } finally {
                    try {
                        connect.setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void changeSceneAdd(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/admin/add-new-teacher.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) tableView_Teacher.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    };

    public ObservableList<Teacher> TeacherList(){
        ObservableList<Teacher> listData = FXCollections.observableArrayList();

        String sql = "SELECT t.id, t.birthday, t.phone, u.fullname, u.username, u.email, c.course_name " +
                "FROM teachers t " +
                "JOIN users u ON t.user_id = u.id " +
                "JOIN teacher_courses tc ON t.id = tc.teacher_id " +
                "JOIN courses c ON tc.course_id = c.id";

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                Teacher teacher = new Teacher(
                        result.getInt("id"),
                        result.getString("fullname"),
                        result.getString("username"),
                        result.getString("email"),
                        result.getString("birthday"),
                        result.getString("phone"),
                        result.getString("course_name")
                );
                listData.add(teacher);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }
    private ObservableList<Teacher> addTeacherList;
    public void showTeacherList(){
        addTeacherList=TeacherList();
        teacherId.setCellValueFactory(new PropertyValueFactory<>("teacherId"));
        teacherName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        teacherUser.setCellValueFactory(new PropertyValueFactory<>("user"));
        teacherEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        teacherBirth.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        teacherPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        teacherCourse.setCellValueFactory(new PropertyValueFactory<>("course_name"));

        tableView_Teacher.setItems(addTeacherList);
    }
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
