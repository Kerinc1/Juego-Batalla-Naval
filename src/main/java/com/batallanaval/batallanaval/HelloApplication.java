package com.batallanaval.batallanaval;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("startup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Batalla Naval - Inicio");
        stage.setScene(scene);
        stage.setMinWidth(700);
        stage.setMinHeight(420);
        stage.show();
    }
}
