package by.bsuir.m0rk4.it.task.third.uicomponents;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressForm {

    private static final String PROGRESS_FORM_TITLE = "Processing...";

    private final Stage dialogStage;
    private final ProgressIndicator progressIndicator;

    public ProgressForm() {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(PROGRESS_FORM_TITLE);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setMinWidth(400);
        progressIndicator.setMinHeight(400);
        progressIndicator.setProgress(-1F);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(progressIndicator);

        Scene scene = new Scene(stackPane);
        dialogStage.setScene(scene);
    }

    public void activateProgressBar(final Task<?> task) {
        progressIndicator.progressProperty().bind(task.progressProperty());
        dialogStage.show();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }
}