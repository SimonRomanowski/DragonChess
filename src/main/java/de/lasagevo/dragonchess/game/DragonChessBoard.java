package de.lasagevo.dragonchess.game;

import java.util.List;
import java.util.UUID;

public class DragonChessBoard {
    public static DragonChessBoard create() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public DragonChessBoardState getState() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public Player getOwner(final UUID pieceId) throws IllegalIdException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public List<UUID> getPieces(Player player) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void movePiece(final UUID pieceId,
                          final BoardPosition position) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public UUID getKing(final Player gold) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void removePiece(final UUID pieceId) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void promotePiece(final UUID pieceId) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public UUID getPiece(final BoardPosition position) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
