package com.batallanaval.batallanaval.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import com.batallanaval.batallanaval.model.*;
import com.batallanaval.batallanaval.util.Constantes;
import com.batallanaval.batallanaval.util.DibujadorBarcos;

import java.io.IOException;
import java.util.*;

import com.batallanaval.batallanaval.model.RandomBoardGenerator;

/**
 * Controlador de la interfaz de colocación de barcos.
 */
public class PlacementController {
    @FXML
    private Pane tableroPane;
    @FXML
    private VBox panelIzquierdo;
    @FXML
    private VBox flotaDisponibleVBox;
    @FXML
    private Button botonRotar;
    @FXML
    private Button botonReiniciar;
    @FXML
    private Button botonIniciarPartida;
    @FXML
    private Label etiquetaEstado;
    @FXML
    private TextField nombreField;
    @FXML
    private Button botonMostrarTablero;

    private Flota flota;
    private Tablero tablero;
    private Barco barcoSeleccionado;
    private Barco barcoEnArrastre;
    private Map<String, Group> visualesBarcos;
    private Map<String, Node> nodosBarco;
    private String nickname;
    private Board tableroEnemigo;
    private final GamePersistence persistence = new GamePersistence();
    private final double margenCoordenadas = 35;

    /**
     * Inicializa el controlador y la interfaz.
     */
    @FXML
    public void initialize() {
        flota = new Flota();
        tablero = new Tablero();
        tableroEnemigo = new RandomBoardGenerator().generarTableroEnemigo();
        visualesBarcos = new HashMap<>();
        nodosBarco = new HashMap<>();
        barcoSeleccionado = null;
        barcoEnArrastre = null;

        configurarTablero();
        configurarFlotaDisponible();
        configurarBotones();
        configurarDragDropTablero();
        actualizarEstado();
        actualizarVisualesBarcos();
    }

    /**
     * Configura el tablero con la cuadrícula 10x10.
     */
    private void configurarTablero() {
        double tamañoTotal = (Constantes.TAMAÑO_CASILLA * Constantes.TABLERO_TAMAÑO) + (margenCoordenadas * 2);
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
            Label etiqueta = new Label(String.valueOf(i + 1));
            etiqueta.setStyle("-fx-font-size: 10; -fx-text-fill: #333333; -fx-font-weight: bold;");
            etiqueta.setPrefWidth(24);
            etiqueta.setPrefHeight(Constantes.TAMAÑO_CASILLA);
            etiqueta.setLayoutX(8);
            etiqueta.setLayoutY(margenCoordenadas + i * Constantes.TAMAÑO_CASILLA + 10);
            tableroPane.getChildren().add(etiqueta);
        }

        String[] columnas = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        for (int j = 0; j < Constantes.TABLERO_TAMAÑO; j++) {
            Label etiqueta = new Label(columnas[j]);
            etiqueta.setStyle("-fx-font-size: 10; -fx-text-fill: #333333; -fx-font-weight: bold;");
            etiqueta.setPrefWidth(Constantes.TAMAÑO_CASILLA);
            etiqueta.setPrefHeight(24);
            etiqueta.setLayoutX(margenCoordenadas + j * Constantes.TAMAÑO_CASILLA + 12);
            etiqueta.setLayoutY(8);
            etiqueta.setAlignment(Pos.CENTER);
            tableroPane.getChildren().add(etiqueta);
        }

        for (int i = 0; i < Constantes.TABLERO_TAMAÑO; i++) {
            for (int j = 0; j < Constantes.TABLERO_TAMAÑO; j++) {
                Rectangle casilla = new Rectangle(Constantes.TAMAÑO_CASILLA - 2, Constantes.TAMAÑO_CASILLA - 2);
                casilla.setFill(Color.web("#B8E0F0"));
                casilla.setStroke(Color.web("#666666"));
                casilla.setStrokeWidth(1);

                double x = margenCoordenadas + j * Constantes.TAMAÑO_CASILLA;
                double y = margenCoordenadas + i * Constantes.TAMAÑO_CASILLA;
                casilla.setLayoutX(x);
                casilla.setLayoutY(y);

                casilla.setOnMouseEntered(e -> casilla.setFill(Color.web("#90D4F0")));
                casilla.setOnMouseExited(e -> casilla.setFill(Color.web("#B8E0F0")));

                tableroPane.getChildren().add(casilla);
            }
        }
    }

    /**
     * Configura la visualización de la flota disponible.
     */
    private void configurarFlotaDisponible() {
        flotaDisponibleVBox.setSpacing(12);
        flotaDisponibleVBox.setStyle("-fx-padding: 10 8 8 8;");

        Label titulo = new Label("FLOTA DISPONIBLE");
        titulo.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #325D78;");
        flotaDisponibleVBox.getChildren().add(titulo);

        for (Barco barco : flota.obtenerTodos()) {
            VBox contenedorBarco = crearContenedorBarco(barco);
            flotaDisponibleVBox.getChildren().add(contenedorBarco);
            nodosBarco.put(barco.getId(), contenedorBarco);
        }
    }

    /**
     * Crea el contenedor visual de un barco con nombre.
     */
    private VBox crearContenedorBarco(Barco barco) {
        Group visualBarco = DibujadorBarcos.crearVisualizacionBarco(barco);
        configurarEventosBarco(visualBarco, barco);
        visualesBarcos.put(barco.getId(), visualBarco);

        Label nombre = new Label(obtenerNombreBarco(barco));
        nombre.setStyle("-fx-font-size: 11; -fx-text-fill: #335566; -fx-font-weight: bold;");

        VBox contenedor = new VBox(6);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setStyle("-fx-padding: 6 0;");
        contenedor.getChildren().addAll(visualBarco, nombre);
        return contenedor;
    }

    /**
     * Obtiene el nombre visible para cada barco.
     */
    private String obtenerNombreBarco(Barco barco) {
        return switch (barco.getTipo()) {
            case PORTAAVIONES -> "Portaaviones";
            case SUBMARINO -> "Submarino " + barco.getId().substring(barco.getId().lastIndexOf('-') + 1);
            case DESTRUCTOR -> "Destructor " + barco.getId().substring(barco.getId().lastIndexOf('-') + 1);
            case FRAGATA -> "Fragata " + barco.getId().substring(barco.getId().lastIndexOf('-') + 1);
        };
    }

    /**
     * Configura los botones de control.
     */
    private void configurarBotones() {
        botonRotar.setOnAction(e -> rotarBarcoSeleccionado());
        botonReiniciar.setOnAction(e -> reiniciarFlota());
        botonIniciarPartida.setOnAction(e -> iniciarPartida());
        botonMostrarTablero.setOnAction(e -> mostrarTableroOponente());
    }

    /**
     * Configura los eventos de un barco visual.
     */
    private void configurarEventosBarco(Group visual, Barco barco) {
        visual.setCursor(Cursor.HAND);
        visual.setOnMouseClicked(e -> seleccionarBarco(barco));
        visual.setOnMousePressed(e -> {
            seleccionarBarco(barco);
            visual.requestFocus();
        });
        visual.setOnDragDetected(e -> iniciarArrastre(e, barco, visual));
        visual.setOnKeyPressed(e -> manejarTecla(e, barco));
    }

    /**
     * Selecciona un barco.
     */
    private void seleccionarBarco(Barco barco) {
        barcoSeleccionado = barco;
        actualizarVisualesBarcos();
        actualizarEstado();
    }

    /**
     * Inicia el arrastre de un barco.
     */
    private void iniciarArrastre(MouseEvent evento, Barco barco, Group visualBarco) {
        Dragboard dragboard = visualBarco.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent contenido = new ClipboardContent();
        contenido.putString(barco.getId());
        dragboard.setContent(contenido);
        seleccionarBarco(barco);
        barcoEnArrastre = barco;
        visualBarco.setCursor(Cursor.MOVE);
        evento.consume();
    }

    /**
     * Maneja las teclas presionadas sobre un barco.
     */
    private void manejarTecla(KeyEvent evento, Barco barco) {
        if (evento.getCode() == KeyCode.R) {
            barcoSeleccionado = barco;
            rotarBarcoSeleccionado();
        }
    }

    /**
     * Configura los eventos de drag-drop del tablero.
     */
    private void configurarDragDropTablero() {
        tableroPane.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
                actualizarEstiloTablero(e.getDragboard().getString(), e.getX(), e.getY());
            }
            e.consume();
        });

        tableroPane.setOnDragExited(e -> {
            tableroPane.getStyleClass().remove("board-invalid");
            e.consume();
        });

        tableroPane.setOnDragDropped(e -> {
            Dragboard dragboard = e.getDragboard();
            boolean exito = false;
            tableroPane.getStyleClass().remove("board-invalid");

            if (dragboard.hasString()) {
                String idBarco = dragboard.getString();
                Barco barco = flota.obtenerPorId(idBarco);
                Group visualBarco = visualesBarcos.get(idBarco);

                if (barco != null && visualBarco != null) {
                    double xInicial = visualBarco.getLayoutX();
                    double yInicial = visualBarco.getLayoutY();
                    double x = e.getX() - margenCoordenadas;
                    double y = e.getY() - margenCoordenadas;

                    int columna = (int) Math.round(x / Constantes.TAMAÑO_CASILLA);
                    int fila = (int) Math.round(y / Constantes.TAMAÑO_CASILLA);

                    Posicion nuevaPosicion = new Posicion(fila, columna);

                    if (esPosicionValidaParaArrastre(barco, fila, columna, x, y)) {
                        Node nodoActual = nodosBarco.get(idBarco);
                        if (nodoActual != null && nodoActual.getParent() != null) {
                            ((Pane) nodoActual.getParent()).getChildren().remove(nodoActual);
                        }

                        try {
                            intentarColocarBarco(barco, nuevaPosicion);
                            double nuevoX = margenCoordenadas + columna * Constantes.TAMAÑO_CASILLA;
                            double nuevoY = margenCoordenadas + fila * Constantes.TAMAÑO_CASILLA;
                            visualBarco.setLayoutX(nuevoX);
                            visualBarco.setLayoutY(nuevoY);
                            tableroPane.getChildren().add(visualBarco);
                            nodosBarco.put(idBarco, visualBarco);

                            seleccionarBarco(barco);
                            exito = true;
                        } catch (InvalidShipPlacementException ex) {
                            visualBarco.setLayoutX(xInicial);
                            visualBarco.setLayoutY(yInicial);
                        }
                    } else {
                        visualBarco.setLayoutX(xInicial);
                        visualBarco.setLayoutY(yInicial);
                    }
                }
            }

            e.setDropCompleted(exito);
            e.consume();
        });
    }

    /**
     * Rota el barco seleccionado.
     */
    private void rotarBarcoSeleccionado() {
        if (barcoSeleccionado == null) return;

        Barco barco = barcoSeleccionado;
        if (barco.estaColocado()) {
            Posicion posicionActual = barco.getPosicion();
            barco.rotar();
            if (!tablero.puedeColocar(barco, posicionActual)) {
                barco.rotar();
                return;
            }
            tablero.removerBarco(barco.getId());
            tablero.colocarBarco(barco, posicionActual);

            Group visualBarco = visualesBarcos.get(barco.getId());
            Group nuevoVisual = DibujadorBarcos.crearVisualizacionBarco(barco);
            visualesBarcos.put(barco.getId(), nuevoVisual);

            Node nodoActual = nodosBarco.get(barco.getId());
            if (nodoActual != null && nodoActual.getParent() != null) {
                ((Pane) nodoActual.getParent()).getChildren().remove(nodoActual);
            }

            nuevoVisual.setLayoutX(visualBarco.getLayoutX());
            nuevoVisual.setLayoutY(visualBarco.getLayoutY());
            tableroPane.getChildren().add(nuevoVisual);
            nodosBarco.put(barco.getId(), nuevoVisual);
            configurarEventosBarco(nuevoVisual, barco);
            actualizarVisualesBarcos();
        } else {
            barco.rotar();
        }

        actualizarEstado();
    }

    /**
     * Reinicia la flota al estado inicial.
     */
    private void reiniciarFlota() {
        flota.reiniciar();
        tablero.limpiar();

        List<Node> aRemover = new ArrayList<>();
        for (Node nodo : tableroPane.getChildren()) {
            if (nodo instanceof Group && visualesBarcos.containsValue((Group) nodo)) {
                aRemover.add(nodo);
            }
        }
        tableroPane.getChildren().removeAll(aRemover);

        flotaDisponibleVBox.getChildren().clear();
        visualesBarcos.clear();
        nodosBarco.clear();
        configurarFlotaDisponible();
        actualizarEstado();
        actualizarVisualesBarcos();
    }

    /**
     * Inicia la partida.
     */
    private void iniciarPartida() {
        String nombre = obtenerNombre();
        if (nombre.isEmpty()) {
            mostrarAlerta("Falta el nombre", "Ingresa tu nombre para poder iniciar la partida.");
            etiquetaEstado.setText("Debes ingresar un nombre para iniciar la partida.");
            nombreField.requestFocus();
            return;
        }

        GameState partidaGuardada = persistence.cargar(nombre);
        boolean hayPartidaNoTerminada = partidaGuardada != null && !partidaGuardada.estaTerminada();

        if (hayPartidaNoTerminada) {
            ButtonType continuar = new ButtonType("Cargar partida anterior", ButtonBar.ButtonData.YES);
            ButtonType nueva = new ButtonType("Iniciar nueva partida", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert dialogo = new Alert(Alert.AlertType.NONE);
            dialogo.setTitle("Partida guardada");
            dialogo.setHeaderText("Ya tienes una partida sin terminar");
            dialogo.setContentText("¿Quieres cargar la partida anterior o iniciar una nueva partida?");
            dialogo.getButtonTypes().setAll(continuar, nueva, cancelar);

            Node botonCancelar = dialogo.getDialogPane().lookupButton(cancelar);
            if (botonCancelar != null) {
                botonCancelar.setVisible(false);
                botonCancelar.setManaged(false);
            }

            Optional<ButtonType> opcion = dialogo.showAndWait();
            if (opcion.isEmpty() || opcion.get() == cancelar) {
                return;
            }
            if (opcion.get() == continuar) {
                cargarPartidaGuardada(partidaGuardada);
                return;
            }
        }

        if (!flota.estáCompletaColocada()) {
            mostrarAlerta("Falta la flota", "Coloca la flota completa antes de iniciar una partida nueva.");
            etiquetaEstado.setText("Debes colocar la flota completa antes de iniciar la partida.");
            return;
        }

        iniciarNuevaPartida(nombre);
    }

    private void iniciarNuevaPartida(String nombre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/batallanaval/batallanaval/game-view.fxml"));
            Scene escena = new Scene(loader.load());
            GameController controlador = loader.getController();
            controlador.setPlayerShips(crearShipsDesdeFlota());
            controlador.setEnemyBoard(tableroEnemigo);
            controlador.setPlayerNickname(nombre);
            controlador.guardarEstado();

            Stage escenario = (Stage) botonIniciarPartida.getScene().getWindow();
            escenario.setTitle("Batalla Naval - Partida");
            escenario.setScene(escena);
            escenario.setMinWidth(1100);
            escenario.setMinHeight(840);
            escenario.show();
        } catch (IOException e) {
            e.printStackTrace();
            etiquetaEstado.setText("No se pudo iniciar la partida.");
        }
    }

    private void cargarPartidaGuardada(GameState estado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/batallanaval/batallanaval/game-view.fxml"));
            Scene escena = new Scene(loader.load());
            GameController controlador = loader.getController();
            controlador.cargarEstado(estado);
            controlador.setPlayerNickname(estado.getJugadorNickname());

            Stage escenario = (Stage) botonIniciarPartida.getScene().getWindow();
            escenario.setTitle("Batalla Naval - Partida continuada");
            escenario.setScene(escena);
            escenario.setMinWidth(1100);
            escenario.setMinHeight(840);
            escenario.show();
        } catch (IOException e) {
            e.printStackTrace();
            etiquetaEstado.setText("No se pudo cargar la partida guardada.");
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        if (nombreField != null) {
            nombreField.setText(nickname);
        }
        actualizarEstado();
    }

    private String obtenerNombre() {
        return nombreField.getText().trim();
    }

    private void mostrarTableroOponente() {
        Stage escenario = new Stage();
        escenario.setTitle("Tablero oponente");
        Pane pane = new Pane();

        double offset = 35;
        double boardSize = Constantes.TABLERO_TAMAÑO * Constantes.TAMAÑO_CASILLA;
        double totalSize = offset + boardSize + 12;

        pane.setPrefSize(totalSize, totalSize);
        pane.setStyle("-fx-background-color: #EAF7FC; -fx-border-color: #2F5066; -fx-border-width: 2;");
        for (int fila = 0; fila < Constantes.TABLERO_TAMAÑO; fila++) {
            Label etiqueta = new Label(String.valueOf(fila + 1));
            etiqueta.setLayoutX(8);
            etiqueta.setLayoutY(offset + fila * Constantes.TAMAÑO_CASILLA + 10);
            pane.getChildren().add(etiqueta);
        }
        for (int columna = 0; columna < Constantes.TABLERO_TAMAÑO; columna++) {
            Label etiqueta = new Label(String.valueOf((char) ('A' + columna)));
            etiqueta.setLayoutX(offset + columna * Constantes.TAMAÑO_CASILLA + 12);
            etiqueta.setLayoutY(8);
            pane.getChildren().add(etiqueta);
        }
        for (int fila = 0; fila < Constantes.TABLERO_TAMAÑO; fila++) {
            for (int columna = 0; columna < Constantes.TABLERO_TAMAÑO; columna++) {
                Rectangle celda = new Rectangle(Constantes.TAMAÑO_CASILLA - 2, Constantes.TAMAÑO_CASILLA - 2);
                celda.setFill(tableroEnemigo.getCell(fila, columna).tieneBarco() ? Color.web("#7F8790") : Color.web("#B8E0F0"));
                celda.setStroke(Color.web("#666666"));
                celda.setLayoutX(offset + columna * Constantes.TAMAÑO_CASILLA);
                celda.setLayoutY(offset + fila * Constantes.TAMAÑO_CASILLA);
                pane.getChildren().add(celda);
            }
        }
        escenario.setScene(new Scene(pane, totalSize, totalSize));
        escenario.show();
    }

    private java.util.List<Ship> crearShipsDesdeFlota() {
        return flota.obtenerColocados().stream()
            .map(this::crearShipDesdeBarco)
            .toList();
    }

    private Ship crearShipDesdeBarco(Barco barco) {
        return new Ship(
            barco.getId(),
            barco.getTipo().getNombre(),
            barco.getTipo().getTamaño(),
            barco.getOrientacion(),
            barco.getPosicion()
        );
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    /**
     * Actualiza la etiqueta de estado.
     */
    private void actualizarEstado() {
        int colocados = flota.obtenerColocados().size();
        int total = flota.getCantidadTotal();

        etiquetaEstado.setText(String.format("Barcos colocados: %d/%d", colocados, total));

        if (barcoSeleccionado != null) {
            String estado = barcoSeleccionado.estaColocado() ? "colocado" : "no colocado";
            etiquetaEstado.setText(etiquetaEstado.getText() + " | Seleccionado: " +
                barcoSeleccionado.getTipo().getNombre() + " (" + estado + ")");
        }
    }

    /**
     * Actualiza el estado visual de los barcos.
     */
    private void actualizarVisualesBarcos() {
        for (Map.Entry<String, Group> entrada : visualesBarcos.entrySet()) {
            Group visual = entrada.getValue();
            boolean seleccionado = barcoSeleccionado != null && entrada.getKey().equals(barcoSeleccionado.getId());
            aplicarEstadoSeleccion(visual, seleccionado);
        }
    }

    /**
     * Aplica el estado visual de selección a un barco.
     */
    private void aplicarEstadoSeleccion(Group visual, boolean seleccionado) {
        visual.setScaleX(seleccionado ? 1.04 : 1.0);
        visual.setScaleY(seleccionado ? 1.04 : 1.0);
        visual.setEffect(seleccionado ? new DropShadow(8, Color.web("#F2C94C")) : null);
    }

    /**
     * Verifica si la posición bajo arrastre es válida para el barco.
     */
    private boolean esPosicionValidaParaArrastre(Barco barco, int fila, int columna, double x, double y) {
        if (fila < 0 || columna < 0 || fila >= Constantes.TABLERO_TAMAÑO || columna >= Constantes.TABLERO_TAMAÑO) {
            return false;
        }
        Posicion nuevaPosicion = new Posicion(fila, columna);
        return tablero.puedeColocar(barco, nuevaPosicion);
    }

    private void intentarColocarBarco(Barco barco, Posicion posicion) throws InvalidShipPlacementException {
        try {
            tablero.colocarBarco(barco, posicion);
        } catch (IllegalArgumentException e) {
            throw new InvalidShipPlacementException("No se puede colocar el barco en esa posición");
        }
    }

    /**
     * Actualiza el estilo del tablero mientras se arrastra un barco.
     */
    private void actualizarEstiloTablero(String idBarco, double x, double y) {
        Barco barco = flota.obtenerPorId(idBarco);
        if (barco == null) {
            tableroPane.getStyleClass().remove("board-invalid");
            return;
        }

        int columna = (int) Math.round((x - margenCoordenadas) / Constantes.TAMAÑO_CASILLA);
        int fila = (int) Math.round((y - margenCoordenadas) / Constantes.TAMAÑO_CASILLA);
        boolean valido = esPosicionValidaParaArrastre(barco, fila, columna, x, y);

        if (!valido) {
            if (!tableroPane.getStyleClass().contains("board-invalid")) {
                tableroPane.getStyleClass().add("board-invalid");
            }
        } else {
            tableroPane.getStyleClass().remove("board-invalid");
        }
    }
}
