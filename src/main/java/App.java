package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controller.MDVRPController;

public class App extends Application {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 576;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/view/graph.fxml"));
        Parent root = loader.load();
        MDVRPController controller = loader.getController();
        controller.setStage(stage);
        controller.initialize(stage);
        stage.setTitle("MDVRP");
        stage.setScene(new Scene(root, WIDTH, HEIGHT));
        stage.setResizable(false);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
