package com.batallanaval.batallanaval.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Permite guardar y cargar el estado del juego.
 */
public class GamePersistence {
    private static final Path SAVE_FOLDER = Path.of(System.getProperty("user.home"), "batalla-naval-save");

    public GamePersistence() {
        try {
            if (!Files.exists(SAVE_FOLDER)) {
                Files.createDirectories(SAVE_FOLDER);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de guardado", e);
        }
    }

    public void guardar(GameState estado) {
        guardar(estado, estado.getJugadorNickname());
    }

    public void guardar(GameState estado, String nombreUsuario) {
        String usuario = normalizarNombreUsuario(nombreUsuario);
        Path archivoEstado = SAVE_FOLDER.resolve("game-state-" + usuario + ".dat");
        Path archivoInfo = SAVE_FOLDER.resolve("player-info-" + usuario + ".txt");

        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(archivoEstado, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            oos.writeObject(estado);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el estado del juego", e);
        }

        try {
            String info = String.format("nombre=%s%nbarcosHundidosJugador=%d%nbarcosHundidosMaquina=%d%n",
                    estado.getJugadorNickname(), estado.contarBarcosHundidosJugador(), estado.contarBarcosHundidosMaquina());
            Files.writeString(archivoInfo, info, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la información del jugador", e);
        }
    }

    public GameState cargar() {
        return cargar(null);
    }

    public GameState cargar(String nombreUsuario) {
        Path archivoEstado = obtenerRutaEstado(nombreUsuario);
        if (!Files.exists(archivoEstado)) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(archivoEstado, StandardOpenOption.READ))) {
            Object objeto = ois.readObject();
            if (objeto instanceof GameState estado) {
                return estado;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar el estado del juego", e);
        }
        return null;
    }

    public boolean existePartidaGuardada() {
        return existePartidaGuardada(null);
    }

    public boolean existePartidaGuardada(String nombreUsuario) {
        return Files.exists(obtenerRutaEstado(nombreUsuario));
    }

    private Path obtenerRutaEstado(String nombreUsuario) {
        String usuario = normalizarNombreUsuario(nombreUsuario);
        return SAVE_FOLDER.resolve("game-state-" + usuario + ".dat");
    }

    private String normalizarNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return "jugador";
        }
        return nombreUsuario.trim().replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
