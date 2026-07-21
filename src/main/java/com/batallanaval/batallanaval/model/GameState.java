package com.batallanaval.batallanaval.model;

import java.io.Serializable;

/**
 * Representa el estado completo de una partida para guardar y cargar.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Board playerBoard;
    private final Board enemyBoard;
    private final boolean jugadorTurno;
    private final String jugadorNickname;

    public GameState(Board playerBoard, Board enemyBoard, boolean jugadorTurno, String jugadorNickname) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
        this.jugadorTurno = jugadorTurno;
        this.jugadorNickname = jugadorNickname != null && !jugadorNickname.isBlank() ? jugadorNickname : "Jugador";
    }

    public Board getPlayerBoard() {
        return playerBoard;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    public boolean isJugadorTurno() {
        return jugadorTurno;
    }

    public String getJugadorNickname() {
        return jugadorNickname;
    }

    public int contarBarcosHundidosJugador() {
        return enemyBoard.countSunkShips();
    }

    public int contarBarcosHundidosMaquina() {
        return playerBoard.countSunkShips();
    }

    public boolean estaTerminada() {
        return playerBoard.allShipsSunk() || enemyBoard.allShipsSunk();
    }
}
