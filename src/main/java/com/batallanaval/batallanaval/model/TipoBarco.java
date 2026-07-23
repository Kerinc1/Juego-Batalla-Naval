package com.batallanaval.batallanaval.model;

/**
 * Tipos de barcos disponibles en la flota.
 */
public enum TipoBarco {
    PORTAAVIONES(4, "Portaaviones"),
    SUBMARINO(3, "Submarino"),
    DESTRUCTOR(2, "Destructor"),
    FRAGATA(1, "Fragata");

    private final int tamaño;
    private final String nombre;

    TipoBarco(int tamaño, String nombre) {
        this.tamaño = tamaño;
        this.nombre = nombre;
    }

    public int getTamaño() {
        return tamaño;
    }

    public String getNombre() {
        return nombre;
    }
}
