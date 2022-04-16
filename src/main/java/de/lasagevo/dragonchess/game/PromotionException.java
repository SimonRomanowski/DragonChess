package de.lasagevo.dragonchess.game;

public class PromotionException extends RuntimeException {

    public PromotionException() {
        super();
    }

    public PromotionException(final String message) {
        super(message);
    }

}
