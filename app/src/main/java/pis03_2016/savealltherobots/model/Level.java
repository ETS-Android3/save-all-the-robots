package pis03_2016.savealltherobots.model;


import java.util.ArrayList;

/**
 * Level of the game, containing a resolution time, an inventory, a grid of squares filled with
 * game elements
 */
public class Level {

    /**
     * total amount of seconds the player has to resolve the level
     */
    private int totalTime;

    /**
     * inventory of the level
     */
    private LevelInventory inventory;

    /**
     * grid of the level
     */
    private LevelGrid grid;


    /**
     * Constructor
     */
    public Level() {
        this.totalTime = 0;
        this.inventory = new LevelInventory();
        this.grid = new LevelGrid();
    }

    /**
     * Returns the total time
     *
     * @return the total time
     */
    public int getTotalTime() {
        return totalTime;
    }

    /**
     * Set the total time of the level
     *
     * @param totalTime the total time of the level
     */
    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    /**
     * Returns the inventory
     *
     * @return the inventory
     */
    public LevelInventory getInventory() {
        return inventory;
    }

    /**
     * Returns the level grid
     *
     * @return the level grid
     */
    public LevelGrid getGrid() {
        return grid;
    }

    /**
     * Returns whether the level resolution was successful,
     * processing the reaction of robots to the activation of traps
     * * @param isCheckedByLevelSolver    Whether this is being called by level solver (to optimize some features)
     *
     * @return the condition result
     */
    public boolean determineLevelSuccess(boolean isCheckedByLevelSolver) {

        // once all robots have been placed, determine which traps attack each square
        grid.establishTrapsAttackingEachSquare();

        // consider that robots are always placed when checking by the level solver (to optimize inventory movements)
        boolean areAllRobotsPlaced = (isCheckedByLevelSolver) || !inventory.hasRobotsRemaining();

        boolean canAllRobotsSurvive = grid.canAllRobotsSurvive();

        // level is successful only if all robots have been placed, and in safe enough squares
        return areAllRobotsPlaced && canAllRobotsSurvive;

    }

    /**
     * Establishes which traps attack each square
     */
    public void establishTrapsAttackingEachSquare() {
        grid.establishTrapsAttackingEachSquare();
    }

    /**
     * Obtains and returns the robots that can't survive so explode
     *
     * @return the exploding robots
     */
    public ArrayList<LevelSquare> obtainExplodingRobotsSquares() {
        return getGrid().getExplodingRobotsSquares();
    }

    /**
     * Obtains all traps display data units usable to show their attacks effects
     *
     * @return the trap display data units
     */
    public ArrayList<VisualEffectDisplayData> obtainTrapsAttackDisplayUnits() {
        return getGrid().obtainTrapsAttackDisplayUnits();
    }


    /**
     * Obtain the quantity of robots saved in the level
     *
     * @return saved robots amount
     */
    public int obtainSavedRobotsAmount() {
        return grid.getSquaresWithRobots().size() - grid.getExplodingRobotsSquares().size();
    }


    /**
     * Returns the display data units for the explosive robots exploding at their own squares
     *
     * @return VisualEffectDisplayData array
     */
    public ArrayList<VisualEffectDisplayData> obtainExplosiveRobotsSelfExplosionDisplayUnits() {
        return getGrid().obtainExplosiveRobotsSelfExplosionDisplayUnits();
    }


    /**
     * Creates and returns all the robots to be created from the inventory
     *
     * @return the created robots
     */
    public ArrayList<Robot> createAllInventoryRobots() {
        return inventory.createAllRobots();

    }

    /**
     * Returns all the squares that are not occupied
     *
     * @return all the squares that are not occupied
     */
    public ArrayList<LevelSquare> obtainAllNonOccupiedSquares() {

        return grid.obtainAllNonOccupiedSquares();
    }


    /**
     * Returns whether the robot installed at the passed square can survive there
     *
     * @param square level square to check
     * @return whether the square robot can survive
     */
    public boolean canRobotOfSquareSurvive(LevelSquare square) {
        return grid.canRobotOfSquareSurvive(square);
    }


    /**
     * Returns the amount of squares that are not attacked at all and that are still non-occupied
     *
     * @return the amount of squares that are not attacked at all and that are still non-occupied
     */
    public int obtainUnattackedEmptySquaresAmount() {
        return obtainUnattackedEmptySquares().size();
    }

    /**
     * Returns the squares that are not attacked at all and that are still non-occupied
     *
     * @return the squares that are not attacked at all and that are still non-occupied
     */
    public ArrayList<LevelSquare> obtainUnattackedEmptySquares() {
        return grid.obtainNonAttackedEmptySquares();
    }

    /**
     * Returns whether the inventory has explosive robots
     *
     * @return whether the inventory has explosive robots
     */
    public boolean areThereExplosiveRobots() {
        return inventory.areThereExplosiveRobots();
    }


    /**
     * Randomly find and return a non-occupied square
     *
     * @return the found square, or null if none
     */
    public LevelSquare randomlyObtainNonOccupiedSquare() {
        return grid.randomlyObtainNonOccupiedSquare();
    }


    /**
     * Returns all squares whose traps cannot attack any squares
     *
     * @return all squares whose traps cannot attack any squares
     */
    public ArrayList<LevelSquare> obtainSquaresOfTrapablesThatCannotAttackAnySquare() {
        return grid.obtainSquaresOfTrapablesThatCannotAttackAnySquare();
    }


    /**
     * Returns the trap-like objects attacking the passed square
     *
     * @param square square to check
     * @return the traps attacking the square
     */
    public ArrayList<Trapable> obtainTrapsAttackingSquare(LevelSquare square) {
        return grid.obtainTrapsAttackingSquare(square);
    }

    /**
     * Returns whether the passed square's trap can attack any valid square (empty or with a robot)
     *
     * @param trapSquare square to check
     * @return whether the passed squares trap can attack at least one valid square (empty or with a robot)
     */
    public boolean canSquareAttackAtLeastOneValidSquare(LevelSquare trapSquare) {
        return grid.canSquareAttackAtLeastOneValidSquare(trapSquare);
    }


    /**
     * Returns whether the passed square is the first one attacked by the passed laser cannon trap
     *
     * @param square      square to check
     * @param laserCannon cannon to check
     * @return whether the passed square is the first one attacked by the cannon
     */
    public boolean isSquareTheFirstOneAttackedByLaser(LevelSquare square, Trap_LaserCannon laserCannon) {

        return grid.isSquareTheFirstOneAttackedByLaser(square, laserCannon);
    }

    /**
     * Check if different number total robots and robots placed and not placed
     *
     * @return true if exists robots hiding
     */
    public int checkIfRobotsGoneExists() {

        int totalRobotLevel = getInventory().obtainTotalRobotNumber();
        int totalRobotLevelNotPlaced = getInventory().obtainTotalRobotNumberNotPlaced();
        int totalRobotLevelPlaced = grid.getSquaresWithRobots().size();

        if (totalRobotLevel == totalRobotLevelNotPlaced + totalRobotLevelPlaced) {
            return 0;

        } else if (totalRobotLevel < totalRobotLevelNotPlaced + totalRobotLevelPlaced) {
            return -1;
        }

        return 1;
    }
}
