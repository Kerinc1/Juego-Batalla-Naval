package com.batallanaval.batallanaval.model;

/**
 * Excepción marcada para indicar que un barco no puede colocarse en una posición válida.
 */
public class InvalidShipPlacementException extends Exception {
    public InvalidShipPlacementException(String message) {
        super(message);
    }
}
