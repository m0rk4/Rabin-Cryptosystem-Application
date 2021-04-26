package by.bsuir.m0rk4.it.task.third.controller;

import by.bsuir.m0rk4.it.task.third.data.RabinInvalidDataException;
import by.bsuir.m0rk4.it.task.third.model.AppModel;
import by.bsuir.m0rk4.it.task.third.uicomponents.ProgressForm;
import by.bsuir.m0rk4.it.task.third.uicomponents.service.AlertService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PrimaryController {

    @FXML
    private CheckBox sampleTestCBox;
    @FXML
    private TextField sampleTestTField;

    @FXML
    private TextArea pTArea;
    @FXML
    private TextArea qTArea;
    @FXML
    private TextArea bTArea;
    @FXML
    private TextArea nTArea;

    @FXML
    private Button fileUploadButton;
    @FXML
    private Button encryptButton;
    @FXML
    private Button decryptButton;
    @FXML
    private Button cancelFileButton;

    @FXML
    private Label filenameLabel;
    @FXML
    private Label blockSizeLabel;

    private final AppModel appModel;
    private FileChooser fileChooser;
    private Optional<File> fileInputOptional = Optional.empty();

    public PrimaryController(AppModel appModel) {
        this.appModel = appModel;
    }

    @FXML
    private void initialize() {
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Select file");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Any file (*.*)", "*.*"));

        pTArea.textProperty().addListener((obs, oldVal, newVal) -> {
            processTextInputChanges(pTArea, newVal);
            calculateN();
        });
        qTArea.textProperty().addListener((obs, oldVal, newVal) -> {
            processTextInputChanges(qTArea, newVal);
            calculateN();
        });
        bTArea.textProperty().addListener((obs, oldVal, newVal) ->
                processTextInputChanges(bTArea, newVal));

        sampleTestTField.editableProperty().bind(sampleTestCBox.selectedProperty());
        sampleTestTField.textProperty().addListener((obs, oldVal, newVal) ->
                processTextInputChanges(sampleTestTField, newVal));

        encryptButton.setOnAction(this::processEncrypt);
        decryptButton.setOnAction(this::processDecrypt);
        fileUploadButton.setOnAction(this::uploadFile);
        cancelFileButton.setOnAction(this::cancelFile);
    }

    private void cancelFile(ActionEvent actionEvent) {
        fileInputOptional = Optional.empty();
        filenameLabel.setText("Filename: ");
    }

    private void processDecrypt(ActionEvent actionEvent) {
        String pText = pTArea.getText();
        String qText = qTArea.getText();
        String bText = bTArea.getText();

        try {
            appModel.validate(pText, qText, bText);
        } catch (RabinInvalidDataException e) {
            AlertService.showAlert(Alert.AlertType.ERROR, "Validation Error", "Message", e.getMessage());
        }
    }

    private void processEncrypt(ActionEvent actionEvent) {
        String pText = pTArea.getText();
        String qText = qTArea.getText();
        String bText = bTArea.getText();

        BigInteger p;
        BigInteger q;
        BigInteger b;
        try {
            appModel.validate(pText, qText, bText);
            p = new BigInteger(pText);
            q = new BigInteger(qText);
            b = new BigInteger(bText);
        } catch (RabinInvalidDataException e) {
            AlertService.showAlert(Alert.AlertType.ERROR, "Validation Error", "Message", e.getMessage());
            return;
        }


        if (sampleTestCBox.isSelected()) {

        } else {
            if (fileInputOptional.isEmpty()) {
                AlertService.showAlert(Alert.AlertType.ERROR, "File Error", "Message", "No input file found.");
                return;
            }
            Optional<File> fileSaveOptional = saveFile();
            if (fileSaveOptional.isPresent()) {
                File fileInput = fileInputOptional.get();
                File fileOutput = fileSaveOptional.get();

                BigInteger n = p.multiply(q);
                runTask(() -> appModel.encryptFile(fileInput, fileOutput, b, n));
            }
        }
    }

    private void runTask(Supplier<Task<List<String>>> taskSupplier) {
        Task<List<String>> task = taskSupplier.get();
        ProgressForm pForm = new ProgressForm();
        task.setOnSucceeded(event -> {
            Worker<List<String>> source = event.getSource();
            List<String> value = source.getValue();
            updateResults(value);

            encryptButton.setDisable(false);
            decryptButton.setDisable(false);
            pForm.getDialogStage().close();
        });
        encryptButton.setDisable(true);
        decryptButton.setDisable(true);
        pForm.activateProgressBar(task);
        Thread thread = new Thread(task);
        thread.start();
    }

    private void updateResults(List<String> value) {

    }

    private void calculateN() {
        String pText = pTArea.getText();
        if (pText.isBlank()) {
            nTArea.clear();
            blockSizeLabel.setText("Block Size: ");
            return;
        }
        String qText = qTArea.getText();
        if (qText.isBlank()) {
            nTArea.clear();
            blockSizeLabel.setText("Block Size: ");
            return;
        }
        BigInteger p = new BigInteger(pText);
        BigInteger q = new BigInteger(qText);
        BigInteger n = p.multiply(q);
        String nStr = n.toString();
        nTArea.setText(nStr);
        int length = n.toByteArray().length;
        blockSizeLabel.setText("Block Size: " + (length - 1));
    }

    private void processTextInputChanges(TextInputControl textInputControl, String newVal) {
        String filteredVal = newVal.replaceAll("[^\\d]+", "");
        textInputControl.setText(filteredVal);
    }

    private Optional<File> saveFile() {
        File saveFile = fileChooser.showSaveDialog(null);
        return Optional.ofNullable(saveFile);
    }

    private void uploadFile(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        fileInputOptional = Optional.ofNullable(file);
    }
}
