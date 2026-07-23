package com.batallanaval.batallanaval.model;

/**
 * Adaptador para convertir Barco en Ship.
 * Este patrón estructural permite reutilizar la lógica de colocación existente en PlacementController.
 */
public class BarcoToShipAdapter {
    public Ship adaptar(Barco barco) {
        return new Ship(barco.getId(), barco.getTipo().getNombre(), barco.getTipo().getTamaño(), barco.getOrientacion(), barco.getPosicion());
    }
}
