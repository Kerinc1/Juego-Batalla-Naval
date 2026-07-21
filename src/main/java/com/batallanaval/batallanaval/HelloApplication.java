package com.batallanaval.batallanaval;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("placement-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Batalla Naval - Colocación de barcos");
        stage.setScene(scene);
        stage.setMinWidth(1400);
        stage.setMinHeight(860);
        stage.setMaximized(true);
        stage.show();
    }
}
