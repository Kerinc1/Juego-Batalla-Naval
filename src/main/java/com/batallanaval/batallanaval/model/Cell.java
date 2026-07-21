package com.batallanaval.batallanaval.model;

/**
 * Representa una casilla del tablero con su estado mínimo.
 */
public class Cell {
    private final int fila;
    private final int columna;
    private Ship ship;
    private boolean disparada;

    /**
     * Inicializa la casilla con su fila y columna.
     */
    public Cell(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.ship = null;
        this.disparada = false;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public boolean tieneBarco() {
        return ship != null;
    }

    public boolean fueDisparada() {
        return disparada;
    }

    public Ship getShip() {
        return ship;
    }

    public void asignarBarco(Ship ship) {
        this.ship = ship;
    }

    public void marcarDisparo() {
        this.disparada = true;
    }

    public Posicion getPosicion() {
        return new Posicion(fila, columna);
    }
}
