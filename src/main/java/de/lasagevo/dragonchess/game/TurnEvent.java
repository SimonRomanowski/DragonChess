package de.lasagevo.dragonchess.game;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.UUID;

@SuppressWarnings("ClassCanBeRecord")
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TurnEvent {

    public static final TurnEvent INVALID_PLAYER_EVENT = new TurnEvent(
            TurnEventType.INVALID_PLAYER,
            null,
            null,
            null);

    public static final TurnEvent INVALID_LOCATION_EVENT = new TurnEvent(
            TurnEventType.INVALID_LOCATION,
            null,
            null,
            null);

    /**
     * Gives information on whether the turn was successful and if so what exactly happened.
     */
    @NonNull
    TurnEventType type;

    /**
     * Contains the UUID of the piece affected by the effect described through
     * {@link TurnEvent#getType()}.<br>
     * For types that do not have an effect on any piece, this value is undefined.
     */
    UUID pieceId;

    /**
     * Contains the position of the piece affected by this action.<br>
     * For types that do not have an effect on any piece, this value is undefined.
     */
    BoardPosition position;

    /**
     * Contains the UUID of the piece that has been captured  by the unit referred to by
     * {@link TurnEvent#getPieceId()}.<br>
     * For types other than {@link TurnEventType#PIECE_CAPTURED} or
     * {@link TurnEventType#PIECE_CAPTURED_AFAR} this is undefined.
     */
    UUID capturedPieceId;

    /**
     * Returns an event of the current type if it is an end-game type. That means it has to be one
     * of {@link TurnEventType#WIN_GOLD}, {@link TurnEventType#WIN_SCARLET} or
     * {@link TurnEventType#TIE}
     * @param eventType The type of end-game
     * @return An event of the current type
     */
    public static TurnEvent endGameEvent(final TurnEventType eventType) {
        if (eventType.isEndGameEvent()) {
            return new TurnEvent(eventType, null, null, null);
        } else {
            throw new IllegalArgumentException("Given type was not an end-game type");
        }
    }

    /**
     * Creates a move-event with the given piece's ID and position.
     * @param pieceId  The ID of the piece that has moved
     * @param position The position the piece moved to
     * @return A move-event with the given piece's ID and position
     */
    public static TurnEvent moveEvent(final UUID pieceId, final BoardPosition position) {
        if (pieceId == null) {
            throw new IllegalArgumentException("Piece ID can not be null");
        }
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null");
        }
        return new TurnEvent(TurnEventType.PIECE_MOVED, pieceId, position, null);
    }

    /**
     * Creates a capture-event with the given piece's ID and position.
     * @param pieceId         The ID of the piece that captured another
     * @param capturedPieceId The ID of the piece that has been captured
     * @param position        The position at which the piece has been captured
     * @return A capture-event with the given piece's ID and position
     */
    public static TurnEvent captureEvent(final UUID pieceId,
                                         final UUID capturedPieceId,
                                         final BoardPosition position) {
        if (pieceId == null) {
            throw new IllegalArgumentException("Piece ID can not be null");
        }
        if (capturedPieceId == null) {
            throw new IllegalArgumentException("Captured piece ID can not be null");
        }
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null");
        }
        return new TurnEvent(TurnEventType.PIECE_CAPTURED, pieceId, position, capturedPieceId);
    }

    /**
     * Creates a capture-from-afar-event with the given piece's ID and position.
     * @param pieceId         The ID of the piece that captured another from afar
     * @param capturedPieceId The ID of the piece that has been captured from afar
     * @param position        The position at which the piece has been captured
     * @return A capture-event with the given piece's ID and position
     */
    public static TurnEvent captureAfarEvent(final UUID pieceId,
                                             final UUID capturedPieceId,
                                             final BoardPosition position) {
        if (pieceId == null) {
            throw new IllegalArgumentException("Piece ID can not be null");
        }
        if (capturedPieceId == null) {
            throw new IllegalArgumentException("Captured piece ID can not be null");
        }
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null");
        }
        return new TurnEvent(TurnEventType.PIECE_CAPTURED_AFAR, pieceId, position, capturedPieceId);
    }

    /**
     * Creates a promotion-event with the given piece's ID and position.
     * @param pieceId  The ID of the piece that has promoted
     * @param position The location that piece has moved to in order to promote
     * @return A promotion-event with the given piece's ID and position
     */
    public static TurnEvent promoteEvent(final UUID pieceId,
                                         final BoardPosition position) {
        if (pieceId == null) {
            throw new IllegalArgumentException("Piece ID can not be null");
        }
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null");
        }
        return new TurnEvent(TurnEventType.PIECE_PROMOTED, pieceId, position, null);
    }
}
