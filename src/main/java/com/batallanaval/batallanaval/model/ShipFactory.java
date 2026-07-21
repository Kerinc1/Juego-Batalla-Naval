package com.batallanaval.batallanaval.model;

/**
 * Crea barcos y flotas para el juego.
 */
public class ShipFactory {
    public static Ship crearShip(String id, TipoBarco tipo, Orientacion orientacion, Posicion posicion) {
        return new Ship(id, tipo.getNombre(), tipo.getTamaño(), orientacion, posicion);
    }
}
