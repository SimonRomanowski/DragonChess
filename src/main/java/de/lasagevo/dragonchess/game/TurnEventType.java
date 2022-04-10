package de.lasagevo.dragonchess.game;

public enum TurnEventType {
    PIECE_MOVED,
    PIECE_CAPTURED,
    PIECE_CAPTURED_AFAR,
    PIECE_PROMOTED,
    INVALID_LOCATION,
    INVALID_PLAYER,
    WIN_GOLD,
    WIN_SCARLET,
    TIE;

    public boolean isErrorEvent() {
        return (this == INVALID_LOCATION)
                || (this == INVALID_PLAYER);
    }

    public boolean isEndGameEvent() {
        return (this == WIN_GOLD)
                || (this == WIN_SCARLET)
                || (this == TIE);
    }
}
