package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage primaryStage;
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/SalesView.fxml"));
        Scene scene = new Scene(root);

        Main.primaryStage = stage;
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Retailing System");
        stage.show();
    }
}
