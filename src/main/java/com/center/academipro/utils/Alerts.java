package com.center.academipro.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Alerts {
    /**
     * Hiển thị thông báo lỗi
     * @param title Tiêu đề của hộp thoại
     * @param message Nội dung thông báo
     */

    public static void alertError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị thông báo thông tin
     * @param title Tiêu đề của hộp thoại
     * @param message Nội dung thông báo
     */
    public static void alertInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị thông báo cảnh báo
     * @param title Tiêu đề của hộp thoại
     * @param message Nội dung thông báo
     */
    public static void alertWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị hộp thoại xác nhận với hai lựa chọn Yes/No
     * @param title Tiêu đề của hộp thoại
     * @param message Nội dung thông báo
     * @return true nếu người dùng chọn Yes, false nếu chọn No
     */
    private static boolean lastResult;
    public static boolean alertConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        lastResult = result.isPresent() && result.get() == ButtonType.OK;
        return lastResult;
    }

    public static boolean getResult() {
        return lastResult;
    }
}
