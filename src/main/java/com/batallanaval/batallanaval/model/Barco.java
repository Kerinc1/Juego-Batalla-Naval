package com.batallanaval.batallanaval.model;

import java.util.*;

/**
 * Representa un barco en el tablero.
 */
public class Barco {
    private final String id;
    private final TipoBarco tipo;
    private Posicion posicion;
    private Orientacion orientacion;
    private EstadoBarco estado;
    private final Set<Posicion> casillasOcupadas;

    /**
     * Inicializa un barco con identificador único y tipo.
     */
    public Barco(String id, TipoBarco tipo) {
        this.id = id;
        this.tipo = tipo;
        this.posicion = null;
        this.orientacion = Orientacion.HORIZONTAL;
        this.estado = EstadoBarco.NO_COLOCADO;
        this.casillasOcupadas = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public TipoBarco getTipo() {
        return tipo;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public Orientacion getOrientacion() {
        return orientacion;
    }

    public EstadoBarco getEstado() {
        return estado;
    }

    public Set<Posicion> getCasillasOcupadas() {
        return new HashSet<>(casillasOcupadas);
    }

    /**
     * Coloca el barco en una posición específica del tablero.
     */
    public void colocar(Posicion pos) {
        this.posicion = pos;
        actualizarCasillasOcupadas();
        this.estado = EstadoBarco.COLOCADO;
    }

    /**
     * Actualiza la orientación del barco.
     */
    public void setOrientacion(Orientacion orientacion) {
        this.orientacion = orientacion;
        if (this.posicion != null) {
            actualizarCasillasOcupadas();
        }
    }

    /**
     * Rota el barco 90 grados.
     */
    public void rotar() {
        this.orientacion = this.orientacion.rotar();
        if (this.posicion != null) {
            actualizarCasillasOcupadas();
        }
    }

    /**
     * Calcula y actualiza las casillas ocupadas por el barco.
     */
    private void actualizarCasillasOcupadas() {
        casillasOcupadas.clear();
        if (posicion == null) return;

        if (orientacion == Orientacion.HORIZONTAL) {
            for (int i = 0; i < tipo.getTamaño(); i++) {
                casillasOcupadas.add(new Posicion(posicion.getFila(), posicion.getColumna() + i));
            }
        } else {
            for (int i = 0; i < tipo.getTamaño(); i++) {
                casillasOcupadas.add(new Posicion(posicion.getFila() + i, posicion.getColumna()));
            }
        }
    }

    /**
     * Reinicia el barco a su estado inicial.
     */
    public void reiniciar() {
        this.posicion = null;
        this.orientacion = Orientacion.HORIZONTAL;
        this.estado = EstadoBarco.NO_COLOCADO;
        this.casillasOcupadas.clear();
    }

    /**
     * Verifica si el barco está colocado en el tablero.
     */
    public boolean estaColocado() {
        return estado != EstadoBarco.NO_COLOCADO;
    }

    @Override
    public String toString() {
        return tipo.getNombre() + " - " + (posicion != null ? posicion.toString() : "No colocado");
    }
}
