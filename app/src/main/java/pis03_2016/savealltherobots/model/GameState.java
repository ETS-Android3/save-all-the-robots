package pis03_2016.savealltherobots.model;

import java.util.ArrayList;

import java.util.List;

import pis03_2016.savealltherobots.R;


/**
 * set of data describing current game state (score, difficulty, allowed-to-appear game elements...)
 */
public class GameState {

    /**
     * Array of items that players has unlocked
     */
    public static final List<Item> ITEMS = new ArrayList<>();
    /**
     * score gained for saving a robot
     */
    private static final int SCORE_PER_SAVED_ROBOT = 100;
    /**
     * classes of robots allowed to appear since the first level
     */
    private final Class[] startAllowedRobotClasses = {Robot.class};
    /**
     * classes of traps allowed to appear since the first level
     */
    private final Class[] startAllowedTrapClasses = {Trap_Fist.class};
    /**
     * total score accumulated by the player consecutively
     */
    private long score;
    /**
     * amount of robots saved
     */
    private int savedRobotsAmount;
    /**
     * amount of levels successfully completed
     */
    private int completedLevelsAmount;
    /**
     * current game difficulty
     */
    private int difficulty;
    /**
     * classes of the robots currently allowed to appear in levels
     */
    private ArrayList<Class> allowedRobotClasses;
    /**
     * classes of the traps currently allowed to appear in levels
     */
    private ArrayList<Class> allowedTrapClasses;

    /**
     * Items that player has achieved (Robots and Traps)
     */

    /**
     * whether a new score record has been achieved in the resolution of the most recent level
     */
    private boolean newRecordAchieved;

    /**
     * Static instance of the items to unlock during the game
     */
    private UnlockableItems unlockedItems;

    /**
     * Constructor
     */

    public GameState() {
        this.score = 0;
        this.difficulty = 0;
        this.savedRobotsAmount = 0;
        this.completedLevelsAmount = 0;
        this.newRecordAchieved = false;
        setUpStartAllowedClasses();
    }

    public static List<Item> getITEMS() {
        return ITEMS;
    }

    /**
     * Returns the difficulty
     *
     * @return the difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the items that are not unlocked already.
     *
     * @return UnlocableItems
     */
    public UnlockableItems getUnlockedItems() {
        return this.unlockedItems;
    }

    /**
     * Sets the unlocked items
     *
     * @param unlockedItem unlockedItem
     */
    public void setUnlockedItems(UnlockableItems unlockedItem) {
        this.unlockedItems = unlockedItem;
    }

    /**
     * Returns the score
     *
     * @return the score
     */
    public long getScore() {
        return score;
    }

    /**
     * Returns the quantity of saved robots
     *
     * @return the quantity of saved robots
     */
    public int getSavedRobotsAmount() {
        return savedRobotsAmount;
    }

    /**
     * Returns the quanity of levels completed
     *
     * @return the quantity of levels completed
     */
    public int getCompletedLevelsAmount() {
        return completedLevelsAmount;
    }

    /**
     * Returns whether a new record has been achieved
     *
     * @return whether a new record has been achieved
     */
    public boolean isNewRecordAchieved() {
        return newRecordAchieved;
    }

    /**
     * Returns the allowed robot classes
     *
     * @return the allowed robot classes
     */
    public ArrayList<Class> getAllowedRobotClasses() {
        return allowedRobotClasses;
    }

    /**
     * Returns the allowed trap classes
     *
     * @return the allowed trap classes
     */
    public ArrayList<Class> getAllowedTrapClasses() {
        return allowedTrapClasses;
    }

    /**
     * make allowed classes be the start ones
     */
    private void setUpStartAllowedClasses() {

        allowedRobotClasses = new ArrayList<>();

        for (Class robotClass : startAllowedRobotClasses) {
            allowedRobotClasses.add(robotClass);
            addToUnlockedItemsList(robotClass);
        }

        allowedTrapClasses = new ArrayList<>();

        for (Class trapClass : startAllowedTrapClasses) {
            allowedTrapClasses.add(trapClass);
            addToUnlockedItemsList(trapClass);
        }
    }

    public void addToUnlockedItemsList(Class c) {
        String s = c.getSimpleName();

        switch (s) {
            case "Robot":
                ITEMS.add(new Item("Robot", R.string.description_robot, R.drawable.robot));
                break;
            case "Robot_Deactivator_Radio":
                ITEMS.add(new Item("Robot", R.string.description_robotRadio, R.drawable.robot_deactivator_radio));
                break;
            case "Robot_Diamond":
                ITEMS.add(new Item("Robot", R.string.description_robotDiamond, R.drawable.robot_diamond));
                break;
            case "Robot_Explosive":
                ITEMS.add(new Item("Robot", R.string.description_robotExplosive, R.drawable.robot_explosive));
                break;
            case "Robot_Gold":
                ITEMS.add(new Item("Robot", R.string.description_robotGold, R.drawable.robot_gold));
                break;
            case "Robot_Steel":
                ITEMS.add(new Item("Robot", R.string.description_robotSteel, R.drawable.robot_steel));
                break;
            case "Trap_ElectricCircle":
                ITEMS.add(new Item("Trap", R.string.description_trapSpikesCircle, R.drawable.trap_electriccircle));
                break;
            case "Trap_Fist":
                ITEMS.add(new Item("Trap", R.string.description_trapPunchx1, R.drawable.trap_fist));
                break;
            case "Trap_FlameThrower":
                ITEMS.add(new Item("Trap", R.string.description_trapFlameThrower, R.drawable.trap_flamethrower));
                break;
            case "Trap_LaserCannon":
                ITEMS.add(new Item("Trap", R.string.description_trapLaserCannon, R.drawable.trap_lasercannon));
                break;
            case "Trap_QuadrupleLaserCannon":
                ITEMS.add(new Item("Trap", R.string.description_trapQuadrupleLaserCannon, R.drawable.trap_quadruplelasercannon));
                break;
            default:
                break;
        }

    }


    /**
     * Updates the game state after the resolution of the passed level
     *
     * @param level level to use for the update
     * @return whether the level is successfully resolved
     */
    public boolean updateWithLevelResolution(Level level) {

        // determine if the level is successful (it also performs some calculations that are used
        // in the other updates)
        boolean isSuccessful = level.determineLevelSuccess(false);

        score += calculateScoreOfLevelResolution(level);

        savedRobotsAmount += level.obtainSavedRobotsAmount();

        completedLevelsAmount += (isSuccessful) ? 1 : 0;

        difficulty += (isSuccessful) ? 1 : 0;

        //Unlocks the next item
        if (isSuccessful) {
            unlockedItems.unlockNextItem();
        }

        // update persistent data if new record score is achieved
        if (score > PersistentData.getHighscore()) {
            PersistentData.setHighscore(score);
            PersistentData.setHighscoreSavedRobotsAmount(savedRobotsAmount);
            PersistentData.setHighscoreCompletedLevelsAmount(completedLevelsAmount);
            newRecordAchieved = true;

        } else newRecordAchieved = false;


        return isSuccessful;
    }


    /**
     * Calculates and returns the score won at the passed level
     *
     * @param level level
     * @return the score
     */
    private long calculateScoreOfLevelResolution(Level level) {
        /**
         * Score is the saved robots amount per a constant
         */
        return level.obtainSavedRobotsAmount() * SCORE_PER_SAVED_ROBOT;
    }
}
