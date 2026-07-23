package com.batallanaval.batallanaval.model;

import java.util.Random;

/**
 * Genera un tablero con la flota enemiga colocada aleatoriamente.
 */
public class RandomBoardGenerator {
    private final Random random;

    public RandomBoardGenerator() {
        this.random = new Random();
    }

    public Board generarTableroEnemigo() {
        Board tablero = new Board();
        colocarBarcos(tipoBarcoCount(TipoBarco.PORTAAVIONES, 1), tablero, TipoBarco.PORTAAVIONES);
        colocarBarcos(tipoBarcoCount(TipoBarco.SUBMARINO, 2), tablero, TipoBarco.SUBMARINO);
        colocarBarcos(tipoBarcoCount(TipoBarco.DESTRUCTOR, 3), tablero, TipoBarco.DESTRUCTOR);
        colocarBarcos(tipoBarcoCount(TipoBarco.FRAGATA, 4), tablero, TipoBarco.FRAGATA);
        return tablero;
    }

    private int tipoBarcoCount(TipoBarco tipo, int count) {
        return count;
    }

    private void colocarBarcos(int count, Board tablero, TipoBarco tipo) {
        for (int i = 1; i <= count; i++) {
            boolean colocado = false;
            int intentos = 0;
            while (!colocado && intentos < 5000) {
                Posicion posicion = new Posicion(random.nextInt(10), random.nextInt(10));
                Orientacion orientacion = random.nextBoolean() ? Orientacion.HORIZONTAL : Orientacion.VERTICAL;
                Ship ship = ShipFactory.crearShip(tipo.name() + "-" + i, tipo, orientacion, posicion);
                try {
                    tablero.addShip(ship);
                    colocado = true;
                } catch (IllegalArgumentException ignored) {
                }
                intentos++;
            }
            if (!colocado) {
                throw new RuntimeException("No se pudo colocar el barco: " + tipo.getNombre());
            }
        }
    }
}
