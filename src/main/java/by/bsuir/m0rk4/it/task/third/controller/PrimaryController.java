package by.bsuir.m0rk4.it.task.third.controller;

import by.bsuir.m0rk4.it.task.third.data.exception.RabinInvalidDataException;
import by.bsuir.m0rk4.it.task.third.entity.ResultBuilder;
import by.bsuir.m0rk4.it.task.third.entity.ResultModel;
import by.bsuir.m0rk4.it.task.third.model.AppModel;
import by.bsuir.m0rk4.it.task.third.uicomponents.ProgressForm;
import by.bsuir.m0rk4.it.task.third.util.AlertService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class PrimaryController {
    @FXML
    private Slider jacobiSlider;
    @FXML
    private Label jacobiLabel;
    @FXML
    private RadioButton evenRButton;
    @FXML
    private RadioButton oddRButton;

    @FXML
    private BorderPane mainPane;

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
    public Button clearHistoryButton;

    @FXML
    private Label filenameLabel;
    @FXML
    private Label blockSizeLabel;
    @FXML
    private TableView<ResultModel> resultsTable;

    private final Stage stage;
    private final AppModel appModel;

    private FileChooser fileChooser;
    private Optional<File> fileInputOptional;
    private ResultModel tableRowsSeparator;

    public PrimaryController(Stage stage, AppModel appModel) {
        this.stage = stage;
        this.appModel = appModel;
    }

    @FXML
    private void initialize() {
        ResultBuilder resultBuilder = new ResultBuilder();
        resultBuilder.reset();
        tableRowsSeparator = resultBuilder
                .buildMeta("")
                .buildHexResult("")
                .buildDecimalResult("")
                .buildOperationType("")
                .buildDecimalSource("")
                .buildHexSource("")
                .getResultModel();

        TableColumn<ResultModel, String> hexSourceCol = new TableColumn<>("Hex Source");
        TableColumn<ResultModel, String> decimalSourceCol = new TableColumn<>("Decimal Source");
        TableColumn<ResultModel, String> operationCol = new TableColumn<>("Operation");
        TableColumn<ResultModel, String> hexResultCol = new TableColumn<>("Hex Result");
        TableColumn<ResultModel, String> decimalResultCol = new TableColumn<>("Decimal Result");
        TableColumn<ResultModel, String> metaCol = new TableColumn<>("Meta");

        hexResultCol.setCellValueFactory(
                new PropertyValueFactory<>("hexResult")
        );
        decimalResultCol.setCellValueFactory(
                new PropertyValueFactory<>("decimalResult")
        );
        operationCol.setCellValueFactory(
                new PropertyValueFactory<>("operationType")
        );
        operationCol.setMinWidth(70);
        operationCol.setMaxWidth(70);
        metaCol.setCellValueFactory(
                new PropertyValueFactory<>("meta")
        );
        metaCol.setMinWidth(190);
        metaCol.setMaxWidth(190);
        hexSourceCol.setCellValueFactory(
                new PropertyValueFactory<>("hexSource")
        );
        decimalSourceCol.setCellValueFactory(
                new PropertyValueFactory<>("decimalSource")
        );

        resultsTable.getColumns().addAll(hexSourceCol, decimalSourceCol, operationCol,
                hexResultCol, decimalResultCol, metaCol);

        mainPane.setRight(resultsTable);

        fileInputOptional = Optional.empty();

        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Select file");

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

        sampleTestTField.textProperty().addListener((obs, oldVal, newVal) ->
                processTextInputChanges(sampleTestTField, newVal));

        sampleTestTField.disableProperty().bind(sampleTestCBox.selectedProperty().not());
        evenRButton.disableProperty().bind(sampleTestCBox.selectedProperty().not());
        oddRButton.disableProperty().bind(sampleTestCBox.selectedProperty().not());
        jacobiLabel.disableProperty().bind(sampleTestCBox.selectedProperty().not());
        jacobiSlider.disableProperty().bind(sampleTestCBox.selectedProperty().not());

        encryptButton.setOnAction(this::processEncrypt);
        decryptButton.setOnAction(this::processDecrypt);
        fileUploadButton.setOnAction(this::uploadFile);
        cancelFileButton.setOnAction(this::cancelFile);
        clearHistoryButton.setOnAction(e -> resultsTable.getItems().clear());
    }

    private void cancelFile(ActionEvent actionEvent) {
        fileInputOptional = Optional.empty();
        filenameLabel.setText("Filename: ");
    }

    private void processDecrypt(ActionEvent actionEvent) {
        String pText = pTArea.getText();
        String qText = qTArea.getText();
        String bText = bTArea.getText();
        String cText = sampleTestTField.getText();

        BigInteger p = new BigInteger(pText);
        BigInteger q = new BigInteger(qText);
        BigInteger b = new BigInteger(bText);
        BigInteger n = p.multiply(q);

        try {
            if (sampleTestCBox.isSelected()) {
                appModel.validateNumberSource(pText, qText, bText, cText);
                int evenOdd = oddRButton.isSelected() ? 1 : 0;
                int jacobi = ((int) jacobiSlider.getValue() + 1) / 2;
                BigInteger c = new BigInteger(cText);
                runTask(() ->
                        appModel.decryptNumber(p, q, b, n, jacobi, evenOdd, c));
            } else {
                appModel.validateFileSource(pText, qText, bText);
                if (fileInputOptional.isPresent()) {
                    saveFile().ifPresent(file ->
                            runTask(() -> appModel.decryptFile(fileInputOptional.get(), file, b, n, p, q)));
                } else {
                    AlertService.showAlert(Alert.AlertType.ERROR, "File not found error",
                            "Message", "Specify the source file.");
                }
            }
        } catch (RabinInvalidDataException e) {
            AlertService.showAlert(Alert.AlertType.ERROR, "Data validation error", "Message", e.getMessage());
        }
    }

    private void processEncrypt(ActionEvent actionEvent) {
        String pText = pTArea.getText();
        String qText = qTArea.getText();
        String bText = bTArea.getText();
        String mText = sampleTestTField.getText();

        BigInteger p = new BigInteger(pText);
        BigInteger q = new BigInteger(qText);
        BigInteger b = new BigInteger(bText);
        BigInteger n = p.multiply(q);

        try {
            if (sampleTestCBox.isSelected()) {
                appModel.validateNumberSource(pText, qText, bText, mText);
                BigInteger m = new BigInteger(mText);
                runTask(() ->
                        appModel.encryptNumber(m, b, n));
            } else {
                appModel.validateFileSource(pText, qText, bText);
                if (fileInputOptional.isPresent()) {
                    saveFile().ifPresent(fileOutput ->
                            runTask(() -> appModel.encryptFile(fileInputOptional.get(), fileOutput, b, n)));
                } else {
                    AlertService.showAlert(Alert.AlertType.ERROR, "File not found error",
                            "Message", "Specify the source file.");
                }
            }
        } catch (RabinInvalidDataException e) {
            AlertService.showAlert(Alert.AlertType.ERROR, "Data validation error",
                    "Message", e.getMessage());
        }
    }

    private void runTask(Supplier<Task<List<ResultModel>>> taskSupplier) {
        Task<List<ResultModel>> task = taskSupplier.get();
        ProgressForm pForm = new ProgressForm();
        task.setOnSucceeded(event -> {
            Worker<List<ResultModel>> source = event.getSource();
            List<ResultModel> value = source.getValue();
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

    private void updateResults(List<ResultModel> value) {
        ObservableList<ResultModel> resultModelObservableList = FXCollections.observableArrayList(value);
        ObservableList<ResultModel> items = resultsTable.getItems();
        if (!items.isEmpty()) {
            items.add(tableRowsSeparator);
        }
        items.addAll(resultModelObservableList);
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
        File saveFile = fileChooser.showSaveDialog(stage);
        return Optional.ofNullable(saveFile);
    }

    private void uploadFile(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(stage);
        Optional<File> fileInputOptional = Optional.ofNullable(file);
        fileInputOptional.ifPresent(fileInput -> {
            filenameLabel.setText("Filename: " + file.getName());
            this.fileInputOptional = fileInputOptional;
        });
    }
}
