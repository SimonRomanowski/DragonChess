package de.lasagevo.dragonchess.game;

public enum DragonChessPieceType {
    SYLPH,
    GRIFFIN,
    DRAGON,
    OLIPHANT,
    UNICORN,
    HERO,
    THIEF,
    CLERIC,
    MAGE,
    KING,
    PALADIN,
    WARRIOR,
    BASILISK,
    ELEMENTAL,
    DWARF;

    public boolean isPromotable() {
        // Only the warrior can promote
        return this == WARRIOR;
    }

    /**
     * Returns the type of piece into which this type promotes.
     * @return The type of piece into which this type promotes
     * @throws PromotionException If this type of piece can not promote
     */
    public DragonChessPieceType getPromotion() {
        // Only the warrior can promote
        if (this == DragonChessPieceType.WARRIOR) {
            return PALADIN;
        }
        throw new PromotionException("Piece of type " + this + " can not promote");
    }

    @Override
    public String toString() {
        String typeString = "unknown";

        switch (this) {

            case SYLPH -> typeString = "sylph";

            case GRIFFIN -> typeString = "griffin";

            case DRAGON -> typeString = "dragon";

            case OLIPHANT -> typeString = "oliphant";

            case UNICORN -> typeString = "unicorn";

            case HERO -> typeString = "hero";

            case THIEF -> typeString = "thief";

            case CLERIC -> typeString = "cleric";

            case MAGE -> typeString = "mage";

            case KING -> typeString = "king";

            case PALADIN -> typeString = "paladin";

            case WARRIOR -> typeString = "warrior";

            case BASILISK -> typeString = "basilisk";

            case ELEMENTAL -> typeString = "elemental";

            case DWARF -> typeString = "dwarf";

        }

        return typeString;
    }
}
