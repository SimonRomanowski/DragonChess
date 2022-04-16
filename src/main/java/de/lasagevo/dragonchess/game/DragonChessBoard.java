package de.lasagevo.dragonchess.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class DragonChessBoard {

    Logger logger;

    /**
     * 3x12x8 chess board represented by which piece is standing on the corresponding tile.<br>
     * An empty tile is represented by a 'null'-entry.
     */
    DragonChessBoardState boardState;

    /**
     * Static factory method for a DragonChess board.
     * @param logger The logger that should be used
     * @return A newly created DragonChess board with all pieces in their starting position
     * @throws IllegalArgumentException If <tt>logger</tt> is null
     */
    static DragonChessBoard create(final Logger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Logger can not be null");
        }

        logger.log(FINE, "Creating board");

        DragonChessBoardState boardState = DragonChessBoardUtils.create();

        return new DragonChessBoard(logger, boardState);
    }

    /**
     * Returns a {@link DragonChessBoardState} object containing the information of every tile on
     * the board.
     * @return A copy of the current board state
     */
    DragonChessBoardState getState() {
        logger.log(FINE, "Received state request");
        return boardState.copy();
    }

    /**
     * Returns the player to whom the piece with the given ID belongs.
     * @param pieceId The ID of the piece in question
     * @return The player to whom the piece with the given ID belongs
     * @throws IllegalIdException If the given ID is invalid
     * @throws IllegalArgumentException If <tt>pieceId</tt> is null
     */
    Player getOwner(final UUID pieceId) throws IllegalIdException {
        if (pieceId == null) {
            throw new IllegalArgumentException("Piece ID can not be null");
        }
        logger.log(FINE, "Received owner request for piece with ID " + pieceId);
        return boardState.getOwner(pieceId);
    }

    /**
     * Moves the piece with the given ID to the given location.
     * @param pieceId  The piece to move
     * @param position The location to move the piece to
     * @throws IllegalIdException If the given ID is invalid
     * @throws IllegalArgumentException If <tt>pieceId</tt> or <tt>position</tt> is null
     */
    void movePiece(final UUID pieceId,
                   final BoardPosition position) throws IllegalIdException {
        if (pieceId == null) {
            throw new IllegalArgumentException("Piece ID can not be null");
        }
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null");
        }
        logger.log(FINE, "Moving piece with ID " + pieceId + " to position " + position);
        DragonChessPiece piece = boardState.getPiece(pieceId);
        boardState.setPieceAt(position, piece);
    }

    /**
     * Remove the piece with the given ID from the game.
     * @param pieceId The ID of the piece to remove
     * @throws IllegalIdException If the given ID is invalid
     * @throws IllegalArgumentException If <tt>pieceId</tt> is null
     */
    void removePiece(final UUID pieceId) throws IllegalIdException {
        if (pieceId == null) {
            throw new IllegalArgumentException("Piece ID can not be null");
        }
        logger.log(FINE, "Removing piece with ID " + pieceId);
        boardState.setPieceAt(boardState.getPositionOf(pieceId), null);
    }

    /**
     * Promotes the piece with the given ID.<br>
     * The only type of piece that can promote is the
     * {@link DragonChessPieceType#WARRIOR}. If the given ID belongs to a piece with a different
     * type, an exception is thrown.
     * @param pieceId The ID of the piece to promote
     * @throws IllegalIdException If the given ID is invalid
     * @throws IllegalArgumentException If <tt>pieceId</tt> is null
     * @throws PromotionException If the piece with the given ID can not promote
     */
    void promotePiece(final UUID pieceId) throws IllegalIdException {
        if (pieceId == null) {
            throw new IllegalArgumentException("Piece ID can not be null");
        }
        logger.log(FINE, "Promoting piece with ID " + pieceId);

        DragonChessPiece piece = boardState.getPiece(pieceId);

        // Throws exception for invalid type
        DragonChessPieceType promotionType = piece.getType().getPromotion();

        DragonChessPiece promotedPiece = piece.withType(promotionType);

        boardState.setPieceAt(boardState.getPositionOf(pieceId), promotedPiece);
    }

    /**
     * Returns the ID of the piece at the given position.
     * @param position The position of the requested piece
     * @return The ID of the piece at the given position
     * @throws IllegalArgumentException If <tt>position</tt> is null
     */
    UUID getPiece(final BoardPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null");
        }
        return boardState.getPieceAt(position).getId();
    }

}
