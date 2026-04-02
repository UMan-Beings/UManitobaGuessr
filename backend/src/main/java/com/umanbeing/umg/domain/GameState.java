package com.umanbeing.umg.domain;

/**
 * Enumeration representing the different states of a game.
 * It is used to track the current state of a game and determine the next action to be taken.
 * <p>It provides the following options:
 * <li>GUESS - the player is making a guess</li>
 * <li>REVEAL - the correct answer is being revealed</li>
 * <li>FINISHED - the game is finished</li>
 * </p>
 */
public enum GameState {
    /**
     * GUESS - the player is making a guess
     */
    GUESS,
    /**
     * REVEAL - the correct answer is being revealed
     */
    REVEAL,
    /**
     * FINISHED - the game is finished
     */
    FINISHED
}
