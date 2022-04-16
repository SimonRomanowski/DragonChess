package de.lasagevo.dragonchess.game;

import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@SuppressWarnings("ClassCanBeRecord")
@Value
public class BoardPosition {

    /**
     * The amount of levels in a board of DragonChess
     */
    public static final int LEVEL_AMOUNT = 3;

    /**
     * The amount of columns in a board of DragonChess
     */
    public static final int BOARD_WIDTH = 12;

    /**
     * The amount of rows in a board of DragonChess
     */
    public static final int BOARD_HEIGHT = 8;

    /**
     * Constant representing the value for the level "sky".
     */
    public static final int LEVEL_SKY = 3;

    /**
     * Constant representing the value for the level "ground".
     */
    public static final int LEVEL_GROUND = 2;

    /**
     * Constant representing the value for the level "underworld".
     */
    public static final int LEVEL_UNDERWORLD = 1;

    @Max(LEVEL_AMOUNT)
    @Min(1)
    int level;

    @Max(BOARD_WIDTH)
    @Min(1)
    int width;

    @Max(BOARD_HEIGHT)
    @Min(1)
    int height;

    /**
     * Throw a {@link IndexOutOfBoundsException} if the given position is not indexing a tile
     * on a DragonChess board.
     * @param level  The level of the position
     * @param width  The width-index of the position
     * @param height The height-index of the position
     * @throws IndexOutOfBoundsException If any index is out of bounds for a DragonChess board
     */
    static void assertValidPosition(final int level, final int width, final int height) {
        if (!isValidPosition(level, width, height)) {
            throw new IndexOutOfBoundsException(
                    "Position (" + level + ", " + width + ", " + height + ") is out of bounds");
        }
    }

    static boolean isValidPosition(final int level, final int width, final int height) {
        return !(level <= 0 || level > LEVEL_AMOUNT
            || width <= 0 || width > BOARD_WIDTH
            || height <= 0 || height > BOARD_HEIGHT);
    }

    /**
     * Returns this position in algebraic notation.<br>
     * That means the location is translated into a string of the form
     * {level/int}{width/char}{height/int}.<br>
     * For example, the location with level 2, width 3 and height 4 is represented as the string
     * "2c4".
     * @return The algebraic representation of this position
     */
    @Override
    public String toString() {
        // Get the char for the column name by adding to the ASCII code of 'a'
        char widthChar = (char) (((int) 'a') + width - 1);
        return "" + level + widthChar + height;
    }

}
