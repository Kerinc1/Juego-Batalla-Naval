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
        return posicionesDisponibles.remove(random.nextInt(posicionesDisponibles.size()));
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
