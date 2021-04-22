package by.bsuir.m0rk4.it.task.third;

import by.bsuir.m0rk4.it.task.third.controller.PrimaryController;
import by.bsuir.m0rk4.it.task.third.crypto.RabinCryptoSystem;
import by.bsuir.m0rk4.it.task.third.primetesting.PrimeTester;
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

        RabinCryptoSystem rabinCryptoSystem = new RabinCryptoSystem();
        PrimeTester primeTester = new PrimeTester();
        PrimaryController primaryController = new PrimaryController(primeTester, rabinCryptoSystem);
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