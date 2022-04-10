package de.lasagevo.dragonchess.game;

public enum Player {
    GOLD, SCARLET;

    @Override
    public String toString() {
        String playerString = "unknown";

        switch (this) {
            case GOLD -> playerString = "gold";
            case SCARLET -> playerString = "scarlet";
        }

        return playerString;
    }
}
