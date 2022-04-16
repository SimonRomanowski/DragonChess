package de.lasagevo.dragonchess.game;

import static de.lasagevo.dragonchess.game.BoardPosition.BOARD_HEIGHT;
import static de.lasagevo.dragonchess.game.BoardPosition.BOARD_WIDTH;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.BASILISK;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.CLERIC;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.DRAGON;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.DWARF;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.ELEMENTAL;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.GRIFFIN;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.HERO;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.KING;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.MAGE;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.OLIPHANT;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.PALADIN;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.THIEF;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.UNICORN;
import static de.lasagevo.dragonchess.game.DragonChessPieceType.WARRIOR;
import static de.lasagevo.dragonchess.game.Player.GOLD;
import static de.lasagevo.dragonchess.game.Player.SCARLET;

public class DragonChessBoardUtils {

    // This class should not be instantiated
    private DragonChessBoardUtils() {
    }

    public static DragonChessBoardState create() {
        DragonChessPiece[][] boardSky = createSky();
        DragonChessPiece[][] boardGround = createGround();
        DragonChessPiece[][] boardUnderworld = createUnderworld();

        return new DragonChessBoardState(boardUnderworld, boardGround, boardSky);
    }

    private static DragonChessPiece[][] createUnderworld() {
        DragonChessPiece[][] boardUnderworld = createGeneric();

        // Place dwarfs
        // Each player has 6 dwarfs in the second row on every other tile starting at column 2
        for (int columnIndex = 1; columnIndex < BOARD_WIDTH; columnIndex += 2) {
            // Gold dwarf
            boardUnderworld[columnIndex][1] = DragonChessPiece.create(DWARF, GOLD);

            // Scarlet dwarf
            boardUnderworld[columnIndex][10] = DragonChessPiece.create(DWARF, SCARLET);
        }

        // Place basilisks
        // Each player has 2 basilisks in their back row in column 3 and 11

        // Gold basilisks
        boardUnderworld[0][ 2] = DragonChessPiece.create(BASILISK, GOLD);
        boardUnderworld[0][10] = DragonChessPiece.create(BASILISK, GOLD);

        // Scarlet basilisk
        boardUnderworld[7][ 2] = DragonChessPiece.create(BASILISK, SCARLET);
        boardUnderworld[7][10] = DragonChessPiece.create(BASILISK, SCARLET);

        // Place elementals
        // Each player has one elemental in their back row in column 7

        // Gold elemental
        boardUnderworld[0][6] = DragonChessPiece.create(ELEMENTAL, GOLD);

        // Scarlet elemental
        boardUnderworld[7][6] = DragonChessPiece.create(ELEMENTAL, SCARLET);

        return boardUnderworld;
    }

    private static DragonChessPiece[][] createGround() {
        DragonChessPiece[][] boardGround = createGeneric();

        // Place oliphants
        // Each player has two oliphants in the corner of their back row

        // Gold oliphants
        boardGround[ 0][0] = DragonChessPiece.create(OLIPHANT, GOLD);
        boardGround[11][0] = DragonChessPiece.create(OLIPHANT, GOLD);

        // Scarlet oliphants
        boardGround[ 0][7] = DragonChessPiece.create(OLIPHANT, SCARLET);
        boardGround[11][7] = DragonChessPiece.create(OLIPHANT, SCARLET);

        // Place unicorns
        // Each player has one unicorn to the side of each of their oliphants

        // Gold unicorns
        boardGround[ 1][0] = DragonChessPiece.create(UNICORN, GOLD);
        boardGround[10][0] = DragonChessPiece.create(UNICORN, GOLD);

        // Scarlet unicorn
        boardGround[ 1][11] = DragonChessPiece.create(UNICORN, SCARLET);
        boardGround[10][11] = DragonChessPiece.create(UNICORN, SCARLET);

        // Place heroes
        // Each player has one hero to the side of each of their unicorns

        // Gold heroes
        boardGround[2][0] = DragonChessPiece.create(HERO, GOLD);
        boardGround[9][0] = DragonChessPiece.create(HERO, GOLD);

        // Scarlet heroes
        boardGround[2][11] = DragonChessPiece.create(HERO, SCARLET);
        boardGround[9][11] = DragonChessPiece.create(HERO, SCARLET);

        // Place thieves
        // Each player has one thief to the side of each of their heroes

        // Gold thieves
        boardGround[3][0] = DragonChessPiece.create(THIEF, GOLD);
        boardGround[8][0] = DragonChessPiece.create(THIEF, GOLD);

        // Scarlet thieves
        boardGround[3][11] = DragonChessPiece.create(THIEF, SCARLET);
        boardGround[8][11] = DragonChessPiece.create(THIEF, SCARLET);

        // Place clerics
        // Each player has one cleric in the back row at column 5

        // Gold cleric
        boardGround[4][0] = DragonChessPiece.create(CLERIC, GOLD);

        // Scarlet cleric
        boardGround[4][11] = DragonChessPiece.create(CLERIC, SCARLET);

        // Place mages
        // Each player has one mage to the side of their cleric

        // Gold mage
        boardGround[5][0] = DragonChessPiece.create(MAGE, GOLD);

        // Scarlet mage
        boardGround[5][11] = DragonChessPiece.create(MAGE, SCARLET);

        // Place Kings
        // Each player has one king to the side of their mage

        // Gold king
        boardGround[6][0] = DragonChessPiece.create(KING, GOLD);

        // Scarlet king
        boardGround[6][11] = DragonChessPiece.create(KING, SCARLET);

        // Place paladins
        // Each player has one paladin to the side of their king

        // Gold paladin
        boardGround[7][0] = DragonChessPiece.create(PALADIN, GOLD);

        // Scarlet paladin
        boardGround[7][11] = DragonChessPiece.create(PALADIN, SCARLET);

        // Place Warriors
        // Each player's second row is filled with warriors
        for (int columnIndex = 0; columnIndex < BOARD_WIDTH; columnIndex++) {
            // Gold warrior
            boardGround[columnIndex][1] = DragonChessPiece.create(WARRIOR, GOLD);

            // Scarlet warrior
            boardGround[columnIndex][10] = DragonChessPiece.create(WARRIOR, SCARLET);
        }

        return boardGround;
    }

    private static DragonChessPiece[][] createSky() {
        DragonChessPiece[][] boardSky = createGeneric();

        // Place sylphs
        // Each player has 6 sylphs in their second row in every other tile starting at column 1
        for (int columnIndex = 0; columnIndex < BOARD_WIDTH; columnIndex += 2) {
            // Gold sylph
            boardSky[columnIndex][1] =
                    DragonChessPiece.create(DragonChessPieceType.SYLPH, GOLD);

            // Scarlet sylph
            boardSky[columnIndex][6] =
                    DragonChessPiece.create(DragonChessPieceType.SYLPH, Player.SCARLET);
        }

        // Place griffins
        // Each player has two griffins starting in the back row in columns 3 and 11

        // Gold griffins
        boardSky[ 2][0] = DragonChessPiece.create(GRIFFIN, GOLD);
        boardSky[10][0] = DragonChessPiece.create(GRIFFIN, GOLD);

        // Scarlet griffins
        boardSky[ 2][7] = DragonChessPiece.create(GRIFFIN, SCARLET);
        boardSky[10][7] = DragonChessPiece.create(GRIFFIN, SCARLET);

        // Place dragons
        // Each player owns one dragon in colum 7 in the back row

        // Gold dragon
        boardSky[6][0] = DragonChessPiece.create(DRAGON, GOLD);
        // Scarlet dragon
        boardSky[6][7] = DragonChessPiece.create(DRAGON, SCARLET);

        return boardSky;
    }

    private static DragonChessPiece[][] createGeneric() {
        return new DragonChessPiece[BOARD_WIDTH][BOARD_HEIGHT];
    }
}
