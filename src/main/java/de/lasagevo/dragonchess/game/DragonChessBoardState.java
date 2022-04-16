package de.lasagevo.dragonchess.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static de.lasagevo.dragonchess.game.BoardPosition.BOARD_HEIGHT;
import static de.lasagevo.dragonchess.game.BoardPosition.BOARD_WIDTH;
import static de.lasagevo.dragonchess.game.BoardPosition.LEVEL_AMOUNT;

public class DragonChessBoardState {

    private final DragonChessPiece[][][] pieces;

    DragonChessBoardState(final DragonChessPiece[][] piecesSky,
                          final DragonChessPiece[][] piecesGround,
                          final DragonChessPiece[][] piecesUnderworld) {
        pieces = new DragonChessPiece[][][] {piecesUnderworld, piecesGround, piecesSky};
        assertUniqueIds();
    }

    DragonChessBoardState(final DragonChessPiece[][][] pieces) {
        this.pieces = Arrays.copyOf(pieces, pieces.length);
        assertUniqueIds();
    }

    /**
     * Throws an {@link IllegalStateException} if any piece in {@link DragonChessBoardState#pieces}
     * has the same ID as another.
     * @throws IllegalStateException If any IDs in {@link DragonChessBoardState#pieces} match
     */
    private void assertUniqueIds() {
        List<UUID> idList = Arrays.stream(pieces)
                .flatMap(Arrays::stream)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .map(DragonChessPiece::getId)
                .toList();
        // Transform to set removes duplicates, sizes should be the same
        if (idList.size() != Set.of(idList).size()) {
            throw new IllegalStateException("Board contained duplicate IDs");
        }
    }

    /**
     * Returns the piece that is currently standing on the tile specified by the given location.
     * @param position The location of the requested piece
     * @return The piece that is currently standing on the specified tile, or null if emtpy
     * @throws IllegalArgumentException If <tt>position</tt> is null
     */
    public DragonChessPiece getPieceAt(final BoardPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null");
        }
        return getPieceAt(position.getLevel(), position.getWidth(), position.getHeight());
    }

    /**
     * Returns the piece that is currently standing on the tile specified by the given location.<br>
     * Note that the dimension of the board is 3x12x8 and indication starts at 1.<br>
     * Level 1 is the underworld, level 2 is the ground and level 3 is the sky.
     * @param level  The level of the board position
     * @param width  The width-index of the board position
     * @param height The height-index of the board position
     * @return The piece that is currently standing on the specified tile, or null if emtpy
     * @throws IndexOutOfBoundsException If the index exceeds the 3x12x8 board
     */
    public DragonChessPiece getPieceAt(final int level, final int width, final int height) {
        BoardPosition.assertValidPosition(level, width, height);
        return pieces[level - 1][width - 1][height - 1];
    }

    /**
     * Remove the piece at the given location from the game.<br>
     * If that position is already empty, nothing happens.
     * @param position The position to remove a piece from
     * @param piece    The piece to place at the position, use null to remove
     * @throws IllegalArgumentException If <tt>position</tt> is null
     */
    public void setPieceAt(final BoardPosition position, final DragonChessPiece piece) {
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null");
        }
        setPieceAt(position.getLevel(), position.getWidth(), position.getHeight(), piece);
    }

    /**
     * Remove the piece at the given location from the game.<br>
     * If that position is already empty, nothing happens.
     * @param level  The level of the board position
     * @param width  The width-index of the board position
     * @param height The height-index of the board position
     * @param piece  The piece to place at the position, use null to remove
     * @throws IndexOutOfBoundsException If the index exceeds the 3x12x8 board
     */
    public void setPieceAt(final int level,
                           final int width,
                           final int height,
                           final DragonChessPiece piece) {
        BoardPosition.assertValidPosition(level, width, height);
        pieces[level - 1][width - 1][height - 1] = piece;
    }

    /**
     * Returns a copy of the current state of the board. This copy is modifiable, but any
     * modification does not affect the original one.
     * @return A copy of the current state of the board
     */
    public DragonChessBoardState copy() {
        DragonChessPiece[][][] stateCopy =
                new DragonChessPiece[LEVEL_AMOUNT][BOARD_WIDTH][BOARD_HEIGHT];

        for (int levelIndex = 0; levelIndex < LEVEL_AMOUNT; levelIndex++) {
            for (int columnIndex = 0; columnIndex < BOARD_WIDTH; columnIndex++) {
                System.arraycopy(pieces[levelIndex][columnIndex], 0,
                        stateCopy[levelIndex][columnIndex], 0, BOARD_HEIGHT);
            }
        }

        return new DragonChessBoardState(stateCopy);
    }

    /**
     * Returns the player to whom the piece with the given ID belongs.
     * @param pieceId The ID of the piece in question
     * @return The player to whom the piece with the given ID belongs
     * @throws IllegalIdException If the given ID is invalid
     * @throws IllegalArgumentException If <tt>pieceId</tt> is null
     */
    public Player getOwner(final UUID pieceId) throws IllegalIdException {
         return getPiece(pieceId).getPlayer();
    }

    /**
     * Returns the piece with the given ID.
     * @param pieceId The ID of the requested piece
     * @return The piece with the given ID
     * @throws IllegalIdException If the ID is invalid
     * @throws IllegalArgumentException If <tt>pieceId</tt> is null
     */
    public DragonChessPiece getPiece(final UUID pieceId) throws IllegalIdException {
        return Arrays.stream(pieces)
                .flatMap(Arrays::stream)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .filter(piece -> piece.getId().equals(pieceId))
                .findAny() // If exists should be exactly 1
                .orElseThrow(() -> new IllegalIdException("Id " + pieceId + " not found"));
    }

    public BoardPosition getPositionOf(final UUID pieceId) throws IllegalIdException {
        BoardPosition position;

        for (int levelIndex = 1; levelIndex <= LEVEL_AMOUNT; levelIndex++) {
            for (int widthIndex = 1; widthIndex <= BOARD_WIDTH; widthIndex++) {
                for (int heightIndex = 1; heightIndex <= BOARD_HEIGHT; heightIndex++) {
                    position = new BoardPosition(levelIndex, widthIndex, heightIndex);

                    if (getPieceAt(position).getId().equals(pieceId)) {
                        return position;
                    }
                }
            }
        }

        throw new IllegalIdException("Id " + pieceId + " not found");
    }

    public Map<DragonChessPiece, BoardPosition> getPiecePositions() {
        Map<DragonChessPiece, BoardPosition> piecePositions = new HashMap<>();

        DragonChessPiece piece;
        BoardPosition position;
        for (int levelIndex = 1; levelIndex <= LEVEL_AMOUNT; levelIndex++) {
            for (int widthIndex = 1; widthIndex <= BOARD_WIDTH; widthIndex++) {
                for (int heightIndex = 1; heightIndex <= BOARD_HEIGHT; heightIndex++) {
                    position = new BoardPosition(levelIndex, widthIndex, heightIndex);
                    piece = getPieceAt(position);
                    if (piece != null) {
                        piecePositions.put(piece, position);
                    }
                }
            }
        }

        return piecePositions;
    }
}
