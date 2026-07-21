package com.batallanaval.batallanaval.model;

import java.util.*;

/**
 * Gestiona la flota completa del jugador.
 */
public class Flota {
    private final List<Barco> barcos;
    private final Map<String, Barco> barcosMap;

    /**
     * Inicializa la flota con los barcos especificados en HU1.
     */
    public Flota() {
        this.barcos = new ArrayList<>();
        this.barcosMap = new HashMap<>();
        inicializarFlota();
    }

    /**
     * Crea la composición de barcos para la flota.
     */
    private void inicializarFlota() {
        // 1 Portaaviones (tamaño 4)
        agregarBarco("PA-1", TipoBarco.PORTAAVIONES);

        // 2 Submarinos (tamaño 3)
        agregarBarco("SUB-1", TipoBarco.SUBMARINO);
        agregarBarco("SUB-2", TipoBarco.SUBMARINO);

        // 3 Destructores (tamaño 2)
        agregarBarco("DES-1", TipoBarco.DESTRUCTOR);
        agregarBarco("DES-2", TipoBarco.DESTRUCTOR);
        agregarBarco("DES-3", TipoBarco.DESTRUCTOR);

        // 4 Fragatas (tamaño 1)
        agregarBarco("FRA-1", TipoBarco.FRAGATA);
        agregarBarco("FRA-2", TipoBarco.FRAGATA);
        agregarBarco("FRA-3", TipoBarco.FRAGATA);
        agregarBarco("FRA-4", TipoBarco.FRAGATA);
    }

    /**
     * Agrega un nuevo barco a la flota.
     */
    private void agregarBarco(String id, TipoBarco tipo) {
        Barco barco = new Barco(id, tipo);
        barcos.add(barco);
        barcosMap.put(id, barco);
    }

    /**
     * Obtiene todos los barcos de la flota.
     */
    public List<Barco> obtenerTodos() {
        return new ArrayList<>(barcos);
    }

    /**
     * Obtiene un barco por su identificador.
     */
    public Barco obtenerPorId(String id) {
        return barcosMap.get(id);
    }

    /**
     * Obtiene la cantidad total de barcos.
     */
    public int getCantidadTotal() {
        return barcos.size();
    }

    /**
     * Obtiene los barcos no colocados.
     */
    public List<Barco> obtenerNoColocados() {
        return barcos.stream()
            .filter(b -> !b.estaColocado())
            .toList();
    }

    /**
     * Obtiene los barcos colocados.
     */
    public List<Barco> obtenerColocados() {
        return barcos.stream()
            .filter(Barco::estaColocado)
            .toList();
    }

    /**
     * Verifica si toda la flota está colocada.
     */
    public boolean estáCompletaColocada() {
        return barcos.stream().allMatch(Barco::estaColocado);
    }

    /**
     * Reinicia todos los barcos a su estado inicial.
     */
    public void reiniciar() {
        barcos.forEach(Barco::reiniciar);
    }
}
