package pis03_2016.savealltherobots.model;

import android.content.Context;

import java.lang.reflect.Constructor;

import java.util.ArrayList;

import pis03_2016.savealltherobots.controller.GamePlayController;

/**
 * Game levels generator
 */
public class LevelGenerator {


    /**
     * index of the line where to look for the total time of a file-loaded level
     */
    public static final int LEVEL_TOTAL_TIME_LINE_INDEX = LevelGrid.NUM_ROWS + 2;

    /**
     * index of the line where to start looking for the inventory info of a file-loaded level
     */
    public static final int LEVEL_INVENTORY_START_LINE_INDEX = LEVEL_TOTAL_TIME_LINE_INDEX + 2;


    /**
     * minimum amount of totally safe squares a level should have
     */
    public static final int MIN_TOTALLY_SAFE_SQUARES_FOR_LEVEL = 0;

    /**
     * maximum amount of totally safe squares a level should have
     */
    public static final int MAX_TOTALLY_SAFE_SQUARES_FOR_LEVEL = 10;


    /**
     * minimum amount of blocks a level should have
     */
    private static final int MIN_BLOCKS_FOR_LEVEL = 10;


    /**
     * maximum amount of blocks a level should have
     */
    private static final int MAX_BLOCKS_FOR_LEVEL = 25;

    /**
     * maximum number of resistant robots accepted for a level
     */
    private static final int MAX_RESISTANT_ROBOTS_FOR_LEVEL = 3;


    /**
     * probability of having an extra robot in the level
     */
    private static final double EXTRA_ROBOT_PROBABILITY = 0.5;


    /**
     * name of the file to use in case that normal level generation takes too much time
     */
    private static final String TIMEOUT_LEVEL_FILE_NAME = "extraLevel.txt";

    /**
     * maximum time in milliseconds we can tolerate to generate a level
     */
    private static final double MAXIMUM_MILLISECONDS_ACCEPTED_FOR_GENERATION = 5000;


    /**
     * minimum number of milliseconds than can be set to resolve a level
     */
    private static final int MIN_TIME_FOR_LEVEL = 10 * 1000;

    /**
     * number of seconds that is estimated it should take to place a robot
     */
    private static final int SECONDS_TO_PLACE_ROBOT = 4;

    /**
     * code number to indicate error
     */
    private static final int ERROR_CODE = -1;


    /**
     * Returns a random value between the passed limiting values
     *
     * @param lowLimit  low limit value
     * @param highLimit high limit value
     * @return a random value inside the range
     */
    public static int obtainRandomIntBetween(int lowLimit, int highLimit) {
        return lowLimit + (int) (Math.random() * (highLimit - lowLimit + 1));
    }


    /**
     * Generates and returns a level of the passed difficulty and that can only have robots of
     * the passed allowed robot classes and traps from the passed allowed trap classes
     *
     * @param context             application context
     * @param difficulty          indicator of level difficulty
     * @param allowedRobotClasses unlocked robot classes
     * @param allowedTrapClasses  unlocked trap classes
     * @param absoluteStartTime   absolute start time (in milliseconds) in which the whole process started, used in recursive calls
     * @return the generated level
     */
    public static Level generateLevelFromDifficulty(Context context, int difficulty, ArrayList<Class> allowedRobotClasses,
                                                    ArrayList<Class> allowedTrapClasses, long absoluteStartTime) {

        /**
         * LEVEL GENERATOR ALGORITHM EXPLANATION:
         *
         * This is a level generator that creates a level for the game based on a difficulty value
         * and the unlocked robot and trap classes. Through the use of semi-directed random techniques,
         * a level for the indicated difficulty is produced: this consists in a level grid with blocks
         * and traps placed on its squares, a level inventory with the robots to place and a resolution
         * time proportional to the total number of robots. It is important to notice that the level
         * generator only produces solvable levels: it uses the level solution finder to determine whether
         * its candidate generation is a valid level, and modify it until it is solvable.
         *
         * The whole generation process can be divided in different stages. The first one consists in
         * filling the level with blocks and traps from the unlocked types. The number of both is
         * randomly determined between their rational ranges, and having something into account: the
         * approximate number of totally non-attacked squares that the level should have, which is
         * increased with difficulty, which helps giving the impression that levels get progressively
         * harder to resolve. Another noticeable aspect is that traps are only installed in level
         * squares where they can attack at least one square (that is not occupied by a block or
         * a trap, but empty to potentially get a robot installed).
         *
         * The next stage is to determine which robots can be placed in the level, which is influenced
         * by the traps placed in the level, and more specifically the resulting number of attacks that
         * each square receives: in other words, levels with many squares where only resistant robots can
         * survive will need more resistant robots than levels in which the opposite situation takes places.
         * It should be mentioned, nevertheless, that the total number of special robots is limited to a
         * small number: this way we prevent that levels become too easy and help to give the special robots
         * an image of actually being special and rare, not as common as normal robots.
         *
         * The final stage is an adjustment one, in which new blocks are added to be more precise in having
         * approximately the number of non-attacked squares that it was planned for the difficulty parameter.
         * It is important to check, however, that the level has a solution after these changes, otherwise
         * the previous situation is restored, once and again, until we reach a precise situation in terms of
         * non-attacked squares or we have exceeded the expected iterations. In case that after trying many
         * changes in the level it still has no solution, the generation process might start again. In the very
         * exceptional case in which a considerable amount of computational time has elapsed in the generation
         * (including recursive calls, if have taken place), we stop the process and rapidly generate a solvable
         * level established in a file, as an emergency measure. This, as we say, hardly ever has happened in our
         * tests.
         */


        // check if maximum tolerable time for generation has been reached: in that exceptional case use an emergency
        // level generated from a file so the player does not perceive slowness
        if (System.currentTimeMillis() - absoluteStartTime > MAXIMUM_MILLISECONDS_ACCEPTED_FOR_GENERATION) {
            return generateLevelFromFile(context, TIMEOUT_LEVEL_FILE_NAME);
        }

        // level to fill with game elements
        Level level = new Level();

        ArrayList<LevelSquare> squaresWithBlocks = new ArrayList<>();

        // fill the level with blocks and traps
        int nonAttackedSquaresWantedNumber = fillLevelWithBlocksAndTraps(level, difficulty, allowedTrapClasses, squaresWithBlocks);

        // construct the level inventory of robots
        int normalRobotsNumber = constructInventoryForLevel(level, allowedRobotClasses);

        // if the robot assignation gave an error, the generation will need to be repeated
        if (normalRobotsNumber == ERROR_CODE) {
            level = null;
        } else {
            level = adjustAndValidateLevel(level, normalRobotsNumber, nonAttackedSquaresWantedNumber, squaresWithBlocks,
                    context, difficulty,
                    allowedRobotClasses, allowedTrapClasses, absoluteStartTime);
        }

        // if it was not possible to validate the level after several changes, repeat the generation process
        if (level == null) {
            level = generateLevelFromDifficulty(context, difficulty, allowedRobotClasses, allowedTrapClasses, absoluteStartTime);
        }

        // establish the resolution time for the level based on the number of robots to place
        level.setTotalTime(Math.max(MIN_TIME_FOR_LEVEL, level.getInventory().obtainTotalRobotNumber() * 1000 * SECONDS_TO_PLACE_ROBOT));

        // confirm level is created successful
        GamePlayController.getInstance().setNextLevelReadySuccess();

        return level;
    }


    /**
     * Fill the passed level with blocks and traps based on the passed difficulty and unlocked trap types
     *
     * @param level              level to work with
     * @param difficulty         difficulty value
     * @param allowedTrapClasses unlocked trap classes
     * @param squaresWithBlocks  squares with blocks (to be filled by the function)
     * @return the number of squares that we don't want to be attacked according to difficulty
     */
    private static int fillLevelWithBlocksAndTraps(Level level, int difficulty, ArrayList<Class> allowedTrapClasses, ArrayList<LevelSquare> squaresWithBlocks) {

        int squareNumber = level.getGrid().getSquares().size();

        // randomly determine the number of level blocks, within a rational range
        int blockNumber = obtainRandomIntBetween(MIN_BLOCKS_FOR_LEVEL, MAX_BLOCKS_FOR_LEVEL);
        boolean placingBlocks = true;

        // determine the number of non-attacked square that approximately correspond to the current difficulty
        int nonAttackedSquareWantedNumber = calculateNumberOfNonAttackedSquares(difficulty);

        int maxTrapAttempts = 1000;

        // randomly place blocks and then traps over the level
        for (int k = 0; k <= 1; k++) {

            int number = (placingBlocks) ? blockNumber : maxTrapAttempts;

            for (int i = 0; i < number; i++) {

                PlaceableObject object;
                LevelSquare square;

                // placing a block
                if (placingBlocks) {
                    object = createPlaceableObject(Block.class, null);
                    square = level.randomlyObtainNonOccupiedSquare();
                    level.getGrid().installObjectAt(object, square);
                    squaresWithBlocks.add(square);
                }

                // placing a trap
                else {
                    object = (PlaceableObject) randomlyCreateTrapable(allowedTrapClasses);

                    int iterationNumber = 0;
                    int index = level.randomlyObtainNonOccupiedSquare().getId();
                    boolean found = false;

                    // try to find a square in which the trap can attack at least an empty square
                    if (object != null) {
                        while (!found && iterationNumber <= squareNumber) {
                            iterationNumber++;
                            square = level.getGrid().getAt(index);

                            if (square != null && !square.isOccupied()) {
                                level.getGrid().installObjectAt(object, square);
                                level.establishTrapsAttackingEachSquare();
                                // we can only accept installing traps if they don't make the already placed ones useless
                                found = level.canSquareAttackAtLeastOneValidSquare(square) &&
                                        level.obtainSquaresOfTrapablesThatCannotAttackAnySquare().size() == 0;

                                if (!found) {
                                    level.getGrid().uninstallObjectAt(square);
                                    level.establishTrapsAttackingEachSquare();
                                }
                            }

                            index = (index + 1) % squareNumber;
                        }
                    }
                }

                // check if enough traps for the difficulty configuration have been installed
                if (!placingBlocks) {
                    if (nonAttackedSquareWantedNumber >= level.getGrid().obtainNonAttackedEmptySquares().size()) {
                        break;
                    }
                }

            }

            placingBlocks = false;
        }

        return nonAttackedSquareWantedNumber;
    }


    /**
     * Constructs the inventory of robots for the passed level based on the traps and blocks
     * it has and the unlocked robot classes
     *
     * @param level               level to construct the inventory for
     * @param allowedRobotClasses unlocked robot classes
     * @return the number of normal robots
     */
    private static int constructInventoryForLevel(Level level, ArrayList<Class> allowedRobotClasses) {

        int specialRobotNormalRobotReductionFactor = 1;

        ArrayList<LevelSquare> squaresWhereNoRobotCanSurvive = new ArrayList<>();

        // robot classes to consider: the 'null' one will be used indicatively for squares with such a number of attacks
        // that not even the most resistant robot can survive
        Class[] robotClasses = {Robot.class, Robot_Steel.class, Robot_Gold.class, Robot_Diamond.class, null};

        // max quantities of each robot class
        int[] maxRobotQuantities = {12, 3, 2, 1};

        // initially we have 0 robots of each class
        int[] robotQuantities = new int[robotClasses.length];
        for (int i = 0; i < robotQuantities.length; i++) {
            robotQuantities[i] = 0;
        }

        // analyze which traps attack each non-occupied square to determine which robots could the level require
        for (LevelSquare square : level.getGrid().getSquares()) {

            if (!square.isOccupied()) {

                ArrayList<Trapable> trapables = level.obtainTrapsAttackingSquare(square);

                int attackNumber = trapables.size();

                // ignore attacks of laser cannons in squares other than their first ones, since we will
                // suppose, with later code, that resistant robots will neutralize that laser prolongation
                for (Trapable trapable : trapables) {
                    if (trapable instanceof Trap_LaserCannon &&
                            !level.isSquareTheFirstOneAttackedByLaser(square, (Trap_LaserCannon) trapable)) {
                        attackNumber--;
                    }
                }

                // increment the number of the first robot class that can resist the attack
                if (attackNumber < robotClasses.length) {
                    robotQuantities[attackNumber]++;
                }

                // or register the square as one of those in which no robot can survive
                else {
                    squaresWhereNoRobotCanSurvive.add(square);
                }
            }
        }

        int totalRobotCount = 0;
        int normalRobotsNumber = robotQuantities[0];
        int oldNormalRobotsNumber = normalRobotsNumber;

        int inventoryIterations = 0;
        int maxInventoryIterations = 100;

        ArrayList<LevelInventoryCell> inventoryCells = new ArrayList<>();

        boolean hasToReduceSpecialRobotsQuantities = false;

        // prepare the robots inventory cells
        do {

            inventoryIterations++;

            normalRobotsNumber = oldNormalRobotsNumber;

            inventoryCells.clear();

            // reduce the special robots quantities
            if (hasToReduceSpecialRobotsQuantities) {

                for (int i = 0; i < totalRobotCount - MAX_RESISTANT_ROBOTS_FOR_LEVEL; i++) {
                    int index = obtainRandomIntBetween(1, robotQuantities.length - 1);
                    robotQuantities[index]--;
                    normalRobotsNumber -= specialRobotNormalRobotReductionFactor * robotQuantities[index];
                }
            }

            totalRobotCount = 0;

            // prepare the robot cells
            for (int i = 1; i < robotClasses.length; i++) {
                if (robotQuantities[i] > 0) {

                    // if a robot class is unlocked and needed for the level, add it in the inventory
                    if (allowedRobotClasses.contains(robotClasses[i])) {

                        // reduce a quantity if it's beyond the acceptable maximum
                        if (robotQuantities[i] > maxRobotQuantities[i]) {
                            normalRobotsNumber -= (int) (Math.random() * specialRobotNormalRobotReductionFactor * (robotQuantities[i] - maxRobotQuantities[i]));
                            robotQuantities[i] = maxRobotQuantities[i];
                        }

                        inventoryCells.add(new LevelInventoryCell(robotClasses[i], robotQuantities[i]));
                        totalRobotCount += robotQuantities[i];
                    }

                    // otherwise compensate it discarding a number of normal robots proportional
                    // to the number of robots of the non-allowed class that would have been used
                    else {
                        normalRobotsNumber -= specialRobotNormalRobotReductionFactor * robotQuantities[i];
                    }
                }
            }

            hasToReduceSpecialRobotsQuantities = totalRobotCount > MAX_RESISTANT_ROBOTS_FOR_LEVEL;
        }
        //if there are more than 4 special robots, reduce their quantities
        while (hasToReduceSpecialRobotsQuantities && inventoryIterations <= maxInventoryIterations);


        // if reduction has all exceeded the possible iterations without being resolved, return an error code
        if (hasToReduceSpecialRobotsQuantities) {
            return ERROR_CODE;
        }

        // if no robots were assigned yet, at least impose a small presence of normal robots
        if (totalRobotCount == 0) {
            normalRobotsNumber = obtainRandomIntBetween(1, 3);
        }

        // add the normals robots in the end, since its number depends on the other robot types quantities that were just found
        if (normalRobotsNumber > 0) {
            if (normalRobotsNumber > maxRobotQuantities[0]) {
                normalRobotsNumber = maxRobotQuantities[0];
            }
            inventoryCells.add(0, new LevelInventoryCell(Robot.class, normalRobotsNumber));
            totalRobotCount += normalRobotsNumber;
        }


        // determine, with a certain probability, whether to add special robots that are not from the resistant types
        int possibleExtraRobotsNumber = totalRobotCount - normalRobotsNumber - MAX_RESISTANT_ROBOTS_FOR_LEVEL + 1;
        if (possibleExtraRobotsNumber > 0) {
            ArrayList<Class> extraClasses = new ArrayList<>();
            extraClasses.add(Robot_Explosive.class);
            extraClasses.add(Robot_Deactivator_Radio.class);
            for (int i = 0; i < possibleExtraRobotsNumber; i++) {

                if (extraClasses.size() == 0) break;

                // work against a certain probability
                if (Math.random() <= EXTRA_ROBOT_PROBABILITY) {

                    // randomly pick one of the extra classes
                    Class extraClass = extraClasses.get(obtainRandomIntBetween(0, extraClasses.size() - 1));

                    // if the special robot class has been unlocked, use it in the level
                    if (allowedRobotClasses.contains(extraClass)) {

                        inventoryCells.add(new LevelInventoryCell(extraClass, 1));
                        totalRobotCount++;

                        // do not repeat this extra special type for this level
                        extraClasses.remove(extraClass);
                    }
                }
            }
        }

        // actually add the cells to the inventory
        for (LevelInventoryCell cell : inventoryCells) {
            level.getInventory().addCell(cell);
        }

        return normalRobotsNumber;
    }


    /**
     * Adjusts the level so it becomes accurate with the expected difficulty, and modifies it until it is valid
     *
     * @param level                          level to adjust
     * @param normalRobotsNumber             total number of normal robots
     * @param nonAttackedSquaresWantedNumber desired number of non-attacked squares
     * @param squaresWithBlocks              array of squares with blocks
     * @param context                        application context
     * @param difficulty                     level expected difficulty
     * @param allowedRobotClasses            unlocked robot classes
     * @param allowedTrapClasses             unlocked trap classes
     * @param absoluteStartTime              absolute time in milliseconds in which the generation process started
     * @return the resulting level, which not necessarily is the same as the parameter one
     */
    private static Level adjustAndValidateLevel(Level level, int normalRobotsNumber, int nonAttackedSquaresWantedNumber,
                                                ArrayList<LevelSquare> squaresWithBlocks, Context context, int difficulty,
                                                ArrayList<Class> allowedRobotClasses,
                                                ArrayList<Class> allowedTrapClasses, long absoluteStartTime) {
        // if there are too many safe squares, fill the level with some extra blocks
        int outerIteration = 0;
        int maxOuterIteration = 3;
        while (normalRobotsNumber + obtainRandomIntBetween(2, 4) < nonAttackedSquaresWantedNumber &&
                outerIteration <= maxOuterIteration) {
            for (int i = 0; i < obtainRandomIntBetween(0, 2); i++) {
                boolean isValid = false;
                int innerIteration = 0;
                int maxInnerIterations = 3;
                // place blocks carefully, so they do not block any trap nor make the level unsolvable
                while (!isValid && innerIteration <= maxInnerIterations) {
                    LevelSquare square = level.randomlyObtainNonOccupiedSquare();
                    if (square != null) {
                        square.installObject(new Block());
                        level.establishTrapsAttackingEachSquare();
                        isValid = level.obtainSquaresOfTrapablesThatCannotAttackAnySquare().size() == 0 &&
                                LevelSolutionFinder.doesLevelHaveAValidSolution(level);
                        if (!isValid) {
                            square.uninstallObject();
                        }
                    }
                    innerIteration++;
                }
            }
            outerIteration++;
        }

        // if level does not have a solution, try again reducing the number of robots
        LevelInventoryCell normalRobotsCell = level.getInventory().getAtFromClassName(Robot.class.getSimpleName());
        int validationInterations = 0;
        int maxValidationIterations = 20;
        boolean isValid = LevelSolutionFinder.doesLevelHaveAValidSolution(level);
        if (normalRobotsCell != null) {
            while (!isValid && validationInterations <= maxValidationIterations
                    && System.currentTimeMillis() - absoluteStartTime < MAXIMUM_MILLISECONDS_ACCEPTED_FOR_GENERATION) {

                // in the exceptional case in which the normal robots number goes down to 0 and no solution is found, exit to repeat the generation process again
                if (normalRobotsCell.getTotalRobotAmount() == 0) {
                    break;
                }

                // try reducing the number of robots and removing some blocks
                else if (normalRobotsCell.getTotalRobotAmount() > 3) {
                    normalRobotsCell.dcrTotalRobotAmount();
                    for (int i = 0; i <= 2; i++) {
                        squaresWithBlocks.get(obtainRandomIntBetween(0, squaresWithBlocks.size() - 1)).uninstallObject();
                    }
                }

                validationInterations++;
                isValid = LevelSolutionFinder.doesLevelHaveAValidSolution(level);
            }
        }

        return (isValid) ? level : null;
    }

    /**
     * Calculates, for a given difficulty, the number of non-attacked squares that should be in a level
     *
     * @param difficulty difficulty to consider
     * @return the number of non-attacked squares
     */
    private static int calculateNumberOfNonAttackedSquares(int difficulty) {

        int min = MAX_TOTALLY_SAFE_SQUARES_FOR_LEVEL - obtainRandomIntBetween(difficulty, difficulty + 3);
        if (min < MIN_TOTALLY_SAFE_SQUARES_FOR_LEVEL)
            min = obtainRandomIntBetween(MIN_TOTALLY_SAFE_SQUARES_FOR_LEVEL, MIN_TOTALLY_SAFE_SQUARES_FOR_LEVEL + 2);

        int max = MAX_TOTALLY_SAFE_SQUARES_FOR_LEVEL - obtainRandomIntBetween(difficulty, difficulty + 1);

        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }

        return obtainRandomIntBetween(min, max);
    }


    /**
     * Randomly create and return a trap-like object
     *
     * @param allowedTrapClasses classes to which the trap-like object can be
     * @return the created object
     */
    private static Trapable randomlyCreateTrapable(ArrayList<Class> allowedTrapClasses) {

        int index = obtainRandomIntBetween(0, allowedTrapClasses.size() - 1);

        Class trapableClass = allowedTrapClasses.get(index);

        Orientation orientation = obtainRandomOrientationForTrapClass(trapableClass);

        return (Trapable) createPlaceableObject(trapableClass, orientation);
    }


    /**
     * Randomly creates an orientation object
     *
     * @param trapableClass trap-like class to find the orientation for
     * @return the created orientation object
     */
    private static Orientation obtainRandomOrientationForTrapClass(Class trapableClass) {

        Orientation.EOrientationValue orientationValue = Orientation.EOrientationValue.NEUTRAL;

        // check whether traps allows orientations other than neutral
        boolean trapClassAllowsNonNeutralOrientation = !trapableClass.getSimpleName().equals(Trap_FlameThrower.class.getSimpleName()) &&
                !trapableClass.getSimpleName().equals(Trap_ElectricCircle.class.getSimpleName()) &&
                !trapableClass.getSimpleName().equals(Trap_QuadrupleLaserCannon.class.getSimpleName());

        // randomly find an orientation value between all the ones accepted for traps
        if (trapClassAllowsNonNeutralOrientation) {

            Orientation.EOrientationValue trapOrientationValues[] = {Orientation.EOrientationValue.UP, Orientation.EOrientationValue.DOWN,
                    Orientation.EOrientationValue.RIGHT, Orientation.EOrientationValue.LEFT};

            int index = obtainRandomIntBetween(0, trapOrientationValues.length - 1);

            orientationValue = trapOrientationValues[index];
        }

        return new Orientation(orientationValue);
    }


    /**
     * Generates and returns a level whose data is extracted from the passed file
     *
     * @param context  action context
     * @param filename name of the file to generate the level from
     * @return the generated level
     */
    public static Level generateLevelFromFile(Context context, String filename) {

        // create an empty level
        Level level = new Level();

        // fill the level with file data
        FileHandler.fillLevelFromFile(context, level, filename);

        return level;
    }


    /**
     * Create a PlaceableObject from a Class of type PlaceableObjet
     *
     * @param objectClass objectClass
     * @param orientation orientation
     * @return the generated PlaceableObject
     */
    public static PlaceableObject createPlaceableObject(Class objectClass, Orientation orientation) {

        PlaceableObject object = null;

        if (objectClass != null) {

            Class[] paramTypes = {};
            Object[] params = {};

            // no placeable object receives constructor parameters, except traps,
            // that have an orientation value
            if (Trap.class.isAssignableFrom(objectClass)) {
                paramTypes = new Class[]{Orientation.class};
                params = new Object[]{orientation};
            }

            // get a reflection constructor to create an object of the class
            try {
                Constructor<?> constructor = ( (Class<?>) objectClass).getConstructor(paramTypes);
                object = (PlaceableObject) constructor.newInstance(params);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }
}



