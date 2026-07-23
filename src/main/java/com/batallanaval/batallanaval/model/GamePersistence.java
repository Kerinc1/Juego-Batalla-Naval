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
    private static final Path SAVE_STATE_FILE = SAVE_FOLDER.resolve("game-state.dat");
    private static final Path SAVE_INFO_FILE = SAVE_FOLDER.resolve("player-info.txt");

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
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(SAVE_STATE_FILE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            oos.writeObject(estado);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el estado del juego", e);
        }

        try {
            String info = String.format("nombre=%s%nbarcosHundidosJugador=%d%nbarcosHundidosMaquina=%d%n",
                    estado.getJugadorNickname(), estado.contarBarcosHundidosJugador(), estado.contarBarcosHundidosMaquina());
            Files.writeString(SAVE_INFO_FILE, info, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la información del jugador", e);
        }
    }

    public GameState cargar() {
        if (!Files.exists(SAVE_STATE_FILE)) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(SAVE_STATE_FILE, StandardOpenOption.READ))) {
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
        return Files.exists(SAVE_STATE_FILE);
    }
}
