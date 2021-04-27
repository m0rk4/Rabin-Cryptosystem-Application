package by.bsuir.m0rk4.it.task.third;

import by.bsuir.m0rk4.it.task.third.controller.PrimaryController;
import by.bsuir.m0rk4.it.task.third.crypto.RabinCryptoSystem;
import by.bsuir.m0rk4.it.task.third.crypto.RabinFileProcessor;
import by.bsuir.m0rk4.it.task.third.crypto.RabinNumberProcessor;
import by.bsuir.m0rk4.it.task.third.data.RabinFileSourceDataValidator;
import by.bsuir.m0rk4.it.task.third.data.RabinNumSourceDataValidator;
import by.bsuir.m0rk4.it.task.third.data.parser.RabinDataParser;
import by.bsuir.m0rk4.it.task.third.data.primetesting.PrimeTester;
import by.bsuir.m0rk4.it.task.third.model.AppModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private static final String VIEW_NAME = "primary.fxml";
    private static final String APP_TITLE = "IT - 3";

    @Override
    public void start(Stage stage) throws IOException {
        URL appViewUrl = App.class.getResource(VIEW_NAME);
        FXMLLoader fxmlLoader = new FXMLLoader(appViewUrl);

        PrimeTester primeTester = new PrimeTester();
        RabinDataParser rabinDataParser = new RabinDataParser();
        RabinCryptoSystem rabinCryptoSystem = new RabinCryptoSystem();

        RabinFileProcessor rabinFileProcessor = new RabinFileProcessor(rabinCryptoSystem);
        RabinNumberProcessor rabinNumberProcessor = new RabinNumberProcessor(rabinCryptoSystem);

        RabinNumSourceDataValidator rabinNumSourceDataValidator =
                new RabinNumSourceDataValidator(rabinDataParser, primeTester);
        RabinFileSourceDataValidator rabinFileSourceDataValidator =
                new RabinFileSourceDataValidator(rabinDataParser, primeTester);
        AppModel appModel = new AppModel(rabinFileSourceDataValidator, rabinNumSourceDataValidator,
                rabinFileProcessor, rabinNumberProcessor);

        PrimaryController primaryController = new PrimaryController(stage, appModel);
        fxmlLoader.setControllerFactory(c -> primaryController);

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(APP_TITLE);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
