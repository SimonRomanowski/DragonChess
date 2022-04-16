package de.lasagevo.dragonchess.game;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;

/**
 * This class represents a game of Dragonchess.<br>
 * A game can be started by calling {@link DragonChessGame#create(String, String)} with the names
 * of both players.
 */
public class DragonChessGame {

    private final Logger logger;

    /**
     * The player who moves next. At the start of the game it is {@link Player#GOLD}.
     */
    @Getter
    private Player currentPlayer = Player.GOLD;

    /**
     * The name of the player associated with {@link Player#GOLD}.
     */
    @Getter
    private final String goldPlayerName;

    /**
     * The name of the player associated with {@link Player#SCARLET}.
     */
    @Getter
    private final String scarletPlayerName;

    /**
     * The board contains information on what pieces are where and belong to which player.
     */
    private final DragonChessBoard board;

    /**
     * This list contains all successful events in this game.
     */
    @Getter
    private final List<TurnEvent> turnHistory = new ArrayList<>();

    /**
     * Map of piece UUID to possible movement options.<br>
     * Will be calculated at the start of every turn.<br>
     * Contains only the information for the gold player.
     */
    private Map<UUID, Set<MovementOption>> goldOptionsCache = null;

    /**
     * Map of piece UUID to possible movement options.<br>
     * Will be calculated at the start of every turn.<br>
     * Contains only the information for the scarlet player.
     */
    private Map<UUID, Set<MovementOption>> scarletOptionsCache = null;

    private DragonChessGame(final Logger logger,
                            final String goldPlayerName,
                            final String scarletPlayerName,
                            final DragonChessBoard board) {
        this.logger = logger;
        this.goldPlayerName = goldPlayerName;
        this.scarletPlayerName = scarletPlayerName;
        this.board = board;

        updateOptionsCache();

        this.logger.log(FINE, "Created game");
    }

    /**
     * Updates the options caches with the movement options for every piece currently in the game.
     * <br>
     * Should either player's cache be empty, it means that this player has lost the game. Note that
     * it is possible for both player's caches to be empty. In this case the game should end in a
     * tie.
     */
    private void updateOptionsCache() {
        OptionCalculator calculator = OptionCalculator.create(
                board.getState(),
                List.copyOf(turnHistory),
                logger);

        goldOptionsCache = calculator.getGoldOptions();
        scarletOptionsCache = calculator.getScarletOptions();
    }

    /**
     * Start a game of DragonChess.<br>
     * The game is initialized with all pieces in their default position. {@link Player#GOLD} will
     * move first.
     * @param goldPlayerName  The name of the first player (gold)
     * @param scarletPlayerName The name of the second player (scarlet)
     * @return An instance of a game of Dragonchess
     */
    public DragonChessGame create(final String goldPlayerName, final String scarletPlayerName) {
        if (goldPlayerName == null || scarletPlayerName == null) {
            throw new IllegalArgumentException("Player name can not be null");
        }
        Logger logger = Logger.getLogger("DragonChess|" + goldPlayerName + "|" + scarletPlayerName);
        logger.addHandler(new ConsoleHandler());

        DragonChessBoard board = DragonChessBoard.create(logger);

        return new DragonChessGame(logger, goldPlayerName, scarletPlayerName, board);
    }

    /**
     * Returns a {@link DragonChessBoardState} containing the information needed to display
     * the current state of the board.
     * @return The information needed to display the current state of the board
     */
    public DragonChessBoardState getBoardState() {
        logger.log(FINE, "Board state request received");
        return board.getState();
    }

    /**
     * Returns true if the piece with the given UUID exists and belongs to the given player.<br>
     * Note that a piece that has been captured (removed from the game) does not belong to any
     * player.
     * @param player  The player to which the piece should belong
     * @param pieceId The UUID of the piece in question
     * @return True if the piece with the given UUID exists, belongs to the given player and is
     *         still in the game, else false
     * @throws IllegalIdException If the given UUID is invalid
     */
    public boolean belongsToPlayer(final Player player, final UUID pieceId)
            throws IllegalIdException {
        logger.log(FINE, "Piece belonging request received.\n"
                + "player : " + player + '\n'
                + "pieceId: " + pieceId);
        if (player == null) {
            throw new IllegalArgumentException("Player can not be null");
        } else if (pieceId == null) {
            throw new IllegalArgumentException("ID can not be null");
        }
        return board.getOwner(pieceId) == player;
    }

    /**
     * Returns all options the given piece has to move.<br>
     * Each {@link MovementOption} includes the position the piece can move to and the type of the
     * movement.
     * @param pieceId The UUID of the piece in question
     * @return A set of all options the given piece has to move
     * @throws IllegalIdException If the given UUID is invalid
     */
    public Set<MovementOption> getOptions(final UUID pieceId) throws IllegalIdException {
        logger.log(FINE, "Retrieving options for piece with id " + pieceId);
        // The options-caches can be expected to be up-to-date
        Map<UUID, Set<MovementOption>> options;
        if (belongsToPlayer(Player.GOLD, pieceId)) {
            options = goldOptionsCache;
        } else if (belongsToPlayer(Player.SCARLET, pieceId)) {
            options = scarletOptionsCache;
        } else {
            // At this point, belongsToPlayer should already have thrown an exception
            logger.log(SEVERE, "belongsToPlayer did throw the proper exception");
            String errorMessage = "Could not find piece with id " + pieceId;
            logger.log(SEVERE, errorMessage);
            throw new IllegalIdException(errorMessage);
        }

        // This can be expected not to be null, since the piece has to be still in the game
        // in order to even reach this point
        return options.get(pieceId);
    }

    /**
     * Attempts to move the piece with the given UUID to the given location or to capture from
     * afar if possible.<br>
     * The return value gives information on whether the turn was taken successfully and if so
     * what happened.<br>
     * Note that if the turn is executed, the other player's turn starts
     * @param pieceId  The UUID of the piece that should be moved
     * @param position The location where the piece should be moved to or capture another from
     * @return A non-empty list of {@link TurnEvent} objects describing what effects the
     *         requested action had.
     * @throws IllegalIdException If the given UUID is invalid
     */
    public List<TurnEvent> movePiece(final UUID pieceId, final BoardPosition position)
            throws IllegalIdException {
        logger.log(FINE, "Attempting to move piece with id " + pieceId
            + " to position " + position);
        TurnEvent event = determineTurnEvent(pieceId, position);

        updateState(event);

        return addEndGameEvent(event);
    }

    /**
     * Creates a list of the given event and potentially add an end-game event to it.<br>
     * Should both players still have options, nothing is added to <tt>events</tt>.
     * @param event The event to potentially add to
     * @return A list containing <tt>event</tt> and potentially an end-game event
     */
    private List<TurnEvent> addEndGameEvent(final TurnEvent event) {
        List<TurnEvent> eventsCopy = new ArrayList<>();
        eventsCopy.add(event);

        boolean goldEmpty = goldOptionsCache.isEmpty();
        boolean scarletEmpty = scarletOptionsCache.isEmpty();
        if (goldEmpty) {
            if (scarletEmpty) {
                eventsCopy.add(TurnEvent.endGameEvent(TurnEventType.TIE));
            } else {
                eventsCopy.add(TurnEvent.endGameEvent(TurnEventType.WIN_SCARLET));
            }
        } else if (scarletEmpty) {
            eventsCopy.add(TurnEvent.endGameEvent(TurnEventType.WIN_GOLD));
        }

        return Collections.unmodifiableList(eventsCopy);
    }

    /**
     * Updates the game board, the current player and the movement options caches based on what
     * event is given.
     * @param event The event that the player's action triggers
     */
    private void updateState(final TurnEvent event) {
        updateBoard(event);

        if (event.getType().isErrorEvent()) {
            switchPlayer();
        } else {
            // If no error has occurred, these events should be added to the history
            turnHistory.add(event);
        }

        updateOptionsCache();
    }

    /**
     * Switches the current player.
     */
    private void switchPlayer() {
        if (currentPlayer == Player.GOLD) {
            currentPlayer = Player.SCARLET;
        } else {
            currentPlayer = Player.GOLD;
        }
    }

    /**
     * Updates the game board based on the given event.
     * @param event The event that should be processed
     */
    private void updateBoard(final TurnEvent event) {
        logger.log(FINE, "Updating board");
        UUID pieceId = event.getPieceId();
        try {
            switch (event.getType()) {

                case PIECE_MOVED, PIECE_CAPTURED -> {
                    // In the case that a piece is captured, we just override that piece
                    BoardPosition position = event.getPosition();
                    board.movePiece(pieceId, position);
                }

                case PIECE_CAPTURED_AFAR -> board.removePiece(event.getCapturedPieceId());

                case PIECE_PROMOTED -> {
                    BoardPosition position = event.getPosition();
                    board.movePiece(pieceId, position);
                    board.promotePiece(pieceId);
                }

                case INVALID_LOCATION, INVALID_PLAYER -> {
                    // Do nothing
                }

                case WIN_GOLD, WIN_SCARLET, TIE -> {
                    // This case should never occur
                    String errorMessage = "Game result has been determined prematurely";
                    logger.log(SEVERE, errorMessage);
                    throw new IllegalStateException(errorMessage);
                }

            }
        } catch (IllegalIdException e) {
            logger.log(SEVERE, "");
        }
    }

    /**
     * Determines the effect of the action described by the given UUID and position.<br>
     * Note that it is not yet considered whether a player wins or not.
     * @param pieceId  The UUID of the piece that should be moved
     * @param position The position the given piece is supposed to move to or capture from
     * @return A description of the event the given action causes
     * @throws IllegalIdException If the given UUID is invalid
     */
    private TurnEvent determineTurnEvent(final UUID pieceId,
                                         final BoardPosition position)
            throws IllegalIdException {
        logger.log(FINE, "Determining turn event for piece with ID " + pieceId
            + " at board position " + position);
        TurnEvent event;
        // The piece has to currently belong to a player
        if (!belongsToPlayer(currentPlayer, pieceId)) {
            logger.log(Level.WARNING, "Tried to move piece with ID " + pieceId
                    + " but it does not belong to the current player (" + currentPlayer + ").");
            event = TurnEvent.INVALID_PLAYER_EVENT;
        } else {
            Map<UUID, Set<MovementOption>> options = getPlayerOptions(currentPlayer);
            // This can be expected not to be null, since the check before validated that this
            // piece ID is assigned to the current player
            Set<MovementOption> movementOptions = options.get(pieceId);
            Optional<MovementOption> movementOptional = movementOptions.stream()
                    .filter(opt -> opt.getPosition().equals(position))
                    .findAny(); // There only should be one or zero
            if (movementOptional.isPresent()) {
                MovementOption option = movementOptional.get();

                switch (option.getType()) {

                    case MOVE -> event = TurnEvent.moveEvent(pieceId, position);

                    case CAPTURE -> {
                        // This can be expected to not be invalid, since it stems from the
                        // options cache
                        UUID capturedPiece = board.getPiece(position);
                        event = TurnEvent.captureEvent(pieceId, capturedPiece, position);
                    }
                    case CAPTURE_AFAR -> {
                        // This can be expected to not be invalid, since it stems from the
                        // options cache
                        UUID capturedPiece = board.getPiece(position);
                        event = TurnEvent.captureAfarEvent(pieceId, capturedPiece, position);
                    }
                    case PROMOTE -> event = TurnEvent.promoteEvent(pieceId, position);

                    default -> {
                        String errorMessage = "Invalid movement option encountered";
                        logger.log(SEVERE, errorMessage);
                        throw new IllegalStateException(errorMessage);
                    }

                }

            } else {
                event = TurnEvent.INVALID_LOCATION_EVENT;
            }

        }

        return event;
    }

    /**
     * Returns the cached movement-options for the given player.
     * @param player The player whose movement options should be returned
     * @return The cached movement-options for the given player
     */
    private Map<UUID, Set<MovementOption>> getPlayerOptions(final Player player) {
        logger.log(FINE, "Request received for options for player " + player);
        if (player == null) {
            throw new IllegalArgumentException("Player can not be null");
        }

        Map<UUID, Set<MovementOption>> options;

        switch (player) {

            case GOLD -> options = goldOptionsCache;

            case SCARLET -> options = scarletOptionsCache;

            default -> {
                String errorMessage = "Invalid player type encountered";
                logger.log(SEVERE, errorMessage);
                throw new IllegalStateException(errorMessage);
            }

        }

        return options;
    }

}
