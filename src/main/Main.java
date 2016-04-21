package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private final static String APP_NAME = "Spectre Analysis Tool";
    public final static String SCENE_PARENT = "main.fxml";
    private final static int BASIC_HEIGHT = 1000;
    private final static int BASIC_LENGTH = 1500;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(SCENE_PARENT));
        primaryStage.setTitle(APP_NAME);
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, BASIC_LENGTH, BASIC_HEIGHT));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
