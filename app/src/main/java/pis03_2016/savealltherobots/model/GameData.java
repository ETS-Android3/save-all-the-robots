package pis03_2016.savealltherobots.model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Highest-hierarchy block of game data
 */
public class GameData {

    /**
     * State of the game
     */
    private GameState gameState;

    /**
     * currently played level
     */
    private Level currentLevel;
    /**
     * mode in which levels should be created
     */
    private ELevelCreationMode levelCreationMode;


    /**
     * Constructor
     */
    public GameData() {
        this.gameState = new GameState();
        UnlockableItems u = new UnlockableItems(gameState);
        gameState.setUnlockedItems(u);
        this.levelCreationMode = ELevelCreationMode.FROM_ALGORITHM;
        //this.levelCreationMode = ELevelCreationMode.FROM_FILE;


        // level is null until created by generation
        this.currentLevel = null;
    }


    /**
     * Generates a level
     *
     * @param context application context
     */
    public void generateLevel(Context context) {
        switch (levelCreationMode) {
            case FROM_ALGORITHM:

                long startTime = System.currentTimeMillis();

                this.currentLevel = LevelGenerator.generateLevelFromDifficulty(context, gameState.getDifficulty(), gameState.getAllowedRobotClasses(), gameState.getAllowedTrapClasses(), startTime);

                // DEBUG
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println("LEVEL GENERATION TIME:" + elapsedTime + "ms");

                break;

            case FROM_FILE:

                // TODO: this is a temporary new level loading system that just moves between two fixed levels.
                String filename = (gameState.getScore() == 0) ? "level1.txt" : "level2.txt";

                this.currentLevel = LevelGenerator.generateLevelFromFile(context, filename);
                break;

            default:
                break;
        }
    }

    /**
     * Obtains and returns the robots that can't survive so explode
     *
     * @return the exploding robots
     */
    public ArrayList<LevelSquare> obtainExplodingRobotsSquares() {
        return currentLevel.obtainExplodingRobotsSquares();
    }


    /**
     * Obtains all traps display data units usable to show their attacks effects
     *
     * @return the trap display data units
     */
    public ArrayList<VisualEffectDisplayData> obtainTrapsAttackDisplayUnits() {
        return getCurrentLevel().obtainTrapsAttackDisplayUnits();
    }

    /**
     * Update the game state with new score and other data, returning whether it is successfully
     * resolved
     *
     * @return whether the level is successfully resolved
     */
    public boolean updateGameState() {
        return getGameState().updateWithLevelResolution(currentLevel);
    }

    /**
     * Returns the display data units for the explosive robots exploding at their own squares
     *
     * @return the different explosion displays
     */
    public ArrayList<VisualEffectDisplayData> obtainExplosiveRobotsSelfExplosionDisplayUnits() {
        return getCurrentLevel().obtainExplosiveRobotsSelfExplosionDisplayUnits();
    }

    /**
     * Returns the current level
     *
     * @return the current level
     */
    public Level getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Returns the current game state
     *
     * @return the current game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Returns whether the level resolution was successful,
     * processing the reaction of robots to the activation of traps
     *
     * @return the condition result
     */
    public boolean determineLevelSuccess() {
        return getCurrentLevel().determineLevelSuccess(false);
    }


    /**
     * sets of values indicating ways in which levels can be created
     */
    private enum ELevelCreationMode {
        FROM_ALGORITHM,
        FROM_FILE
    }


}
