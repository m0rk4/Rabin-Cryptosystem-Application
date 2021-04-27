package by.bsuir.m0rk4.it.task.third.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertService {
    public static void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
