package com.batallanaval.batallanaval.model;

import java.util.*;

/**
 * Servicio para colocación aleatoria de barcos.
 */
public class ServicioColocacionAleatoria {
    private final Random random;

    /**
     * Inicializa el servicio con un generador de números aleatorios.
     */
    public ServicioColocacionAleatoria() {
        this.random = new Random();
    }

    /**
     * Coloca todos los barcos de la flota aleatoriamente en el tablero.
     */
    public void colocarFlotaAleatoria(Flota flota, Tablero tablero) {
        flota.reiniciar();
        tablero.limpiar();

        for (Barco barco : flota.obtenerTodos()) {
            boolean colocado = false;
            int intentos = 0;
            int maxIntentos = 100;

            while (!colocado && intentos < maxIntentos) {
                int fila = random.nextInt(Tablero.getTamaño());
                int columna = random.nextInt(Tablero.getTamaño());
                Orientacion orientacion = random.nextBoolean() ? Orientacion.HORIZONTAL : Orientacion.VERTICAL;

                Posicion posicion = new Posicion(fila, columna);
                barco.setOrientacion(orientacion);

                if (tablero.puedeColocar(barco, posicion)) {
                    tablero.colocarBarco(barco, posicion);
                    colocado = true;
                }

                intentos++;
            }

            if (!colocado) {
                throw new RuntimeException("No se pudo colocar el barco: " + barco.getId());
            }
        }
    }
}
