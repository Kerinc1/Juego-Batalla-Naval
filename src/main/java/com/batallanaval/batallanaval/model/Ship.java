package com.batallanaval.batallanaval.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa un barco dentro del tablero durante la partida.
 */
public class Ship {
    private final String id;
    private final String nombre;
    private final int tamaño;
    private final Orientacion orientacion;
    private final Posicion posicion;
    private final Set<Posicion> impactos;

    /**
     * Inicializa un barco con su identidad, nombre, tamaño, orientación y posición.
     */
    public Ship(String id, String nombre, int tamaño, Orientacion orientacion, Posicion posicion) {
        this.id = id;
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.orientacion = orientacion;
        this.posicion = posicion;
        this.impactos = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTamaño() {
        return tamaño;
    }

    public Orientacion getOrientacion() {
        return orientacion;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public int getCantidadImpactos() {
        return impactos.size();
    }

    public int getCantidadSegmentos() {
        return tamaño;
    }

    public boolean estaHundido() {
        return impactos.size() >= tamaño;
    }

    /**
     * Devuelve las casillas que ocupa el barco.
     */
    public Set<Posicion> getCasillasOcupadas() {
        Set<Posicion> casillas = new HashSet<>();
        for (int i = 0; i < tamaño; i++) {
            if (orientacion == Orientacion.HORIZONTAL) {
                casillas.add(new Posicion(posicion.getFila(), posicion.getColumna() + i));
            } else {
                casillas.add(new Posicion(posicion.getFila() + i, posicion.getColumna()));
            }
        }
        return casillas;
    }

    /**
     * Registra un impacto en una casilla del barco.
     */
    public void registrarImpacto(Posicion posicionImpactada) {
        if (contienePosicion(posicionImpactada)) {
            impactos.add(posicionImpactada);
        }
    }

    public boolean contienePosicion(Posicion posicion) {
        return getCasillasOcupadas().contains(posicion);
    }
}
