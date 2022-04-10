package de.lasagevo.dragonchess.game;

import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Value
public class BoardPosition {

    public static final int LEVEL_AMOUNT = 3;

    public static final int BOARD_WIDTH = 12;

    private static final int BOARD_HEIGHT = 8;

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
