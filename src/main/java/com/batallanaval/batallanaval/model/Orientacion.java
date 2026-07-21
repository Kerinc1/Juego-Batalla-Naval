package com.batallanaval.batallanaval.model;

/**
 * Orientaciones posibles de un barco en el tablero.
 */
public enum Orientacion {
    HORIZONTAL,
    VERTICAL;

    /**
     * Alterna entre horizontal y vertical.
     */
    public Orientacion rotar() {
        return this == HORIZONTAL ? VERTICAL : HORIZONTAL;
    }
}
