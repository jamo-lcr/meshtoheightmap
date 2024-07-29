package com.releasemeshtoheightmap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gui extends Application {
    static Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setMainWindow(primaryStage);
        primaryStage.setTitle("Meshtoheigthmap(Suported:Obj,  ):  )");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void startGui(String[] args) {
        launch(args);
        System.exit(0);
    }
    public static Controller getcontroller(){
        return controller;
    }
}