package pis03_2016.savealltherobots.controller;

import android.content.Context;

import java.util.ArrayList;

import pis03_2016.savealltherobots.model.GameData;

import pis03_2016.savealltherobots.model.LevelSquare;
import pis03_2016.savealltherobots.model.VisualEffectDisplayData;
import pis03_2016.savealltherobots.view.activity.GamePlayActivity;


/**
 * Controller that manages game while in game play (playing in levels and watching score between
 * levels)
 */
public class GamePlayController {

    /**
     * Keeping the time left if the user rotates the mobile.
     * If the variable is -1 it means that the activity is new.
     */
    public static long TIME_LEFT = -1;
    /**
     * Singleton instance.
     */
    private static GamePlayController instance = null;
    /**
     * Model's game data
     */
    private GameData gameData;

    /**
     * Constructor
     */
    private GamePlayController() {
        gameData = new GameData();
    }

    /**
     * Returns the singleton instance, creating it if non-existent
     *
     * @return the instance
     */
    public static GamePlayController getInstance() {
        if (instance == null) {
            instance = new GamePlayController();
        }
        return instance;
    }

    /**
     * Returns the game data
     *
     * @return the game data
     */
    public GameData getGameData() {
        return gameData;
    }

    /**
     * Invoke level generation
     *
     * @param context action context
     */
    public void generateLevel(Context context) {
        gameData.generateLevel(context);
    }

    /**
     * Generates next level
     *
     * @param context action context
     */
    public void prepareNextLevel(Context context) {
        this.generateLevel(context);
    }


    /**
     * Shows exploding and being defeated the robots that can't survive the attacks of traps
     */
    public ArrayList<LevelSquare> obtainExplodingRobotsSquares() {
        return getGameData().obtainExplodingRobotsSquares();
    }

    /**
     * Obtains all traps display data units usable to show their attacks effects
     *
     * @return the trap display data units
     */
    public ArrayList<VisualEffectDisplayData> obtainTrapsAttackDisplayUnits() {
        return getGameData().obtainTrapsAttackDisplayUnits();
    }

    /**
     * Update the game state with new score and other data, returning whether it is successfully
     * resolved
     *
     * @return whether the level is successfully resolved
     */
    public boolean updateGameState() {
        return getGameData().updateGameState();
    }


    /**
     * delete volatile game data, replacing it with new empty data
     */
    public void resetGameData() {
        gameData = new GameData();
    }


    /**
     * Returns the display data units for the explosive robots exploding at their own squares
     *
     * @return array of VisualEffects
     */
    public ArrayList<VisualEffectDisplayData> obtainExplosiveRobotsSelfExplosionDisplayUnits() {
        return getGameData().obtainExplosiveRobotsSelfExplosionDisplayUnits();
    }

    /**
     * Sets next level is ready
     */
    public void setNextLevelReadySuccess() {
        GamePlayActivity.isNextLevelReady = true;
    }
}
