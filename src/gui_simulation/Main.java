package gui_simulation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainGUI.fxml"));
        primaryStage.setTitle("KIS Simulation Roboter");
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
