package com.center.academipro.controller.student;

import com.center.academipro.models.Timetable;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class StudentTimetableController implements Initializable {

    @FXML private TableView<Timetable> timetableTable;
    @FXML private TableColumn<Timetable, Integer> idColumn;
    @FXML private TableColumn<Timetable, String> classColumn;
    @FXML private TableColumn<Timetable, LocalDate> dateColumn;
    @FXML private TableColumn<Timetable, LocalTime> startTimeCol;
    @FXML private TableColumn<Timetable, LocalTime> endTimeCol;

    private ObservableList<Timetable> timetableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadTimetable();
        timetableTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        classColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
    }

    private void loadTimetable() {
        timetableList.clear();

        String sql = "SELECT t.id, c.class_name, t.date, t.start_time, t.end_time " +
                "FROM timetable t " +
                "JOIN classes c ON t.class_id = c.id " +
                "ORDER BY t.date, t.start_time";

        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Timetable t = new Timetable(
                        rs.getInt("id"),
                        rs.getString("class_name"),
                        rs.getDate("date").toLocalDate(),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime()
                );
                timetableList.add(t);
            }

            timetableTable.setItems(timetableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}