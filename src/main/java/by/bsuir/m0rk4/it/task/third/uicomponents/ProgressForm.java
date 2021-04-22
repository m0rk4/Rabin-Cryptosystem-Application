package by.bsuir.m0rk4.it.task.third.uicomponents;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressForm {

    private final Stage dialogStage;
    private final ProgressIndicator pin = new ProgressIndicator();

    public ProgressForm() {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Processing...");

        pin.setMinWidth(400);
        pin.setMinHeight(400);
        pin.setProgress(-1F);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(pin);

        Scene scene = new Scene(stackPane);
        dialogStage.setScene(scene);
    }

    public void activateProgressBar(final Task<?> task) {
        pin.progressProperty().bind(task.progressProperty());
        dialogStage.show();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }
}