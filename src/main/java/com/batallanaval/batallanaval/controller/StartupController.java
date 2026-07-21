package com.batallanaval.batallanaval.controller;

import com.batallanaval.batallanaval.HelloApplication;
import com.batallanaval.batallanaval.model.GamePersistence;
import com.batallanaval.batallanaval.model.GameState;
import com.batallanaval.batallanaval.model.RandomBoardGenerator;
import com.batallanaval.batallanaval.model.Board;
import com.batallanaval.batallanaval.util.Constantes;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador de la pantalla de inicio del juego.
 */
public class StartupController {
    @FXML
    private TextField nicknameField;
    @FXML
    private Button btnNuevoJuego;
    @FXML
    private Button btnCargarPartida;
    @FXML
    private Button btnMostrarTablero;
    @FXML
    private Label lblEstado;

    private final GamePersistence persistence = new GamePersistence();

    @FXML
    public void initialize() {
        btnCargarPartida.setDisable(!persistence.existePartidaGuardada());
        btnNuevoJuego.setOnAction(e -> iniciarNuevaPartida());
        btnCargarPartida.setOnAction(e -> cargarPartida());
        btnMostrarTablero.setOnAction(e -> mostrarTableroOponente());
    }

    private void iniciarNuevaPartida() {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            lblEstado.setText("Debes ingresar un nickname para iniciar una nueva partida.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/batallanaval/batallanaval/placement-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnNuevoJuego.getScene().getWindow();
            stage.setTitle("Batalla Naval - Colocación de barcos");
            stage.setScene(scene);
            stage.setMinWidth(1020);
            stage.setMinHeight(750);
            PlacementController controller = loader.getController();
            controller.setNickname(nickname);
            stage.show();
        } catch (IOException ex) {
            lblEstado.setText("No se pudo iniciar la pantalla de colocación.");
        }
    }

    private void cargarPartida() {
        try {
            GameState estado = persistence.cargar();
            if (estado == null || estado.estaTerminada()) {
                lblEstado.setText("No hay un juego válido para cargar. Inicia una nueva partida.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/batallanaval/batallanaval/game-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnCargarPartida.getScene().getWindow();
            stage.setTitle("Batalla Naval - Partida cargada");
            stage.setScene(scene);
            stage.setMinWidth(1040);
            stage.setMinHeight(760);
            GameController controller = loader.getController();
            controller.cargarEstado(estado);
            stage.show();
        } catch (IOException ex) {
            lblEstado.setText("No se pudo cargar la partida guardada.");
        }
    }

    private void mostrarTableroOponente() {
        GameState estado = null;
        if (persistence.existePartidaGuardada()) {
            estado = persistence.cargar();
        }
        Board tablero = estado != null ? estado.getEnemyBoard() : new RandomBoardGenerator().generarTableroEnemigo();
        Stage stage = new Stage();
        stage.setTitle("Tablero oponente (verificación)");

        Pane pane = new Pane();
        pane.setPrefSize(420, 420);
        pane.setStyle("-fx-background-color: #EAF7FC; -fx-border-color: #2F5066; -fx-border-width: 2; -fx-padding: 12;");

        double offset = 35;
        for (int i = 0; i < 10; i++) {
            Label etiqueta = new Label(String.valueOf(i + 1));
            etiqueta.setStyle("-fx-font-size: 10; -fx-text-fill: #333333; -fx-font-weight: bold;");
            etiqueta.setLayoutX(8);
            etiqueta.setLayoutY(offset + i * Constantes.TAMAÑO_CASILLA + 10);
            pane.getChildren().add(etiqueta);
        }
        String[] columnas = {"A","B","C","D","E","F","G","H","I","J"};
        for (int j = 0; j < 10; j++) {
            Label etiqueta = new Label(columnas[j]);
            etiqueta.setStyle("-fx-font-size: 10; -fx-text-fill: #333333; -fx-font-weight: bold;");
            etiqueta.setLayoutX(offset + j * Constantes.TAMAÑO_CASILLA + 12);
            etiqueta.setLayoutY(8);
            pane.getChildren().add(etiqueta);
        }

        for (int fila = 0; fila < 10; fila++) {
            for (int columna = 0; columna < 10; columna++) {
                Rectangle celda = new Rectangle(Constantes.TAMAÑO_CASILLA - 2, Constantes.TAMAÑO_CASILLA - 2);
                celda.setFill(Color.web("#B8E0F0"));
                celda.setStroke(Color.web("#666666"));
                celda.setStrokeWidth(1);
                celda.setLayoutX(offset + columna * Constantes.TAMAÑO_CASILLA);
                celda.setLayoutY(offset + fila * Constantes.TAMAÑO_CASILLA);
                if (tablero.getCell(fila, columna).tieneBarco()) {
                    celda.setFill(Color.web("#7F8790"));
                }
                pane.getChildren().add(celda);
            }
        }

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setMinWidth(480);
        stage.setMinHeight(480);
        stage.show();
    }
}
