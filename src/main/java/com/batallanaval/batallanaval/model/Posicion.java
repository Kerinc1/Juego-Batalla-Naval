package com.batallanaval.batallanaval.model;

import java.io.Serializable;

/**
 * Representa una posición en el tablero.
 */
public class Posicion implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int fila;
    private final int columna;

    /**
     * Inicializa una posición con fila y columna.
     */
    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Posicion)) return false;
        Posicion otra = (Posicion) obj;
        return this.fila == otra.fila && this.columna == otra.columna;
    }

    @Override
    public int hashCode() {
        return 31 * fila + columna;
    }

    @Override
    public String toString() {
        return "(" + fila + ", " + columna + ")";
    }
}
