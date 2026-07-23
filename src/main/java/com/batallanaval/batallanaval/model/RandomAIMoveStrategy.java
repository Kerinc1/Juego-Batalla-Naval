package com.batallanaval.batallanaval.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Estrategia de IA que elige aleatoriamente una casilla no utilizada.
 */
public class RandomAIMoveStrategy implements IAttackStrategy {
    private final Random random;
    private final List<Posicion> posicionesDisponibles;

    public RandomAIMoveStrategy() {
        this.random = new Random();
        this.posicionesDisponibles = new ArrayList<>();
    }

    @Override
    public Posicion seleccionarSiguienteDisparo(Board tablero) {
        actualizarPosicionesDisponibles(tablero);
        if (posicionesDisponibles.isEmpty()) {
            throw new IllegalStateException("No hay posiciones disponibles para disparar");
        }

        List<Posicion> objetivos = obtenerVecinosDeImpactos(tablero);
        if (!objetivos.isEmpty()) {
            Posicion objetivo = objetivos.get(random.nextInt(objetivos.size()));
            posicionesDisponibles.remove(objetivo);
            return objetivo;
        }

        return posicionesDisponibles.remove(random.nextInt(posicionesDisponibles.size()));
    }

    private List<Posicion> obtenerVecinosDeImpactos(Board tablero) {
        List<Posicion> objetivos = new ArrayList<>();
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int columna = 0; columna < tablero.getTamaño(); columna++) {
                Cell celda = tablero.getCell(fila, columna);
                if (!celda.fueDisparada() || !celda.tieneBarco() || celda.getShip().estaHundido()) {
                    continue;
                }
                agregarSiDisponible(tablero, objetivos, fila - 1, columna);
                agregarSiDisponible(tablero, objetivos, fila + 1, columna);
                agregarSiDisponible(tablero, objetivos, fila, columna - 1);
                agregarSiDisponible(tablero, objetivos, fila, columna + 1);
            }
        }
        return objetivos;
    }

    private void agregarSiDisponible(Board tablero, List<Posicion> objetivos, int fila, int columna) {
        if (fila >= 0 && fila < tablero.getTamaño() && columna >= 0 && columna < tablero.getTamaño()) {
            Posicion posicion = new Posicion(fila, columna);
            if (!tablero.getCell(fila, columna).fueDisparada() && !objetivos.contains(posicion)) {
                objetivos.add(posicion);
            }
        }
    }

    private void actualizarPosicionesDisponibles(Board tablero) {
        posicionesDisponibles.clear();
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int columna = 0; columna < tablero.getTamaño(); columna++) {
                Cell celda = tablero.getCell(fila, columna);
                if (!celda.fueDisparada()) {
                    posicionesDisponibles.add(new Posicion(fila, columna));
                }
            }
        }
    }

    @Override
    public void reiniciar(Board tablero) {
        actualizarPosicionesDisponibles(tablero);
    }
}
