package de.lasagevo.dragonchess.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.lasagevo.dragonchess.game.BoardPosition.BOARD_WIDTH;
import static de.lasagevo.dragonchess.game.BoardPosition.LEVEL_GROUND;
import static de.lasagevo.dragonchess.game.BoardPosition.LEVEL_SKY;
import static de.lasagevo.dragonchess.game.BoardPosition.LEVEL_UNDERWORLD;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.BASILISK;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.KING;
import static de.lasagevo.dragonchess.game.Player.GOLD;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OptionCalculator {

    /**
     * The current board state to calculate the options for.
     */
    DragonChessBoardState boardState;

    /**
     * The history of turns made in this game. This is important for pieces who can only move to the
     * tile they have occupied previously.
     */
    List<TurnEvent> turnHistory;

    /**
     * A map of a specific piece to the position of the board where it is currently located.<br>
     * For the inverse operation, use {@link DragonChessBoardState#getPieceAt(BoardPosition)}.<br>
     * This map is used because the {@link DragonChessBoardState#getPositionOf(UUID)} method has
     * to sequentially search through the entire board.
     */
    Map<DragonChessPiece, BoardPosition> piecePositions;

    Logger logger;

    /**
     * A map of options the gold player has to move each piece.
     */
    @Getter
    Map<UUID, Set<MovementOption>> goldOptions = new HashMap<>();

    /**
     * A map of options the scarlet player has to move each piece.
     */
    @Getter
    Map<UUID, Set<MovementOption>> scarletOptions = new HashMap<>();

    /**
     * True if the gold player's king can be targeted.
     */
    boolean goldKingTargeted = false;

    /**
     * True if the scarlet player's king can be targeted.
     */
    boolean scarletKingTargeted = false;

    private OptionCalculator(final DragonChessBoardState boardState,
                             final List<TurnEvent> turnHistory,
                             final Map<DragonChessPiece, BoardPosition> piecePositions,
                             final Logger logger) {
        this.boardState = boardState.copy();
        this.turnHistory = turnHistory;
        this.piecePositions = piecePositions;
        this.logger = logger;
        calculateOptions();
    }

    public static OptionCalculator create(final DragonChessBoardState state,
                                          final List<TurnEvent> eventHistory,
                                          final Logger logger) {
        if (state == null) {
            throw new IllegalArgumentException("Board state can not be null");
        }
        if (eventHistory == null) {
            throw new IllegalArgumentException("Event history can not be null");
        }
        if (logger == null) {
            throw new IllegalArgumentException("Logger can not be null");
        }

        logger.log(Level.FINE, "Creating OptionsCalculator");

        Map<DragonChessPiece, BoardPosition> piecePositions = state.getPiecePositions();

        return new OptionCalculator(state, eventHistory, piecePositions, logger);
    }

    /**
     * Set the values for {@link OptionCalculator#goldOptions} and
     * {@link OptionCalculator#scarletOptions}.
     */
    private void calculateOptions() {
        DragonChessPiece goldKing = null;
        DragonChessPiece scarletKing = null;
        List<DragonChessPiece> goldPieces = new ArrayList<>();
        List<DragonChessPiece> scarletPieces = new ArrayList<>();

        // Sort all pieces into the 4 categories
        for (DragonChessPiece piece : piecePositions.keySet()) {
            Player player = piece.getPlayer();
            if (piece.getType().equals(KING)) {
                if (GOLD.equals(player)) {
                    goldKing = piece;
                } else {
                    scarletKing = piece;
                }
            } else {
                if (GOLD.equals(player)) {
                    goldPieces.add(piece);
                } else {
                    scarletPieces.add(piece);
                }
            }
        }

        if (goldKing == null) {
            throw new IllegalStateException("No king was found for gold player");
        }
        if (scarletKing == null) {
            throw new IllegalStateException("No king was found for scarlet player");
        }

        // Add options for all pieces except the kings
        goldPieces.forEach(piece -> goldOptions.put(piece.getId(), getPieceOptions(piece)));
        scarletPieces.forEach(piece -> scarletOptions.put(piece.getId(), getPieceOptions(piece)));

        goldOptions.put(goldKing.getId(), getPieceOptions(goldKing));
        scarletOptions.put(scarletKing.getId(), getPieceOptions(scarletKing));

        // If the king can be targeted, he has to be moved
        if (goldKingTargeted) {
            goldPieces.forEach(piece -> goldOptions.put(piece.getId(), Set.of()));
        }
        if (scarletKingTargeted) {
            scarletPieces.forEach(piece -> scarletOptions.put(piece.getId(), Set.of()));
        }
    }

    /**
     * Returns a set of all options that the given piece has to move.<br>
     * This method expects all other pieces options to be put in their respective sets when it
     * encounters a {@link DragonChessPieceType#KING} piece.
     *
     * @param piece The piece to calculate the options for
     * @return A set of all options that the given piece has to move
     */
    private Set<MovementOption> getPieceOptions(final DragonChessPiece piece) {
        Set<MovementOption> pieceOptions = new HashSet<>();
        // Movement may be blocked
        if (!pieceIsFrozen(piece)) {
            switch (piece.getType()) {
                case SYLPH -> addSylphOptions(piece, pieceOptions);

                case GRIFFIN -> addGriffinOptions(piece, pieceOptions);

                case DRAGON -> addDragonOptions(piece, pieceOptions);

                case OLIPHANT -> addOliphantOptions(piece, pieceOptions);

                case UNICORN -> {
                    BoardPosition piecePosition = piecePositions.get(piece);
                    if (piecePosition.getLevel() == LEVEL_GROUND) {
                        // One steps diagonally
                        // widthBase is -1 or 1
                        for (int widthBase = -1; widthBase <= 1; widthBase += 2) {
                            // heightBase is -2 or 2
                            for (int heightBase = -1; heightBase <= 1; heightBase += 2) {
                                // One step orthogonally outwards
                                for (int i = 0; i < 2; i++) {
                                    int widthModifier;
                                    int heightModifier;
                                    if (i == 0) {
                                        widthModifier = 0;
                                        // 1 if heightBase is positive, else -1
                                        heightModifier = heightBase / 2;
                                    } else {
                                        // 1 if widthBase is positive, else -1
                                        widthModifier = widthBase / 2;
                                        heightModifier = 0;
                                    }
                                    int widthOffset = widthBase + widthModifier;
                                    int heightOffset = heightBase + heightModifier;
                                    addSingleMove(pieceOptions, piece, 0, widthOffset, heightOffset);
                                    addSingleCapture(pieceOptions, piece, 0, widthOffset, heightOffset);
                                }
                            }
                        }
                    }
                }
                case HERO -> {
                }
                case THIEF -> {
                }
                case CLERIC -> {
                }
                case MAGE -> {
                }
                case KING -> {
                }
                case PALADIN -> {
                }
                case WARRIOR -> {
                }
                case BASILISK -> {
                }
                case ELEMENTAL -> {
                }
                case DWARF -> {
                }
            }
        }

        return Set.copyOf(pieceOptions);
    }

    private void addOliphantOptions(DragonChessPiece piece, Set<MovementOption> pieceOptions) {
        BoardPosition piecePosition = piecePositions.get(piece);
        if (piecePosition.getLevel() == LEVEL_GROUND) {
            addOrthogonalMoves(pieceOptions, piece);
        }
    }

    /**
     * Returns true if the given piece is frozen, else false.<br>
     * A piece is frozen if there is a basilisk-piece of the opponent underneath it-
     *
     * @param piece The piece to check
     * @return True if the given piece is frozen, else false
     * @throws IllegalArgumentException If <tt>piece</tt> is null
     */
    private boolean pieceIsFrozen(final DragonChessPiece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece can not be null");
        }

        boolean isFrozen = false;
        BoardPosition position = piecePositions.get(piece);
        int level = position.getLevel();
        // Pieces that are on the lowest level can't have a piece below them
        if (level != LEVEL_UNDERWORLD) {
            DragonChessPiece pieceBelow =
                    boardState.getPieceAt(level - 1, position.getWidth(), position.getHeight());
            isFrozen = (pieceBelow != null)
                    && BASILISK.equals(pieceBelow.getType())
                    && !piece.getPlayer().equals(pieceBelow.getPlayer());
        }

        return isFrozen;
    }

    private void addDragonOptions(final DragonChessPiece piece,
                                  final Set<MovementOption> pieceOptions) {
        BoardPosition piecePosition = piecePositions.get(piece);
        if (piecePosition.getLevel() == LEVEL_SKY) {
            // Can move or capture any amount of tiles diagonally
            addDiagonalMoves(pieceOptions, piece);
            // Can move or capture 1 tile orthogonally
            // Can also capture from afar the piece below or 1 tile orthogonally next
            // to that
            addSingleCaptureAfar(pieceOptions, piece, -1, 0, 0);
            for (int i = -1; i <= 1; i += 2) {
                addSingleMove(pieceOptions, piece, 0, i, 0);
                addSingleMove(pieceOptions, piece, 0, 0, i);
                addSingleCapture(pieceOptions, piece, 0, i, 0);
                addSingleCapture(pieceOptions, piece, 0, 0, i);
                addSingleCaptureAfar(pieceOptions, piece, -1, i, 0);
                addSingleCaptureAfar(pieceOptions, piece, -1, 0, i);
            }
        }
    }

    private void addGriffinOptions(final DragonChessPiece piece,
                                   final Set<MovementOption> pieceOptions) {
        BoardPosition piecePosition = piecePositions.get(piece);
        int currentLevel = piecePosition.getLevel();
        if (currentLevel == LEVEL_SKY) {
            // First option:
            // Two steps diagonally
            // widthBase is -2 or 2
            for (int widthBase = -2; widthBase <= 2; widthBase += 4) {
                // heightBase is -2 or 2
                for (int heightBase = -2; heightBase <= 2; heightBase += 4) {
                    // One step orthogonally outwards
                    for (int i = 0; i < 2; i++) {
                        int widthModifier;
                        int heightModifier;
                        if (i == 0) {
                            widthModifier = 0;
                            // 1 if heightBase is positive, else -1
                            heightModifier = heightBase / 2;
                        } else {
                            // 1 if widthBase is positive, else -1
                            widthModifier = widthBase / 2;
                            heightModifier = 0;
                        }
                        int widthOffset = widthBase + widthModifier;
                        int heightOffset = heightBase + heightModifier;
                        addSingleMove(pieceOptions, piece, 0, widthOffset, heightOffset);
                        addSingleCapture(pieceOptions, piece, 0, widthOffset, heightOffset);
                    }
                }
            }

            // Second option
            // One step diagonally, one level down
            for (int widthOffset = -1; widthOffset <= 1; widthOffset += 2) {
                for (int heightOffset = -1; heightOffset <= 1; heightOffset += 2) {
                    addSingleMove(pieceOptions, piece, -1, widthOffset, heightOffset);
                    addSingleCapture(pieceOptions, piece, -1, widthOffset, heightOffset);
                }
            }
        } else if (currentLevel == LEVEL_GROUND) {
            // One step diagonally
            for (int widthOffset = -1; widthOffset <= 1; widthOffset += 2) {
                for (int heightOffset = -1; heightOffset <= 1; heightOffset += 1) {
                    // Possible to both this level and the one above
                    for (int levelOffset = 0; levelOffset <= 1; levelOffset++) {
                        addSingleMove(pieceOptions,
                                piece,
                                levelOffset,
                                widthOffset,
                                heightOffset);
                        addSingleCapture(pieceOptions,
                                piece,
                                levelOffset,
                                widthOffset,
                                heightOffset);
                    }
                }
            }
        }

    }

    private void addSylphOptions(final DragonChessPiece piece,
                                 final Set<MovementOption> pieceOptions) {
        BoardPosition piecePosition = piecePositions.get(piece);
        int currentLevel = piecePosition.getLevel();
        if (currentLevel == LEVEL_SKY) {
            addSingleMove(pieceOptions, piece, 0, 1, 1);
            addSingleMove(pieceOptions, piece, 0, -1, 1);
            addSingleCapture(pieceOptions, piece, 0, 0, 1);
            addSingleCapture(pieceOptions, piece, -1, 0, 0);
        } else if (currentLevel == LEVEL_GROUND) {
            addSingleMove(pieceOptions, piece, 1, 0, 0);
            // Starting height of sylph pieces
            int height = (GOLD.equals(piece.getPlayer()) ? 2 : 7);
            for (int width = 1; width <= BOARD_WIDTH; width += 2) {
                addSingleMoveAbsolute(pieceOptions, piece, LEVEL_SKY, width, height);
            }
        }
    }

    private void addOrthogonalMoves(final Set<MovementOption> pieceOptions,
                                    final DragonChessPiece piece) {
    }

    private void addSingleMoveAbsolute(final Set<MovementOption> pieceOptions,
                                       final DragonChessPiece piece,
                                       final int level,
                                       final int width,
                                       final int height) {
    }

    private void addSingleCapture(final Set<MovementOption> pieceOptions,
                                  final DragonChessPiece piece,
                                  final int levelOffset,
                                  final int widthOffset,
                                  final int heightOffset) {
    }

    private void addSingleMove(final Set<MovementOption> pieceOptions,
                               final DragonChessPiece piece,
                               final int levelOffset,
                               final int widthOffset,
                               final int heightOffset) {
    }

    private void addSingleCaptureAfar(final Set<MovementOption> pieceOptions,
                                      final DragonChessPiece piece,
                                      final int levelOffset,
                                      final int widthOffset,
                                      final int heightOffset) {

    }

    private void addDiagonalMoves(final Set<MovementOption> pieceOptions,
                                  final DragonChessPiece piece) {
        addDiagonalFrontMoves(pieceOptions, piece);
        addDiagonalBackMoves(pieceOptions, piece);
    }


    private void addDiagonalBackMoves(final Set<MovementOption> pieceOptions,
                                      final DragonChessPiece piece) {

    }

    private void addDiagonalFrontMoves(final Set<MovementOption> pieceOptions,
                                       final DragonChessPiece piece) {

    }

}
