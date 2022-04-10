package de.lasagevo.dragonchess.game;

import lombok.NonNull;
import lombok.Value;

@Value
public class MovementOption {

    @NonNull
    BoardPosition position;

    @NonNull
    MovementType type;

}
