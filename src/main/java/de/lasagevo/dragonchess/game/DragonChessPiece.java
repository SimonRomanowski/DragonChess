package de.lasagevo.dragonchess.game;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.UUID;

/**
 * This class represents a single piece in a game of DragonChess.
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DragonChessPiece {

    @NonNull
    UUID id;

    @NonNull
    DragonChessPieceType type;

    @NonNull
    Player player;

    /**
     * Static factory method for a {@link DragonChessPiece} with the given type.
     * @param type   The type of the piece
     * @param player The player to whom the piece belongs
     * @return A unique chess piece with the given type
     * @throws IllegalArgumentException If any argument is null
     */
    public static DragonChessPiece create(final DragonChessPieceType type, final Player player) {
        if (type == null) {
            throw new IllegalArgumentException("Type can not be null");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player can not be null");
        }
        return new DragonChessPiece(UUID.randomUUID(), type, player);
    }

    /**
     * Creates a chess piece with the same ID as this one but a different type.
     * @param newType The type of the newly created piece
     * @return A chess piece with the same ID as this one but a different type
     * @throws IllegalArgumentException If <tt>type</tt> is null
     */
    public DragonChessPiece withType(final DragonChessPieceType newType) {
        if (newType == null) {
            throw new IllegalArgumentException("Type can not be null");
        }
        return new DragonChessPiece(id, newType, player);
    }

}
