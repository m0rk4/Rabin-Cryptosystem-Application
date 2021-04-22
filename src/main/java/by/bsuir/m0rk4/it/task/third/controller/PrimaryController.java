package by.bsuir.m0rk4.it.task.third.controller;

import by.bsuir.m0rk4.it.task.third.crypto.RabinCryptoSystem;
import by.bsuir.m0rk4.it.task.third.primetesting.PrimeTester;
import by.bsuir.m0rk4.it.task.third.uicomponents.ProgressForm;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class PrimaryController {

    public TextArea pTArea;
    public StackPane pPrimeBackgrondPane;
    public Label pStatusLabel;

    public TextArea qTArea;
    public StackPane qPrimeBackgrondPane;
    public Label qStatusLabel;

    public TextArea bTArea;
    public StackPane bPrimeBackgrondPane;
    public Label bStatusLabel;

    public TextArea nTArea;

    public Button fileUploadButton;

    public Button encryptButton;
    public Button decryptButton;

    public Label filenameLabel;
    public Label blockSizeLabel;
    public Button cancelFileButton;
    public VBox resultContainer;

    public PrimaryController(PrimeTester primeTester, RabinCryptoSystem rabinCryptoSystem) {
        this.primeTester = primeTester;
        this.rabinCryptoSystem = rabinCryptoSystem;
    }


    private final RabinCryptoSystem rabinCryptoSystem;
    private final PrimeTester primeTester;
    private File fileInput;

    @FXML
    void initialize() {
        pTArea.textProperty().addListener((obs, oldVal, newVal) -> {
            processTextAreaChanges(pTArea, newVal);
            calculateN();
        });
        qTArea.textProperty().addListener((obs, oldVal, newVal) -> {
            processTextAreaChanges(qTArea, newVal);
            calculateN();
        });
        bTArea.textProperty().addListener((obs, oldVal, newVal) -> processTextAreaChanges(bTArea, newVal));

        encryptButton.setOnAction(this::processEncrypt);
        decryptButton.setOnAction(this::processDecrypt);
        fileUploadButton.setOnAction(this::uploadFile);
        cancelFileButton.setOnAction(this::cancelFile);
    }



    private void cancelFile(ActionEvent actionEvent) {
        this.fileInput = null;
        filenameLabel.setText("Filename: ");
    }

    private void processDecrypt(ActionEvent actionEvent) {
        String pTAreaText = pTArea.getText();
        String qTAreaText = qTArea.getText();
        String bTAreaText = bTArea.getText();
        boolean pTAreaTextBlank = pTAreaText.isBlank();
        boolean qTAreaTextBlank = qTAreaText.isBlank();
        boolean bTAreaTextBlank = bTAreaText.isBlank();
        if (pTAreaTextBlank || qTAreaTextBlank || bTAreaTextBlank) {
            showAlert("Empty value",
                    (pTAreaTextBlank ? "p is empty.\n" : "") +
                            (qTAreaTextBlank ? "q is empty.\n" : "") +
                            (bTAreaTextBlank ? "b is empty.\n" : "")
            );
            return;
        }

        BigInteger p = new BigInteger(pTAreaText);
        BigInteger q = new BigInteger(qTAreaText);
        boolean pTest = primeTester.test(p, 10);
        boolean qTest = primeTester.test(q, 10);
        if (!pTest || !qTest) {
            showAlert("Prime test failed",
                    (!pTest ? "p failed prime test.\n" : "") +
                            (!qTest ? "q failed prime test.\n" : "")
            );
            return;
        }

        boolean pTestRemainder = primeTester.testRemainderMod4(p);
        boolean qTestRemainder = primeTester.testRemainderMod4(q);
        if (!pTestRemainder || !qTestRemainder) {
            showAlert("Rabin x mod 4 = 3 failed",
                    (!pTestRemainder ? "p failed Rabin necessity.\n" : "") +
                            (!qTestRemainder ? "q failed Rabin necessity.\n" : "")
            );
            return;
        }
        BigInteger n = p.multiply(q);
        if (n.compareTo(BigInteger.valueOf(255)) != 1) {
            showAlert("N range", "Impossible to get unique message!\nCrypto-level is low!");
            return;
        }
        BigInteger b = new BigInteger(bTAreaText);
        if (b.compareTo(n) > -1) {
            showAlert("Out of n",
                    "b value is >= n.");
            return;
        }

        if (fileInput == null) {
            showAlert("File not found",
                    "File wasn't specified.");
            return;
        }

        Optional<File> fileSaveOpt = saveFile();
        if (fileSaveOpt.isPresent()) {
            File fileOutput = fileSaveOpt.get();
            Task<List<String>> encryptTask = rabinCryptoSystem.decrypt(this.fileInput, fileOutput, b, n, p, q);
            ProgressForm pForm = new ProgressForm();
            encryptTask.setOnSucceeded(event -> {
                Worker<List<String>> source = event.getSource();
                List<String> value = source.getValue();

                resultContainer.getChildren().clear();
                value.forEach(v -> resultContainer.getChildren().add(new TextField(v)));

                encryptButton.setDisable(false);
                decryptButton.setDisable(false);
                pForm.getDialogStage().close();
            });
            encryptButton.setDisable(true);
            decryptButton.setDisable(true);
            pForm.activateProgressBar(encryptTask);
            Thread thread = new Thread(encryptTask);
            thread.start();
        }
    }

    private void processEncrypt(ActionEvent actionEvent) {
        String pTAreaText = pTArea.getText();
        String qTAreaText = qTArea.getText();
        String bTAreaText = bTArea.getText();
        boolean pTAreaTextBlank = pTAreaText.isBlank();
        boolean qTAreaTextBlank = qTAreaText.isBlank();
        boolean bTAreaTextBlank = bTAreaText.isBlank();
        if (pTAreaTextBlank || qTAreaTextBlank || bTAreaTextBlank) {
            showAlert("Empty value",
                    (pTAreaTextBlank ? "p is empty.\n" : "") +
                            (qTAreaTextBlank ? "q is empty.\n" : "") +
                            (bTAreaTextBlank ? "b is empty.\n" : "")
            );
            return;
        }

        BigInteger p = new BigInteger(pTAreaText);
        BigInteger q = new BigInteger(qTAreaText);
        boolean pTest = primeTester.test(p, 10);
        boolean qTest = primeTester.test(q, 10);
        if (!pTest || !qTest) {
            showAlert("Prime test failed",
                    (!pTest ? "p failed prime test.\n" : "") +
                            (!qTest ? "q failed prime test.\n" : "")
            );
            return;
        }

        boolean pTestRemainder = primeTester.testRemainderMod4(p);
        boolean qTestRemainder = primeTester.testRemainderMod4(q);
        if (!pTestRemainder || !qTestRemainder) {
            showAlert("Rabin x mod 4 = 3 failed",
                    (!pTestRemainder ? "p failed Rabin necessity.\n" : "") +
                            (!qTestRemainder ? "q failed Rabin necessity.\n" : "")
            );
            return;
        }

        BigInteger n = p.multiply(q);
        if (n.compareTo(BigInteger.valueOf(255)) != 1) {
            showAlert("N range", "Impossible to get unique message!\nCrypto-level is low!");
            return;
        }
        BigInteger b = new BigInteger(bTAreaText);
        if (b.compareTo(n) > -1) {
            showAlert("Out of n",
                    "b value is >= n.");
            return;
        }

        if (fileInput == null) {
            showAlert("File not found",
                    "File wasn't specified.");
            return;
        }

        Optional<File> fileSaveOpt = saveFile();
        if (fileSaveOpt.isPresent()) {
            File fileOutput = fileSaveOpt.get();
            Task<List<String>> encryptTask = rabinCryptoSystem.encrypt(this.fileInput, fileOutput, b, n);
            ProgressForm pForm = new ProgressForm();
            encryptTask.setOnSucceeded(event -> {
                Worker<List<String>> source = event.getSource();
                List<String> value = source.getValue();

                resultContainer.getChildren().clear();
                value.forEach(v -> resultContainer.getChildren().add(new TextField(v)));

                encryptButton.setDisable(false);
                decryptButton.setDisable(false);
                pForm.getDialogStage().close();
            });
            encryptButton.setDisable(true);
            decryptButton.setDisable(true);
            pForm.activateProgressBar(encryptTask);
            Thread thread = new Thread(encryptTask);
            thread.start();
        }
    }

    private Optional<File> saveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Select file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Any file (*.*)", "*.*")
        );
        return Optional.ofNullable(fileChooser.showSaveDialog(null));
    }

    private void calculateN() {
        String pText = pTArea.getText();
        if (pText.isBlank()) {
            nTArea.setText("");
            blockSizeLabel.setText("Block Size: ");
            return;
        }
        String qText = qTArea.getText();
        if (qText.isBlank()) {
            nTArea.setText("");
            blockSizeLabel.setText("Block Size: ");
            return;
        }
        BigInteger p = new BigInteger(pText);
        BigInteger q = new BigInteger(qText);
        BigInteger n = p.multiply(q);
        nTArea.setText(n.toString());
        int length = n.toByteArray().length;
        blockSizeLabel.setText("Block Size: " + (++length));
    }

    private void processTextAreaChanges(TextArea textArea, String newVal) {
        String filteredVal = newVal.replaceAll("[^\\d]+", "");
        textArea.setText(filteredVal);
    }

    private void uploadFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Select file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Any file (*.*)", "*.*")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            this.fileInput = file;
            filenameLabel.setText("Filename: " + file.getName());
        }
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
