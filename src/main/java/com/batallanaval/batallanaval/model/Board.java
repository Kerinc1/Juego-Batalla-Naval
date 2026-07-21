package com.batallanaval.batallanaval.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un tablero completo que conoce todas sus casillas y barcos.
 */
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int TAMAÑO_TABLERO = 10;
    private final Cell[][] celdas;
    private final List<Ship> barcos;

    /**
     * Inicializa un tablero vacío de 10x10.
     */
    public Board() {
        this.celdas = new Cell[TAMAÑO_TABLERO][TAMAÑO_TABLERO];
        this.barcos = new ArrayList<>();
        for (int fila = 0; fila < TAMAÑO_TABLERO; fila++) {
            for (int columna = 0; columna < TAMAÑO_TABLERO; columna++) {
                celdas[fila][columna] = new Cell(fila, columna);
            }
        }
    }

    public int getTamaño() {
        return TAMAÑO_TABLERO;
    }

    public Cell getCell(int fila, int columna) {
        if (!posicionValida(fila, columna)) {
            throw new IllegalArgumentException("Posición fuera del tablero");
        }
        return celdas[fila][columna];
    }

    public List<Ship> getShips() {
        return new ArrayList<>(barcos);
    }

    /**
     * Agrega un barco al tablero con validación de posiciones.
     */
    public void addShip(Ship barco) {
        for (Posicion casilla : barco.getCasillasOcupadas()) {
            if (!posicionValida(casilla.getFila(), casilla.getColumna())) {
                throw new IllegalArgumentException("Posición inválida para colocar el barco");
            }
            Cell celda = getCell(casilla.getFila(), casilla.getColumna());
            if (celda.tieneBarco()) {
                throw new IllegalArgumentException("Ya existe un barco en la posición " + casilla);
            }
        }
        barcos.add(barco);
        for (Posicion casilla : barco.getCasillasOcupadas()) {
            Cell celda = getCell(casilla.getFila(), casilla.getColumna());
            celda.asignarBarco(barco);
        }
    }

    /**
     * Procesa un disparo sobre una posición del tablero.
     *
     * @return el resultado del disparo.
     */
    public ShotResult shoot(int fila, int columna) {
        if (!posicionValida(fila, columna)) {
            throw new IllegalArgumentException("Posición fuera del tablero");
        }
        Cell celda = getCell(fila, columna);
        if (celda.fueDisparada()) {
            throw new IllegalArgumentException("La casilla ya fue utilizada");
        }
        celda.marcarDisparo();
        if (celda.tieneBarco()) {
            Ship barco = celda.getShip();
            barco.registrarImpacto(celda.getPosicion());
            return barco.estaHundido() ? ShotResult.SUNK : ShotResult.HIT;
        }
        return ShotResult.WATER;
    }

    public boolean allShipsSunk() {
        return barcos.stream().allMatch(Ship::estaHundido);
    }

    public int countSunkShips() {
        return (int) barcos.stream().filter(Ship::estaHundido).count();
    }

    public boolean isCellDisparada(int fila, int columna) {
        return getCell(fila, columna).fueDisparada();
    }

    private boolean posicionValida(int fila, int columna) {
        return fila >= 0 && fila < TAMAÑO_TABLERO && columna >= 0 && columna < TAMAÑO_TABLERO;
    }
}
