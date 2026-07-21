package com.batallanaval.batallanaval.model;

import java.util.*;

/**
 * Gestiona el tablero 10x10 del juego.
 */
public class Tablero {
    private static final int TAMAÑO = 10;
    private final Map<Posicion, Barco> ocupacion;

    /**
     * Inicializa un tablero vacío de 10x10.
     */
    public Tablero() {
        this.ocupacion = new HashMap<>();
    }

    public static int getTamaño() {
        return TAMAÑO;
    }

    /**
     * Verifica si una posición está dentro del tablero.
     */
    public boolean posicionValida(Posicion pos) {
        return pos.getFila() >= 0 && pos.getFila() < TAMAÑO &&
               pos.getColumna() >= 0 && pos.getColumna() < TAMAÑO;
    }

    /**
     * Verifica si una posición está ocupada.
     */
    public boolean estáOcupada(Posicion pos) {
        return ocupacion.containsKey(pos);
    }

    /**
     * Obtiene el barco en una posición, o null si está vacía.
     */
    public Barco obtenerBarco(Posicion pos) {
        return ocupacion.get(pos);
    }

    /**
     * Valida si un barco puede colocarse en una posición.
     */
    public boolean puedeColocar(Barco barco, Posicion posicion) {
        // Verificar que todas las casillas estén dentro del tablero
        for (Posicion casillaTemp : generarCasillas(posicion, barco.getOrientacion(), barco.getTipo().getTamaño())) {
            if (!posicionValida(casillaTemp)) {
                return false;
            }
            // Verificar que no haya otro barco
            if (estáOcupada(casillaTemp) && !ocupacion.get(casillaTemp).getId().equals(barco.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Genera el conjunto de casillas que ocuparía un barco.
     */
    private Set<Posicion> generarCasillas(Posicion inicio, Orientacion orientacion, int tamaño) {
        Set<Posicion> casillas = new HashSet<>();
        if (orientacion == Orientacion.HORIZONTAL) {
            for (int i = 0; i < tamaño; i++) {
                casillas.add(new Posicion(inicio.getFila(), inicio.getColumna() + i));
            }
        } else {
            for (int i = 0; i < tamaño; i++) {
                casillas.add(new Posicion(inicio.getFila() + i, inicio.getColumna()));
            }
        }
        return casillas;
    }

    /**
     * Coloca un barco en el tablero.
     */
    public void colocarBarco(Barco barco, Posicion posicion) {
        if (!puedeColocar(barco, posicion)) {
            throw new IllegalArgumentException("Posición inválida para colocar el barco");
        }
        
        // Remover ocupación anterior si existía
        removerBarco(barco.getId());
        
        // Colocar nuevo
        barco.colocar(posicion);
        for (Posicion casilla : barco.getCasillasOcupadas()) {
            ocupacion.put(casilla, barco);
        }
    }

    /**
     * Remueve un barco del tablero.
     */
    public void removerBarco(String idBarco) {
        ocupacion.values().removeAll(
            ocupacion.entrySet().stream()
                .filter(e -> e.getValue().getId().equals(idBarco))
                .map(Map.Entry::getKey)
                .toList()
        );
        ocupacion.entrySet().removeIf(e -> e.getValue().getId().equals(idBarco));
    }

    /**
     * Limpia completamente el tablero.
     */
    public void limpiar() {
        ocupacion.clear();
    }

    /**
     * Devuelve el número de barcos colocados.
     */
    public int contarBarcosColocados() {
        return (int) ocupacion.values().stream()
            .map(Barco::getId)
            .distinct()
            .count();
    }

    /**
     * Verifica si el tablero está completamente lleno.
     */
    public boolean estaLleno(int totalBarcos) {
        return contarBarcosColocados() == totalBarcos;
    }
}
