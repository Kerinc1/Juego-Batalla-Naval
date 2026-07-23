package com.batallanaval.batallanaval.controller;

import com.batallanaval.batallanaval.model.Board;
import com.batallanaval.batallanaval.model.GamePersistence;
import com.batallanaval.batallanaval.model.GameState;
import com.batallanaval.batallanaval.model.IAttackStrategy;
import com.batallanaval.batallanaval.model.Posicion;
import com.batallanaval.batallanaval.model.RandomAIMoveStrategy;
import com.batallanaval.batallanaval.model.Ship;
import com.batallanaval.batallanaval.model.ShotResult;
import com.batallanaval.batallanaval.util.Constantes;
import com.batallanaval.batallanaval.HelloApplication;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.io.IOException;

/**
 * Controlador de la pantalla principal de juego.
 */
public class GameController {
    @FXML
    private Pane playerBoardPane;
    @FXML
    private Pane enemyBoardPane;
    @FXML
    private Label statusLabel;
    @FXML
    private Button volverInicioButton;

    private Board playerBoard;
    private Board enemyBoard;
    private Rectangle[][] enemyRectangles = new Rectangle[Constantes.TABLERO_TAMAÑO][Constantes.TABLERO_TAMAÑO];
    private Rectangle[][] playerRectangles = new Rectangle[Constantes.TABLERO_TAMAÑO][Constantes.TABLERO_TAMAÑO];
    private boolean jugadorTurno;
    private boolean partidaActiva;
    private String jugadorNickname = "Jugador";
    private final IAttackStrategy aiStrategy = new RandomAIMoveStrategy();
    private final GamePersistence persistence = new GamePersistence();

    @FXML
    public void initialize() {
        playerBoard = new Board();
        enemyBoard = new Board();
        jugadorTurno = true;
        partidaActiva = false;
        volverInicioButton.setDisable(true);

        configurarTablero(playerBoardPane, playerBoard, true);
        configurarTablero(enemyBoardPane, enemyBoard, false);
        // No se cargan barcos enemigos mockeados aquí.
    }

    private void configurarTablero(Pane tableroPane, Board board, boolean mostrarBarcos) {
        double tamañoTotal = (Constantes.TAMAÑO_CASILLA * Constantes.TABLERO_TAMAÑO) + 70;
        tableroPane.setPrefSize(tamañoTotal, tamañoTotal);
        tableroPane.setMinSize(tamañoTotal, tamañoTotal);
        tableroPane.setMaxSize(tamañoTotal, tamañoTotal);

        Rectangle fondo = new Rectangle(tamañoTotal, tamañoTotal);
        fondo.setFill(Color.web("#EAF7FC"));
        fondo.setStroke(Color.web("#2F5066"));
        fondo.setStrokeWidth(2);
        fondo.setArcWidth(10);
        fondo.setArcHeight(10);
        tableroPane.getChildren().add(fondo);

        for (int i = 0; i < Constantes.TABLERO_TAMAÑO; i++) {
            Label etiquetaFila = new Label(String.valueOf(i + 1));
            etiquetaFila.setStyle("-fx-font-size: 10; -fx-text-fill: #333333; -fx-font-weight: bold;");
            etiquetaFila.setPrefWidth(24);
            etiquetaFila.setPrefHeight(Constantes.TAMAÑO_CASILLA);
            etiquetaFila.setLayoutX(8);
            etiquetaFila.setLayoutY(35 + i * Constantes.TAMAÑO_CASILLA);
            tableroPane.getChildren().add(etiquetaFila);
        }

        String[] columnas = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        for (int j = 0; j < Constantes.TABLERO_TAMAÑO; j++) {
            Label etiquetaColumna = new Label(columnas[j]);
            etiquetaColumna.setStyle("-fx-font-size: 10; -fx-text-fill: #333333; -fx-font-weight: bold;");
            etiquetaColumna.setPrefWidth(Constantes.TAMAÑO_CASILLA);
            etiquetaColumna.setPrefHeight(24);
            etiquetaColumna.setLayoutX(35 + j * Constantes.TAMAÑO_CASILLA);
            etiquetaColumna.setLayoutY(8);
            etiquetaColumna.setAlignment(javafx.geometry.Pos.CENTER);
            tableroPane.getChildren().add(etiquetaColumna);
        }

        for (int fila = 0; fila < Constantes.TABLERO_TAMAÑO; fila++) {
            for (int columna = 0; columna < Constantes.TABLERO_TAMAÑO; columna++) {
                int x = 35 + columna * Constantes.TAMAÑO_CASILLA;
                int y = 35 + fila * Constantes.TAMAÑO_CASILLA;
                Rectangle celda = crearCasilla(x, y);
                if (mostrarBarcos) {
                    playerRectangles[fila][columna] = celda;
                } else {
                    enemyRectangles[fila][columna] = celda;
                    celda.setOnMouseEntered(event -> celda.setFill(Color.web("#90D4F0")));
                    celda.setOnMouseExited(event -> casillaFillOnHover(celda));
                    celda.setCursor(Cursor.HAND);
                    final int filaFinal = fila;
                    final int columnaFinal = columna;
                    celda.setOnMouseClicked(event -> manejarDisparo(event, filaFinal, columnaFinal, celda));
                }
                tableroPane.getChildren().add(celda);
            }
        }
    }

    private Rectangle crearCasilla(double x, double y) {
        Rectangle casilla = new Rectangle(Constantes.TAMAÑO_CASILLA - 2, Constantes.TAMAÑO_CASILLA - 2);
        casilla.setFill(Color.web("#B8E0F0"));
        casilla.setStroke(Color.web("#666666"));
        casilla.setStrokeWidth(1);
        casilla.setLayoutX(x);
        casilla.setLayoutY(y);
        return casilla;
    }

    private void manejarDisparo(MouseEvent event, int fila, int columna, Rectangle casilla) {
        if (!jugadorTurno || !partidaActiva) {
            return;
        }
        if (casilla.getUserData() != null) {
            mostrarAlerta("Atención", "La casilla ya fue utilizada.");
            return;
        }
        try {
            ShotResult resultado = enemyBoard.shoot(fila, columna);
            casilla.setUserData(resultado);
            casilla.setDisable(true);
            mostrarResultadoVisual(casilla, fila, columna, resultado);

            if (resultado == ShotResult.WATER) {
                jugadorTurno = false;
                statusLabel.setText("Agua. Turno de la máquina.");
                guardarEstado();
                ejecutarTurnoMaquina();
            } else if (resultado == ShotResult.HIT) {
                statusLabel.setText("Tocado. Dispara nuevamente.");
                guardarEstado();
            } else if (resultado == ShotResult.SUNK) {
                statusLabel.setText("Hundido. Continúa disparando.");
                guardarEstado();
                if (enemyBoard.allShipsSunk()) {
                    mostrarVictoria();
                }
            }
        } catch (IllegalArgumentException ex) {
            mostrarAlerta("Atención", ex.getMessage());
        }
    }

    private void mostrarResultadoVisual(Rectangle casilla, int fila, int columna, ShotResult resultado) {
        if (resultado == ShotResult.WATER) {
            casilla.setFill(Color.web("#AED6F1"));
            Line linea1 = new Line(casilla.getLayoutX() + 10, casilla.getLayoutY() + 10,
                    casilla.getLayoutX() + Constantes.TAMAÑO_CASILLA - 12,
                    casilla.getLayoutY() + Constantes.TAMAÑO_CASILLA - 12);
            linea1.setStroke(Color.web("#4F5A65"));
            linea1.setStrokeWidth(2);
            Line linea2 = new Line(casilla.getLayoutX() + Constantes.TAMAÑO_CASILLA - 12,
                    casilla.getLayoutY() + 10,
                    casilla.getLayoutX() + 10,
                    casilla.getLayoutY() + Constantes.TAMAÑO_CASILLA - 12);
            linea2.setStroke(Color.web("#4F5A65"));
            linea2.setStrokeWidth(2);
            enemyBoardPane.getChildren().addAll(linea1, linea2);
            animarImpacto(linea1, linea2);
        } else if (resultado == ShotResult.HIT) {
            casilla.setFill(Color.web("#F1948A"));
            Circle punto = new Circle(casilla.getLayoutX() + Constantes.TAMAÑO_CASILLA / 2 - 1,
                    casilla.getLayoutY() + Constantes.TAMAÑO_CASILLA / 2 - 1, 8);
            punto.setFill(Color.web("#E74C3C"));
            enemyBoardPane.getChildren().add(punto);
            animarImpacto(punto);
        } else if (resultado == ShotResult.SUNK) {
            Ship barcoHundido = enemyBoard.getCell(fila, columna).getShip();
            if (barcoHundido != null) {
                for (Posicion pos : barcoHundido.getCasillasOcupadas()) {
                    Rectangle objetivo = enemyRectangles[pos.getFila()][pos.getColumna()];
                    if (objetivo != null) {
                        objetivo.setDisable(true);
                        objetivo.setUserData(ShotResult.SUNK);
                        objetivo.setFill(Color.web("#7F8790"));
                    }
                    Circle impacto = new Circle(35 + pos.getColumna() * Constantes.TAMAÑO_CASILLA + Constantes.TAMAÑO_CASILLA / 2 - 1,
                            35 + pos.getFila() * Constantes.TAMAÑO_CASILLA + Constantes.TAMAÑO_CASILLA / 2 - 1, 6);
                    impacto.setFill(Color.web("#E74C3C"));
                    enemyBoardPane.getChildren().add(impacto);
                }
                animarImpacto();
            }
        }
    }

    private void mostrarResultadoMaquina(Posicion posicion, ShotResult resultado) {
        Rectangle casilla = playerRectangles[posicion.getFila()][posicion.getColumna()];
        if (casilla == null) {
            return;
        }
        casilla.setUserData(resultado);
        casilla.setDisable(true);
        if (resultado == ShotResult.WATER) {
            casilla.setFill(Color.web("#AED6F1"));
            Line linea1 = new Line(casilla.getLayoutX() + 10, casilla.getLayoutY() + 10,
                    casilla.getLayoutX() + Constantes.TAMAÑO_CASILLA - 12,
                    casilla.getLayoutY() + Constantes.TAMAÑO_CASILLA - 12);
            linea1.setStroke(Color.web("#4F5A65"));
            linea1.setStrokeWidth(2);
            Line linea2 = new Line(casilla.getLayoutX() + Constantes.TAMAÑO_CASILLA - 12,
                    casilla.getLayoutY() + 10,
                    casilla.getLayoutX() + 10,
                    casilla.getLayoutY() + Constantes.TAMAÑO_CASILLA - 12);
            linea2.setStroke(Color.web("#4F5A65"));
            linea2.setStrokeWidth(2);
            playerBoardPane.getChildren().addAll(linea1, linea2);
            animarImpacto(linea1, linea2);
        } else if (resultado == ShotResult.HIT) {
            casilla.setFill(Color.web("#F1948A"));
            Circle punto = new Circle(casilla.getLayoutX() + Constantes.TAMAÑO_CASILLA / 2 - 1,
                    casilla.getLayoutY() + Constantes.TAMAÑO_CASILLA / 2 - 1, 8);
            punto.setFill(Color.web("#E74C3C"));
            playerBoardPane.getChildren().add(punto);
            animarImpacto(punto);
            statusLabel.setText("La máquina acertó en " + obtenerCoordenada(posicion) + ".");
        } else if (resultado == ShotResult.SUNK) {
            Ship barcoHundido = playerBoard.getCell(posicion.getFila(), posicion.getColumna()).getShip();
            if (barcoHundido != null) {
                for (Posicion pos : barcoHundido.getCasillasOcupadas()) {
                    Rectangle objetivo = playerRectangles[pos.getFila()][pos.getColumna()];
                    if (objetivo != null) {
                        objetivo.setDisable(true);
                        objetivo.setUserData(ShotResult.SUNK);
                        objetivo.setFill(Color.web("#7F8790"));
                    }
                    Circle impacto = new Circle(35 + pos.getColumna() * Constantes.TAMAÑO_CASILLA + Constantes.TAMAÑO_CASILLA / 2 - 1,
                            35 + pos.getFila() * Constantes.TAMAÑO_CASILLA + Constantes.TAMAÑO_CASILLA / 2 - 1, 6);
                    impacto.setFill(Color.web("#E74C3C"));
                    playerBoardPane.getChildren().add(impacto);
                }
                animarImpacto();
                statusLabel.setText("La máquina hundió un barco en " + obtenerCoordenada(posicion) + ".");
            }
        }
    }

    private String obtenerCoordenada(Posicion posicion) {
        char columna = (char) ('A' + posicion.getColumna());
        int fila = posicion.getFila() + 1;
        return columna + String.valueOf(fila);
    }

    private void casillaFillOnHover(Rectangle casilla) {
        if (casilla.getUserData() == ShotResult.SUNK) {
            casilla.setFill(Color.web("#7F8790"));
        } else if (casilla.getUserData() == ShotResult.HIT) {
            casilla.setFill(Color.web("#F1948A"));
        } else if (casilla.getUserData() == ShotResult.WATER || casilla.isDisabled()) {
            casilla.setFill(Color.web("#AED6F1"));
        } else {
            casilla.setFill(Color.web("#B8E0F0"));
        }
    }

    private void animarImpacto(javafx.scene.Node... nodos) {
        for (javafx.scene.Node nodo : nodos) {
            FadeTransition transicion = new FadeTransition(Duration.millis(180), nodo);
            transicion.setFromValue(0.0);
            transicion.setToValue(1.0);
            transicion.play();
        }
    }

    private void mostrarVictoria() {
        jugadorTurno = false;
        partidaActiva = false;
        volverInicioButton.setDisable(false);
        statusLabel.setText("Partida finalizada. Has ganado.");
        guardarEstado();
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Victoria");
        alerta.setHeaderText("¡Has ganado!");
        alerta.setContentText("Todos los barcos enemigos han sido hundidos.");
        mostrarOpcionesFinales(alerta);
    }

    private void mostrarDerrota() {
        jugadorTurno = false;
        partidaActiva = false;
        volverInicioButton.setDisable(false);
        statusLabel.setText("Partida finalizada. Ha ganado la máquina.");
        guardarEstado();
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Derrota");
        alerta.setHeaderText("¡Has perdido!");
        alerta.setContentText("Tu tropa ha sido destruida.");
        mostrarOpcionesFinales(alerta);
    }

    private void mostrarOpcionesFinales(Alert alerta) {
        ButtonType volver = new ButtonType("Volver al inicio");
        ButtonType cerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(volver, cerrar);
        alerta.showAndWait().ifPresent(opcion -> {
            if (opcion == volver) {
                volverAlInicio();
            }
        });
    }

    public void guardarEstado() {
        GameState estado = new GameState(playerBoard, enemyBoard, jugadorTurno, jugadorNickname);
        persistence.guardar(estado, jugadorNickname);
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public void setPlayerShips(List<Ship> ships) {
        for (Ship ship : ships) {
            playerBoard.addShip(ship);
        }
        mostrarBarcos(playerBoard, playerBoardPane);
    }

    private void colocarBarcosEnemigo() {
        // No se colocan barcos enemigos aquí. Esta pantalla actualmente solo muestra la mecánica de disparo.
    }

    private void mostrarBarcos(Board board, Pane tableroPane) {
        for (Ship barco : board.getShips()) {
            for (Posicion posicion : barco.getCasillasOcupadas()) {
                Rectangle segmento = new Rectangle(Constantes.TAMAÑO_CASILLA - 6, Constantes.TAMAÑO_CASILLA - 6);
                segmento.setFill(Color.web("#7F8790"));
                segmento.setStroke(Color.web("#4A4E55"));
                segmento.setStrokeWidth(1.5);
                segmento.setLayoutX(35 + posicion.getColumna() * Constantes.TAMAÑO_CASILLA + 3);
                segmento.setLayoutY(35 + posicion.getFila() * Constantes.TAMAÑO_CASILLA + 3);
                tableroPane.getChildren().add(segmento);
            }
        }
    }

    public void setEnemyBoard(Board enemyBoard) {
        this.enemyBoard = enemyBoard;
        aiStrategy.reiniciar(this.playerBoard);
        if (this.playerBoard != null && this.enemyBoard != null) {
            comenzarPartida();
        }
    }

    public void setPlayerNickname(String jugadorNickname) {
        this.jugadorNickname = jugadorNickname;
    }

    public void cargarEstado(GameState estado) {
        this.playerBoard = estado.getPlayerBoard();
        this.enemyBoard = estado.getEnemyBoard();
        this.jugadorTurno = estado.isJugadorTurno();
        this.jugadorNickname = estado.getJugadorNickname();
        this.partidaActiva = !estado.estaTerminada();
        this.aiStrategy.reiniciar(playerBoard);
        playerBoardPane.getChildren().clear();
        enemyBoardPane.getChildren().clear();
        configurarTablero(playerBoardPane, playerBoard, true);
        configurarTablero(enemyBoardPane, enemyBoard, false);
        mostrarBarcos(playerBoard, playerBoardPane);
        restaurarDisparos(playerBoard, playerRectangles, playerBoardPane, false);
        restaurarDisparos(enemyBoard, enemyRectangles, enemyBoardPane, true);
        statusLabel.setText(jugadorTurno ? "Turno del jugador." : "Turno de la máquina.");
        if (!jugadorTurno) {
            ejecutarTurnoMaquina();
        }
    }

    private void comenzarPartida() {
        playerBoardPane.getChildren().clear();
        enemyBoardPane.getChildren().clear();
        configurarTablero(playerBoardPane, playerBoard, true);
        configurarTablero(enemyBoardPane, enemyBoard, false);
        mostrarBarcos(playerBoard, playerBoardPane);
        jugadorTurno = true;
        partidaActiva = true;
        statusLabel.setText("Turno del jugador. Dispara sobre el tablero enemigo.");
    }

    private void ejecutarTurnoMaquina() {
        if (!partidaActiva) {
            return;
        }
        Posicion seleccion = aiStrategy.seleccionarSiguienteDisparo(playerBoard);
        try {
            ShotResult resultado = playerBoard.shoot(seleccion.getFila(), seleccion.getColumna());
            mostrarResultadoMaquina(seleccion, resultado);
            if (resultado == ShotResult.WATER) {
                jugadorTurno = true;
                statusLabel.setText("La máquina falló en " + obtenerCoordenada(seleccion) + ". Tu turno.");
            } else if (resultado == ShotResult.HIT) {
                if (playerBoard.allShipsSunk()) {
                    mostrarDerrota();
                    return;
                }
                statusLabel.setText("La máquina acertó en " + obtenerCoordenada(seleccion) + ". Sigue disparando.");
                ejecutarTurnoMaquina();
                return;
            } else if (resultado == ShotResult.SUNK) {
                if (playerBoard.allShipsSunk()) {
                    mostrarDerrota();
                    return;
                }
                statusLabel.setText("La máquina hundió un barco en " + obtenerCoordenada(seleccion) + ". Sigue disparando.");
                ejecutarTurnoMaquina();
                return;
            }
        } catch (IllegalArgumentException ex) {
            ejecutarTurnoMaquina();
            return;
        }
        guardarEstado();
    }

    private void restaurarDisparos(Board board, Rectangle[][] rectangles, Pane pane, boolean esTableroEnemigo) {
        for (int fila = 0; fila < board.getTamaño(); fila++) {
            for (int columna = 0; columna < board.getTamaño(); columna++) {
                if (!board.isCellDisparada(fila, columna)) {
                    continue;
                }
                Rectangle casilla = rectangles[fila][columna];
                ShotResult resultado = obtenerResultadoGuardado(board, fila, columna);
                casilla.setUserData(resultado);
                casilla.setDisable(true);
                if (resultado == ShotResult.SUNK && !esPrimeraCasillaDelBarco(board, fila, columna)) {
                    continue;
                }
                if (esTableroEnemigo) {
                    mostrarResultadoVisual(casilla, fila, columna, resultado);
                } else {
                    mostrarResultadoMaquina(new Posicion(fila, columna), resultado);
                }
            }
        }
    }

    private ShotResult obtenerResultadoGuardado(Board board, int fila, int columna) {
        if (!board.getCell(fila, columna).tieneBarco()) {
            return ShotResult.WATER;
        }
        Ship barco = board.getCell(fila, columna).getShip();
        return barco.estaHundido() ? ShotResult.SUNK : ShotResult.HIT;
    }

    private boolean esPrimeraCasillaDelBarco(Board board, int fila, int columna) {
        Ship barco = board.getCell(fila, columna).getShip();
        if (barco == null) {
            return true;
        }
        Posicion primera = barco.getCasillasOcupadas().stream()
                .min((izquierda, derecha) -> {
                    int comparacionFila = Integer.compare(izquierda.getFila(), derecha.getFila());
                    return comparacionFila != 0 ? comparacionFila : Integer.compare(izquierda.getColumna(), derecha.getColumna());
                })
                .orElse(new Posicion(fila, columna));
        return primera.getFila() == fila && primera.getColumna() == columna;
    }

    @FXML
    private void volverAlInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("placement-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) volverInicioButton.getScene().getWindow();
            stage.setTitle("Batalla Naval - Colocación de barcos");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            mostrarAlerta("Error", "No se pudo volver a la pantalla inicial.");
        }
    }
}
