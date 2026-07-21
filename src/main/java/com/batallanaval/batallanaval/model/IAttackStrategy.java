package com.batallanaval.batallanaval.model;

/**
 * Estrategia para seleccionar la siguiente posición de disparo.
 */
public interface IAttackStrategy {
    Posicion seleccionarSiguienteDisparo(Board tablero);
    void reiniciar(Board tablero);
}
